package us.zonix.core.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import us.zonix.core.CorePlugin;
import us.zonix.core.api.callback.AbstractBukkitCallback;
import us.zonix.core.api.request.PlayerRequest;
import us.zonix.core.api.request.PunishmentRequest;
import us.zonix.core.events.RankChangeEvent;
import us.zonix.core.punishment.Punishment;
import us.zonix.core.punishment.PunishmentType;
import us.zonix.core.rank.Rank;
import us.zonix.core.shared.api.callback.Callback;
import us.zonix.core.symbols.Symbol;
import us.zonix.core.util.CC;
import us.zonix.core.util.ItemBuilder;

@Getter
public class Profile {

    private static CorePlugin main = CorePlugin.getInstance();
    @Getter
    private static Map<UUID, Profile> profiles = new HashMap<>();

    private UUID uuid;
    @Setter
    private String name;
    @Setter
    private Long firstLogin;
    @Setter
    private Long lastLogin;
    @Setter
    private String ip;
    private Rank rank = Rank.DEFAULT;
    @Setter
    private Symbol symbol;
    @Setter
    private boolean boughtSymbols;
    @Setter
    private UUID lastMessaged;
    @Setter
    private List<UUID> ignored;
    @Setter
    private String emailAddress;
    @Setter
    private String confirmationId;
    @Setter
    private boolean registered;
    @Setter
    private long lastRegister;
    @Setter
    private long chatCooldown;
    @Setter
    private boolean chatEnabled;
    @Setter
    private String twoFactorAuthentication;
    @Setter
    private boolean authenticated;
    private List<Punishment> punishments;
    private Set<UUID> alts;
    private ProfileOptions options;

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.punishments = new ArrayList<>();
        this.ignored = new ArrayList<>();
        this.alts = new HashSet<>();
        this.options = new ProfileOptions();

        this.loadProfile();
        this.loadPunishments();

