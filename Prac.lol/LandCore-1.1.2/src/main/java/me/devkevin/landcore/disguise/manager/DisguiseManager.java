package me.devkevin.landcore.disguise.manager;

import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.nametag.impl.InternalNametag;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import net.minecraft.server.v1_8_R3.WorldSettings;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.haoshoku.nick.NickPlugin;
import xyz.haoshoku.nick.api.NickAPI;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/02/2023 @ 18:53
 * DisguiseManager / me.devkevin.landcore.disguise / LandCore
 */
@Getter
public class DisguiseManager {
    public static HashMap<Player, String> nickData = new HashMap<>();

    private static final List<String> skinNames = Arrays.asList("_rsu:Killer", "E_Girl:E-girl", "MHF_Sheep:Sheep Man", "BackupDancer:L", "Arro:Arabian", "bennyknight:Badman", "MHF_Herobrine:Herobrine",
            "MHF_Villager:Villager", "CocoDeMedellin:Black Goku", "SheAGoldDigger:Blue Goku", "MHF_Enderman:Enderman", "Marcel:Marcel", "Zwergoor:Emoji",
            "HikakinGames:Youtuber", "loudoggydog3010:Juice Wrld", "Reinstallation:Rambo Chicken", "SolluxCaptor:Nezuko", "DevKevin:DevKevin", "Brandy:Brandy",
            "Grief:Grief", "Nimble:Nimble", "fangirl:fangirl", "Pokemon:Pokemon", "icy:icy", "Magista80:Magista80");


    private static final List<String> shortWords = Arrays.asList("About", "Active", "Admit", "Advise", "Again", "After", "Agent", "Alive", "Alone", "Beach", "Basket", "Basic", "Bath", "Battle",
            "Bean", "Beat", "Bed", "Become", "Begin", "Before", "Beer", "Behind", "Blade", "Black", "Blue", "Bomb", "Brush", "Build", "Bunch", "Button", "Biz", "Busy",
            "Box", "Boy", "Break", "Best", "Better", "Cake", "Camera", "Campus", "Cap", "Card", "Care", "Case", "Catch", "Center", "Chain", "Chair", "Chara", "Charge",
            "Chase", "Cheap", "Cheese", "Check", "Close", "Choose", "Christ", "Circle", "Dad", "Dance", "Dark", "Data", "Dead", "Defend", "Desert", "Desk", "Device",
            "Detect", "Dinner", "Direct", "Dirt", "Dirty", "Doctor", "Down", "Drama", "Draw", "Dream", "Drop", "Earth", "Eat", "Easy", "Editor", "Effect", "Eight",
            "Elect", "Effort", "Emote", "Enter", "Engine", "Enemy", "Empty", "Entry", "Error", "Enough", "Every", "Exact", "Eye", "Expert", "Face", "Fact", "Fade",
            "Fail", "Family", "Famous", "Farmer", "Father", "Fight", "Find", "Finger", "Fire", "First", "Fit", "Fix", "Fish", "Field", "Floor", "Focus", "Fly",
            "Forest", "Force", "Frame", "Uber");

    private static final List<String> longWords = Arrays.asList("Actually", "Aircraft", "Backbone", "Blooming", "Brightly", "Building", "Camellia", "Cardinal", "Careless", "Chemical", "Cheerful",
            "Civilian", "Daughter", "Demolish", "Detector", "Disaster", "Disposal", "Electron", "Elective", "Engaging", "Enormous", "Erection", "Evidence", "Exertion",
            "External", "Faithful", "Familiar", "Favorite", "Fearless", "Fixation", "Fragment", "Generous", "Grateful", "Grievous", "Hydrogen", "Horrible", "Ignorant",
            "Industry", "Majority", "Military", "Mountain", "Mythical", "Normally", "Numerous", "Organism", "Overview", "Pacifist", "Pentagon", "Perilous", "Physical",
            "Precious", "Prestige", "Puzzling", "Railroad", "Reckless");

    private static final List<String> conjuctions = Arrays.asList("The", "Da", "And", "Of", "By", "Is", "El", "Its", "MC", "GANGMEMBER", "xXx", "_", "__");

