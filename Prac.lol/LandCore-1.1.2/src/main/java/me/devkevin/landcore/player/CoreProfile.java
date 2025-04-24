package me.devkevin.landcore.player;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.grant.Grant;
import me.devkevin.landcore.player.notes.Note;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.punishment.Punishment;
import me.devkevin.landcore.punishment.PunishmentType;
import me.devkevin.landcore.storage.database.MongoRequest;
import me.devkevin.landcore.storage.database.MongoStorage;
import me.devkevin.landcore.utils.location.CustomLocation;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.message.Messages;
import me.devkevin.landcore.utils.timer.Timer;
import me.devkevin.landcore.utils.timer.impl.DoubleTimer;
import me.devkevin.landcore.utils.timer.impl.IntegerTimer;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class CoreProfile {
    public static Type GRANT_TYPE = new TypeToken<Grant>() {}.getType();
    public static Type GRANTS_TYPE = new TypeToken<List<Grant>>() {}.getType();
    public static Type LIST_STRING_TYPE = new TypeToken<List<String>>() {}.getType();

    private final List<UUID> ignored = new ArrayList<>();

    private final String name;
    private final UUID id;
    private final Timer commandCooldownTimer = new DoubleTimer(1);
    private final Timer reportCooldownTimer = new IntegerTimer(TimeUnit.SECONDS, 60);
    private Timer chatCooldownTimer;
    private UUID converser;
    private String reportingPlayerName;
    private boolean playingSounds = true;
    private boolean messaging = true;
    private boolean globalChatEnabled = true;
    private boolean inStaffChat;
    private boolean vanished;
    private long lastChatTime;
    private boolean frozen = false;

    private Grant grant;
    private List<Grant> grants = new ArrayList<>();

    private List<Note> notes = new ArrayList<>();

    private double misplace;
    private CustomLocation lastMovePacket;
    private Map<UUID, List<CustomLocation>> recentPlayerPackets = new HashMap<>();

    private Set<String> prefixes, suffix = new HashSet<>();

    private String customPrefix = "";
    private String customSuffix = "";
    private String customColor;
    private Rank disguiseRank;
    private String disguiseName;

    private List<Punishment> punishments = new ArrayList<>();

    private String currentAddress;
    private List<String> ipAddresses = new ArrayList<>();
    private List<UUID> knownAlts = new ArrayList<>();

    private int p_coin;

    @SuppressWarnings("unchecked")
    public CoreProfile(String name, UUID id,  String address) {
        this.name = name;
        this.id = id;
        this.ipAddresses.add(address);

        LandCore.getInstance().getMongoStorage().getOrCreateDocument("players", id, (document, exists) -> {
            if (exists) {
                this.inStaffChat = document.getBoolean("staff_chat_enabled", inStaffChat);
                this.messaging = document.getBoolean("messaging_enabled", messaging);
                this.playingSounds = document.getBoolean("playing_sounds", playingSounds);


                this.customPrefix = document.getString("customPrefix");
                this.customSuffix = document.getString("customSuffix");
                this.customColor = document.getString("customColor");

                this.grant = LandCore.GSON.fromJson(document.getString("grant"), GRANT_TYPE);
                this.grants = LandCore.GSON.fromJson(document.getString("grants"), GRANTS_TYPE);

                this.currentAddress = document.getString("currentAddress");
                this.ipAddresses = LandCore.GSON.fromJson(document.getString("ipAddresses"), LIST_STRING_TYPE);

                this.p_coin = document.getInteger("p_coin", p_coin);

                List<String> permissions = (List<String>) document.get("permissions");

                if (permissions != null) {
                    this.grant.getRank().getPermissions().addAll(permissions);
                }

                List<UUID> ignored = (List<UUID>) document.get("ignored_ids");

                if (ignored != null) {
                    this.ignored.addAll(ignored);
                }

                JsonArray punishments = new JsonParser().parse(document.getString("punishments")).getAsJsonArray();
                for (JsonElement jsonElement : punishments) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    this.punishments.add(Punishment.DESERIALIZER.deserialize(jsonObject));
                }

                if (document.getString("notes") != null) {
                    JsonArray notesArray = new JsonParser().parse(document.getString("notes")).getAsJsonArray();

                    for (JsonElement noteData : notesArray) {
                        // Transform into a note object
                        Note note = Note.DESERIALIZER.deserialize(noteData.getAsJsonObject());

                        if (note != null) {
                            notes.add(note);
                        }
                    }
                }
            } else {
                grant = new Grant(Rank.MEMBER, -1L, System.currentTimeMillis(), "Console", "Profile creation");
            }

            save(true);
        });
    }

    public Punishment getActiveMute() {
        for (Punishment punishment : punishments) {
            if (punishment.getType() == PunishmentType.MUTE && punishment.isActive()) {
                return punishment;
            }
        }

        return null;
    }

    public Punishment getActiveBan() {
        for (Punishment punishment : punishments) {
            if (punishment.getType().isBan() && punishment.isActive()) {
                return punishment;
            }
        }

        return null;
    }

    public int getPunishmentCountByType(PunishmentType type) {
        int i = 0;

        for (Punishment punishment : punishments) {
            if (punishment.getType() == type) i++;
        }

        return i;
    }

    public void save(boolean async) {
        MongoRequest request = MongoRequest.newRequest("players", id)
                .put("name", name)
                .put("staff_chat_enabled", inStaffChat)
                .put("messaging_enabled", messaging)
                .put("playing_sounds", playingSounds)
                .put("ignored_ids", ignored)
                .put("customPrefix", customPrefix)
                .put("customSuffix", customSuffix)
                .put("customColor", customColor)
                .put("currentAddress", currentAddress)
                .put("ipAddresses", LandCore.GSON.toJson(ipAddresses, LIST_STRING_TYPE))
                .put("permissions", this.grant.getRank().getPermissions())
                .put("p_coin", p_coin);

        request.put("grant", LandCore.GSON.toJson(this.grant, GRANT_TYPE));
        request.put("grants", LandCore.GSON.toJson(this.grants, GRANTS_TYPE));

        JsonArray notesArray = new JsonArray();
        for (Note note : notes) {
            notesArray.add(Note.SERIALIZER.serialize(note));
        }
        request.put("notes", notesArray.toString());

        JsonArray punishments = new JsonArray();
        for (Punishment punishment : this.punishments) {
            punishments.add(Punishment.SERIALIZER.serialize(punishment));
        }
        request.put("punishments", punishments.toString());

        if (async) {
            LandCore.getInstance().getServer().getScheduler().runTaskAsynchronously(LandCore.getInstance(), request::run);
        } else {
            request.run();
        }
    }

    public Timer getChatCooldownTimer() {
        if (chatCooldownTimer == null) {
            if (isDonor()) {
                chatCooldownTimer = new DoubleTimer(1);
            } else {
                chatCooldownTimer = new DoubleTimer(3);
            }
        }

        return chatCooldownTimer;
    }

    public String getChatFormat() {
        String rankColor = this.getRank().getColor();
        String color = customColor != null && !customColor.isEmpty() && hasRank(Rank.BASIC) ? customColor : rankColor;
        String prefix = getGrant().getRank().getRawFormat().equals("") ? "" : customPrefix + " ";
        String suffix = getGrant().getRank().getName().equals(" ") ? " " : "" + customSuffix;

        return String.format(prefix + this.getRank().getRawFormat()) + color + name + suffix;
    }

    public void updateLastChatTime() {
        lastChatTime = System.currentTimeMillis();
    }

    public boolean hasRank(Rank requiredRank) {
        return grant.getRank().ordinal() >= requiredRank.ordinal();
    }

    public boolean hasStaff() {
        return hasRank(Rank.HOST);
    }

    public boolean hasDonor() {
        return hasRank(Rank.BASIC);
    }

    public boolean isDonor() {
        return grant.getRank() == Rank.BASIC;
    }

    public void ignore(UUID id) {
        ignored.add(id);
    }

    public void unignore(UUID id) {
        ignored.remove(id);
    }

    public boolean hasPlayerIgnored(UUID id) {
        return ignored.contains(id);
    }

    public Player getPlayer() {
        return LandCore.getInstance().getServer().getPlayer(this.id);
    }

    public boolean freeze(CommandSender sender) {
        frozen = !frozen;

        if (frozen) {
            getPlayer().setWalkSpeed(0.0F);
            getPlayer().setFlySpeed(0.0F);
            getPlayer().setFoodLevel(0);
            getPlayer().setSprinting(false);
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
            Arrays.stream(Messages.FROZEN_MESSAGE).forEach(message -> Messages.sendCenteredMessage(getPlayer(), message));
            sender.sendMessage(CC.RED + "Player frozen.");
            return true;
        }
        getPlayer().setWalkSpeed(0.2f);
        getPlayer().setFlySpeed(0.0001f);
        getPlayer().setFoodLevel(20);
        getPlayer().setSprinting(true);
        getPlayer().removePotionEffect(PotionEffectType.JUMP);
        getPlayer().sendMessage(CC.SECONDARY + "You have been unfrozen.");
        sender.sendMessage(CC.RED + getPlayer().getName() + " has been unfrozen.");
        return false;
    }

    public Rank getRank() {
        return this.getGrant() != null ? this.getGrant().getRank() : Rank.MEMBER;
    }

    public Grant getGrant() {
        if (this.grant.hasExpired()) {
            return new Grant(Rank.MEMBER, -1L, System.currentTimeMillis(), "Console", "Profile creation");
        }
        return this.grant;
    }

    public boolean removeNote(int id) {
        Note note = notes.stream().filter(note1 -> note1.getId() == id).findFirst().orElse(null);
        return notes.remove(note);
    }

    public Note getNote(int id) {
        return notes.stream().filter(note1 -> note1.getId() == id).findFirst().orElse(null);
    }

    public void addPlayerPacket(final UUID playerUUID, final CustomLocation customLocation) {
        List<CustomLocation> customLocations = this.recentPlayerPackets.get(playerUUID);
        if (customLocations == null) {
            customLocations = new ArrayList<>();
        }
        if (customLocations.size() == 20) {
            customLocations.remove(0);
        }
        customLocations.add(customLocation);
        this.recentPlayerPackets.put(playerUUID, customLocations);
    }

    public CustomLocation getLastPlayerPacket(final UUID playerUUID, final int index) {
        final List<CustomLocation> customLocations = this.recentPlayerPackets.get(playerUUID);
        if (customLocations != null && customLocations.size() > index) {
            return customLocations.get(customLocations.size() - index);
        }
        return null;
    }

    public static List<CoreProfile> getByIpAddress(String ipAddress) {
        List<CoreProfile> profiles = new ArrayList<>();

        MongoStorage storage = LandCore.getInstance().getMongoStorage();

        storage.getDocumentsByFilter("players", Filters.eq("currentAddress", ipAddress)).forEach((Block<Document>) document ->
                profiles.add(new CoreProfile(document.getString("username"), UUID.fromString(document.getString("uuid")), document.getString("currentAddress"))));

        return profiles;
    }
}