        profiles.put(this.uuid, this);
    }

    public void setRank(Rank rank) {
        this.rank = rank;

        Player player = getPlayer();

        if (player != null) {
            new RankChangeEvent(getPlayer(), rank).call();
        }
    }

    public boolean isMuted() {
        for (Punishment punishment : punishments) {
            if (punishment.getType() == PunishmentType.MUTE) {
                if (punishment.isActive()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isBanned() {
        for (Punishment punishment : punishments) {
            if (punishment.getType() == PunishmentType.BAN || punishment.getType() == PunishmentType.TEMPBAN) {
                if (punishment.isActive()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isBlacklisted() {
        for (Punishment punishment : punishments) {
            if (punishment.getType() == PunishmentType.BLACKLIST) {
                if (punishment.isActive()) {
                    return true;
                }
            }
        }

        return false;
    }

    public Punishment getMutedPunishment() {
        for (Punishment punishment : punishments) {
            if (punishment.getType() == PunishmentType.MUTE) {
                if (punishment.isActive()) {
                    return punishment;
                }
            }
        }

        return null;
    }

    public Punishment getBannedPunishment() {
        for (Punishment punishment : punishments) {
            if (punishment.getType() == PunishmentType.BAN || punishment.getType() == PunishmentType.TEMPBAN) {
                if (punishment.isActive()) {
                    return punishment;
                }
            }
        }

        return null;
    }

    public Punishment getBlacklistedPunishment() {
        for (Punishment punishment : punishments) {
            if (punishment.getType() == PunishmentType.BLACKLIST) {
                if (punishment.isActive()) {
                    return punishment;
                }
            }
        }

        return null;
    }

    public List<Punishment> getPunishmentsByType(PunishmentType type) {
        List<Punishment> toReturn = new ArrayList<>();

        for (Punishment punishment : punishments) {
            if (punishment.getType().name().contains(type.name())) {
                toReturn.add(punishment);
            }
        }

        Map<Punishment, Long> toCompare = new HashMap<>();

        for (Punishment punishment : toReturn) {
            toCompare.put(punishment, punishment.getAddedAt());
        }

        toReturn.sort(new Comparator<Punishment>() {
            @Override
            public int compare(Punishment punishment, Punishment comparison) {
                return toCompare.get(comparison).compareTo(toCompare.get(punishment));
            }
        });

        return toReturn;
    }

    private void loadProfile() {
        JsonElement response = main.getRequestProcessor().sendRequest(new PlayerRequest.FetchByUuidRequest(this.uuid));

        if (response.isJsonNull() || response.isJsonPrimitive()) {
            System.out.println("Error while getting JSON response.");
            System.out.println("Issue: " + response.toString());
            return;
        }

        JsonObject data = response.getAsJsonObject();

        this.rank = data.get("rank") instanceof JsonNull ? Rank.DEFAULT : Rank.getRankOrDefault(data.get("rank").getAsString());

        this.symbol = data.get("symbol") instanceof JsonNull ? null : Symbol.getSymbolOrDefault(data.get("symbol").getAsString());
        this.boughtSymbols = !(data.get("boughtSymbols") instanceof JsonNull) && data.get("boughtSymbols").getAsBoolean();

        this.twoFactorAuthentication = data.get("twoFactorAuthentication") instanceof JsonNull ? null : data.get("twoFactorAuthentication").getAsString();
        this.authenticated = !(data.get("authenticated") instanceof JsonNull) && data.get("authenticated").getAsBoolean();

        this.firstLogin = data.get("firstLogin").getAsLong();

        if (this.lastLogin == null) {
            this.lastLogin = data.get("lastLogin").getAsLong();
        }

        this.ip = data.get("ip") instanceof JsonNull ? null : data.get("ip").getAsString();
    }

    public void loadProfileAlts() {
        JsonElement response = main.getRequestProcessor().sendRequest(new PlayerRequest.FetchAltsRequest(this.uuid));

        if (response.isJsonNull() || response.isJsonPrimitive()) {
            System.out.println("Error while getting JSON response.");
            System.out.println("Issue: " + response.toString());
            return;
        }

        JsonArray data = response.getAsJsonArray();

        data.iterator().forEachRemaining((altElement) -> {
            JsonObject element = altElement.getAsJsonObject();
            UUID profileUUID = UUID.fromString(element.get("uuid").getAsString());

            if (!this.uuid.toString().equalsIgnoreCase(profileUUID.toString())) {
                alts.add(profileUUID);
            }
        });
    }

    private void loadPunishments() {
        JsonElement response = main.getRequestProcessor().sendRequest(new PunishmentRequest.FetchByUuidRequest(this.uuid));

        if (response.isJsonNull() || response.isJsonPrimitive()) {
            System.out.println("Error while getting JSON response.");
            System.out.println("Issue: " + response.toString());
            return;
        }

        JsonArray data = response.getAsJsonArray();

        data.iterator().forEachRemaining((punishmentElement) -> {
            JsonObject punishmentObject = punishmentElement.getAsJsonObject();
            punishments.add(Punishment.fromJson(punishmentObject));
        });
    }

    public void save() {
        main.getRequestProcessor().sendRequest(new PlayerRequest.SaveRequest(this.uuid, this.name, this.lastLogin, main.getServerId(), this.ip, this.rank == null ? Rank.DEFAULT : this.rank, this.symbol == null ? Symbol.SYMBOL_0 : this.symbol, this.boughtSymbols, this.twoFactorAuthentication, this.authenticated));
    }

    public static void getPlayerInformation(String name, CommandSender sender, Callback callback) {
        Player player = Bukkit.getPlayer(name);

        if (player != null) {
            JsonObject retrieved = new JsonObject();
            retrieved.addProperty("uuid", player.getUniqueId().toString());
            retrieved.addProperty("name", player.getName());

            callback.callback(retrieved);
        } else {
            main.getRequestProcessor().sendRequestAsync(new PlayerRequest.FetchByNameRequest(name), new AbstractBukkitCallback() {
                @Override
                public void callback(JsonElement element) {
                    if (element == null || element.isJsonNull() || element.isJsonPrimitive()) {
                        callback.callback(null);
                        return;
                    }

                    JsonObject data = element.getAsJsonObject();

                    JsonObject object = new JsonObject();
                    object.addProperty("uuid", data.get("uuid").getAsString());
                    object.addProperty("name", data.get("name").getAsString());

                    callback.callback(object);
                }

                @Override
                public void onError(String message) {
                    callback.callback(null);
                }
            });
        }
    }

    public Player getPlayer() {
        return CorePlugin.getInstance().getServer().getPlayer(this.uuid);
    }

    public static void updateTabList(Player player, Rank rank) {
        player.setPlayerListName(rank.getColor() + player.getName());
    }

    public void updateTabList(Rank rank) {
        this.getPlayer().setPlayerListName(rank.getColor() + this.getPlayer().getName());
    }

    public void setDonatorArmor() {

        Player player = this.getPlayer();

        if (player != null) {
            player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).color(this.rank).enchantment(Enchantment.DURABILITY).build());
            player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).color(this.rank).enchantment(Enchantment.DURABILITY).build());
            player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).color(this.rank).enchantment(Enchantment.DURABILITY).build());
        }
    }

    public static Profile getByUuidIfAvailable(UUID uuid) {
        return profiles.get(uuid);
    }

    public static Profile getByUuid(UUID uuid) {
        Profile profile = profiles.get(uuid);

        if (profile == null) {
            return new Profile(uuid);
        } else {
            return profile;
        }
    }

    public static String getRankColor(UUID uuid) {
        Profile profile = Profile.getByUuidIfAvailable(uuid);

        if (profile == null) {
            return CC.WHITE;
        } else {
            return profile.getRank().getColor();
        }
    }

}