    private static final List<String> onlyNames = Arrays.asList("Ibirawyr", "Niniel", "Celahan", "Gwysien", "Figovudd", "Zathiel", "Adwiawyth", "Nydinia", "Laraeb", "Eowendasa", "Grendakin",
            "Werradia", "Cauth", "Umigolian", "Tardond", "Dwearia", "Yeiwyn", "Adraclya", "Zaev", "Thabeth", "Chuven", "Zaredon", "Bob", "Robert", "Johnny", "Joy",
            "Matthew", "Michael", "Jacob", "Joshua", "Daniel", "Christopher", "Andrew", "Ethan", "Joseph", "William", "Anthony", "David", "Alexander", "Nicholas",
            "Ryan", "Tyler", "James", "John", "Jonathan", "Noah", "Brandon", "Christian", "Dylan", "Samuel", "Benjamin", "Nathan", "Zachary", "Logan", "Justin",
            "Gabriel", "Emily", "Madison", "Emma", "Olivia", "Hannah", "Abigail", "Isabella", "Samantha", "Elizabeth", "Ashley", "Alexis", "Sarah", "Sophia",
            "Alyssa", "Grace", "Ava", "Taylor", "Brianna", "Lauren", "Chloe", "Natalie", "Kayla", "Jessica", "Anna", "Victoria", "Mia", "Hailey", "Sydney", "Jasmine");

    private static final List<String> japaneseNames = Arrays.asList("Ai", "Aya", "Ayako", "Itsuki", "Eita", "Eiko", "Kanta", "Kaito", "Kenta", "Kento", "Kouki", "Kouta", "Kouya", "Kou",
            "Keito", "Keita", "Saya", "Sayako", "Sara", "Sizuki", "Sizuko", "Sizuno", "Sizuya", "Suzuka", "Suzuki", "Suzuko", "Sumi", "Seiya", "Souta", "Souya",
            "Sou", "Taichi", "Takuya", "Tatsuki", "Chitose", "Tutomu", "Tumuya", "Tetsuya", "Tetsuto", "Tekuto", "Touya", "Tomi", "Nami", "Nao", "Neo", "Notomi",
            "Haruya", "Harumi", "Haruto", "Hitomi", "Hitoshi", "Fuuta", "Fuyuki", "Fuuto", "Mami", "Maya", "Mai", "Masaya", "Masahiro", "Masato", "Misaki", "Mitsuki",
            "Mutsuki", "Mei", "Yae", "Yuuto", "Yuuta", "Yuuya", "Youta", "Youki");


    public static String generate() {
        Random random = new Random();
        return generate(NicknamePattern.values()[random.nextInt(NicknamePattern.values().length)]);
    }

    public static String generateSkin() {
        Random random = new Random();

        return skinNames.get(random.nextInt(skinNames.size()));
    }

    public static String generate(NicknamePattern pattern) {

        String disguiseNickname = null;

        if (pattern.equals(NicknamePattern.NameWithNumbers)) {
            Random random = new Random();
            String name = onlyNames.get(random.nextInt(onlyNames.size()));
            if (name.length() <= 10 && chance(50.0)) {
                disguiseNickname = name + "_" + random.nextInt(9999);
            } else {
                disguiseNickname = name + random.nextInt(9999);
            }
        }

        if (pattern.equals(NicknamePattern.TwoShortsWithConjunction)) {
            pattern = NicknamePattern.JapaneseNameWithBirth;
        }

        if (pattern.equals(NicknamePattern.JapaneseNameWithBirth)) {
            Random random = new Random();
            String name = japaneseNames.get(random.nextInt(japaneseNames.size()));
            String birth = random.nextInt(12) + 1 + String.valueOf(random.nextInt(30) + 1);
            if (chance(50.0)) {
                disguiseNickname = name + "_" + birth;
            } else {
                disguiseNickname = name + birth;
            }
        }

        if (pattern.equals(NicknamePattern.LongWithNumbers)) {
            Random random = new Random();
            String name = longWords.get(random.nextInt(longWords.size()));
            if (chance(50.0)) {
                disguiseNickname = name + "_" + random.nextInt(9999);
            } else {
                disguiseNickname = name + random.nextInt(9999);
            }
        }

        if (pattern.equals(NicknamePattern.ShortWithConjunction)) {
            Random random = new Random();
            String name = conjuctions.get(random.nextInt(conjuctions.size()));
            String secondName = shortWords.get(random.nextInt(shortWords.size()));
            if (chance(50.0)) {
                disguiseNickname = name + "_" + secondName;
            } else {
                disguiseNickname = name + secondName;
            }
        }

        if (pattern.equals(NicknamePattern.ShortAndLong)) {
            Random random = new Random();
            String name = shortWords.get(random.nextInt(shortWords.size()));
            String secondName = longWords.get(random.nextInt(longWords.size()));
            disguiseNickname = name + secondName;
        }

        if (chance(50.0)) {
            assert disguiseNickname != null;
            disguiseNickname = disguiseNickname.toLowerCase();
        }

        return disguiseNickname;
    }

    private static boolean chance(double percent) {
        return Math.random() < percent / 100.0;
    }

