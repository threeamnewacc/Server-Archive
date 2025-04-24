// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.managers;

import java.util.Collection;
import org.bukkit.configuration.ConfigurationSection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import club.minemen.practice.kit.Kit;
import java.util.Iterator;
import org.bukkit.configuration.file.FileConfiguration;
import club.minemen.core.util.CustomLocation;
import java.util.HashMap;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.UUID;
import club.minemen.practice.arena.StandaloneArena;
import club.minemen.practice.arena.Arena;
import java.util.Map;
import club.minemen.core.util.Config;
import club.minemen.practice.Practice;

public class ArenaManager
{
    private final Practice plugin;
    private final Config config;
    private final Map<String, Arena> arenas;
    private final Map<StandaloneArena, UUID> arenaMatchUUIDs;
    private int generatingArenaRunnables;
    
    public ArenaManager() {
        this.plugin = Practice.getInstance();
        this.config = new Config("arenas", (JavaPlugin)this.plugin);
        this.arenas = new HashMap<String, Arena>();
        this.arenaMatchUUIDs = new HashMap<StandaloneArena, UUID>();
        this.loadArenas();
    }
    
    private void loadArenas() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        club/minemen/practice/managers/ArenaManager.config:Lclub/minemen/core/util/Config;
        //     4: invokevirtual   club/minemen/core/util/Config.getConfig:()Lorg/bukkit/configuration/file/FileConfiguration;
        //     7: astore_1        /* fileConfig */
        //     8: aload_1         /* fileConfig */
        //     9: ldc             "arenas"
        //    11: invokevirtual   org/bukkit/configuration/file/FileConfiguration.getConfigurationSection:(Ljava/lang/String;)Lorg/bukkit/configuration/ConfigurationSection;
        //    14: astore_2        /* arenaSection */
        //    15: aload_2         /* arenaSection */
        //    16: ifnonnull       20
        //    19: return         
        //    20: aload_2         /* arenaSection */
        //    21: iconst_0       
        //    22: invokeinterface org/bukkit/configuration/ConfigurationSection.getKeys:(Z)Ljava/util/Set;
        //    27: aload_0         /* this */
        //    28: aload_2         /* arenaSection */
        //    29: invokedynamic   BootstrapMethod #0, accept:(Lclub/minemen/practice/managers/ArenaManager;Lorg/bukkit/configuration/ConfigurationSection;)Ljava/util/function/Consumer;
        //    34: invokeinterface java/util/Set.forEach:(Ljava/util/function/Consumer;)V
        //    39: return         
        //    StackMapTable: 00 01 FD 00 14 07 00 6A 07 00 6B
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Could not infer any expression.
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:382)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:344)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void saveArenas() {
        final FileConfiguration fileConfig = this.config.getConfig();
        fileConfig.set("arenas", (Object)null);
        this.arenas.forEach((arenaName, arena) -> {
            final String a = CustomLocation.locationToString(arena.getA());
            final String b = CustomLocation.locationToString(arena.getB());
            final String min = CustomLocation.locationToString(arena.getMin());
            final String max = CustomLocation.locationToString(arena.getMax());
            final String arenaRoot = "arenas." + arenaName;
            fileConfig.set(arenaRoot + ".a", (Object)a);
            fileConfig.set(arenaRoot + ".b", (Object)b);
            fileConfig.set(arenaRoot + ".min", (Object)min);
            fileConfig.set(arenaRoot + ".max", (Object)max);
            fileConfig.set(arenaRoot + ".enabled", (Object)arena.isEnabled());
            fileConfig.set(arenaRoot + ".standaloneArenas", (Object)null);
            int i = 0;
            if (arena.getStandaloneArenas() != null) {
                arena.getStandaloneArenas().iterator();
                final Iterator iterator;
                while (iterator.hasNext()) {
                    final StandaloneArena saArena = iterator.next();
                    final String saA = CustomLocation.locationToString(saArena.getA());
                    final String saB = CustomLocation.locationToString(saArena.getB());
                    final String saMin = CustomLocation.locationToString(saArena.getMin());
                    final String saMax = CustomLocation.locationToString(saArena.getMax());
                    final String standAloneRoot = arenaRoot + ".standaloneArenas." + i;
                    fileConfig.set(standAloneRoot + ".a", (Object)saA);
                    fileConfig.set(standAloneRoot + ".b", (Object)saB);
                    fileConfig.set(standAloneRoot + ".min", (Object)saMin);
                    fileConfig.set(standAloneRoot + ".max", (Object)saMax);
                    ++i;
                }
            }
            return;
        });
        this.config.save();
    }
    
    public void createArena(final String name) {
        this.arenas.put(name, new Arena(name));
    }
    
    public void deleteArena(final String name) {
        this.arenas.remove(name);
    }
    
    public Arena getArena(final String name) {
        return this.arenas.get(name);
    }
    
    public Arena getRandomArena(final Kit kit) {
        final List<Arena> enabledArenas = new ArrayList<Arena>();
        for (final Arena arena : this.arenas.values()) {
            if (!arena.isEnabled()) {
                continue;
            }
            if (kit.getExcludedArenas().contains(arena.getName())) {
                continue;
            }
            if (kit.getArenaWhiteList().size() > 0 && !kit.getArenaWhiteList().contains(arena.getName())) {
                continue;
            }
            enabledArenas.add(arena);
        }
        if (enabledArenas.size() == 0) {
            return null;
        }
        return enabledArenas.get(ThreadLocalRandom.current().nextInt(enabledArenas.size()));
    }
    
    public void removeArenaMatchUUID(final StandaloneArena arena) {
        this.arenaMatchUUIDs.remove(arena);
    }
    
    public UUID getArenaMatchUUID(final StandaloneArena arena) {
        return this.arenaMatchUUIDs.get(arena);
    }
    
    public void setArenaMatchUUID(final StandaloneArena arena, final UUID matchUUID) {
        this.arenaMatchUUIDs.put(arena, matchUUID);
    }
    
    public Map<String, Arena> getArenas() {
        return this.arenas;
    }
    
    public Map<StandaloneArena, UUID> getArenaMatchUUIDs() {
        return this.arenaMatchUUIDs;
    }
    
    public int getGeneratingArenaRunnables() {
        return this.generatingArenaRunnables;
    }
    
    public void setGeneratingArenaRunnables(final int generatingArenaRunnables) {
        this.generatingArenaRunnables = generatingArenaRunnables;
    }
}