    public void setPlayerDisguise(Player player, String disguiseName, String disguiseSkin) {
        LandCore.getInstance().getStaffManager().messageStaff(Rank.TRIAL_MOD, CC.GRAY + "[Staff] " +
                LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() +
                player.getName() + CC.GRAY + " has disguise as " + CC.PRIMARY + disguiseName
        );

        NickAPI.nick(player, disguiseName);
        NickAPI.setGameProfileName(player, disguiseName);
        NickAPI.setUniqueId(player, disguiseName);

        NickAPI.setSkin(player, disguiseSkin);
        NickAPI.refreshPlayer(player);


        Bukkit.getScheduler().runTask(LandCore.getInstance(), () -> {
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.hidePlayer(player);
                player1.showPlayer(player);

                final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

                entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
                entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
                entityPlayer.playerConnection.sendPacket(new PacketPlayOutRespawn(
                        entityPlayer.getWorld().worldProvider.getDimension(),
                        entityPlayer.getWorld().worldData.getDifficulty(),
                        entityPlayer.getWorld().worldData.getType(),
                        WorldSettings.EnumGamemode.valueOf(entityPlayer.getBukkitEntity().getGameMode().name())
                ));

                updateTablist();
                updateCache(player);

                player.setDisplayName(player.getName());
                player.setPlayerListName(player.getName());

                InternalNametag.reloadPlayer(player);
                InternalNametag.reloadOthersFor(player);

                NickAPI.refreshPlayer(player);
                NickAPI.refreshPlayerSync(player);
            }
        });

        player.sendMessage(CC.SECONDARY + "You've disguised as " + CC.PRIMARY + disguiseName + CC.GRAY + " (with a random skin)" + CC.SECONDARY + ".");
    }

    public boolean isNameUsed(String[] args, Player player) {
        for (Player p : LandCore.getInstance().getServer().getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(args[0])) {
                player.sendMessage(CC.RED + "That name is already used.");
                return true;
            }
        }

        return false;
    }

    //  Make it check if the name contains a filtered word in between, before or after the text
    boolean isFiltered(String[] args, Player player) {
        List<String> filteredWord = LandCore.getInstance().getConfig().getStringList("FILTERED-WORDS");
        if (filteredWord.contains(args[0].toLowerCase())) {
            player.sendMessage(CC.RED + "You can't use that name because it contains a filtered word in it!");
            return true;
        }

        return false;
    }

    public void updateTablist() {
        final List<EntityPlayer> playerList = new ArrayList<>(net.minecraft.server.v1_8_R3.MinecraftServer.getServer().getPlayerList().players);
        final List<EntityPlayer> finalList = playerList.stream()
                .sorted(Comparator.comparingInt(potPlayer -> -(LandCore.getInstance().getProfileManager().getProfile(potPlayer.getUniqueID()).getDisguiseRank() != null ? LandCore.getInstance().getProfileManager().getProfile(potPlayer.getUniqueID()).getDisguiseRank().getWeight() : LandCore.getInstance().getProfileManager().getProfile(potPlayer.getUniqueID()).getGrant().getRank().getWeight())))
                .collect(Collectors.toList());

        try {
            final Object list = net.minecraft.server.v1_8_R3.MinecraftServer.getServer().getPlayerList().getClass()
                    .getMethod("playerList", ((Class<?>[]) null))
                    .invoke(net.minecraft.server.v1_8_R3.MinecraftServer.getServer().getPlayerList());
            final Class<?> playerListClass = list.getClass().getSuperclass();
            final Field declaredField = playerListClass.getDeclaredField("players");

            declaredField.set(list, finalList);
        } catch (Exception ignored) {
        }
    }

    public void updateCache(Player player) {
        final List<EntityPlayer> playerList = new ArrayList<>(net.minecraft.server.v1_8_R3.MinecraftServer.getServer().getPlayerList().players);
        final EntityPlayer entityPlayer = playerList.stream()
                .filter(entityPlayer1 -> entityPlayer1.getUniqueID().equals(player.getUniqueId()))
                .findFirst().orElse(null);

        playerList.remove(entityPlayer);
        playerList.add(((CraftPlayer) player).getHandle());

        try {
            final Object list = net.minecraft.server.v1_8_R3.MinecraftServer.getServer().getPlayerList().getClass()
                    .getMethod("playerList", ((Class<?>[]) null))
                    .invoke(net.minecraft.server.v1_8_R3.MinecraftServer.getServer().getPlayerList());
            final Class<?> playerListClass = list.getClass().getSuperclass();
            final Field declaredField = playerListClass.getDeclaredField("players");

            declaredField.set(list, playerList);
        } catch (Exception ignored) {
        }
    }

    public enum NicknamePattern {
        ShortAndLong,
        NameWithNumbers,
        LongWithNumbers,
        ShortWithConjunction,
        JapaneseNameWithBirth,
        TwoShortsWithConjunction
    }
}
