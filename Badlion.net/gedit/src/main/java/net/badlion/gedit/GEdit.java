package net.badlion.gedit;

import net.badlion.gedit.commands.*;
import net.badlion.gedit.listeners.AxeListener;
import net.badlion.gedit.sessions.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class GEdit extends JavaPlugin {

	public static final String PREFIX = ChatColor.LIGHT_PURPLE + "[" + ChatColor.GREEN + "GEdit" + ChatColor.LIGHT_PURPLE + "] ";

    private static GEdit plugin;

    public static File schematics;

    @Override
    public void onEnable() {
        GEdit.plugin = this;

        Bukkit.getPluginManager().registerEvents(new AxeListener(),this);

        getCommand("gcopy").setExecutor(new Copy());
        getCommand("gpaste").setExecutor(new Paste());
        getCommand("ghollowpaste").setExecutor(new PasteHollow());
        getCommand("gpos1").setExecutor(new PositionOne());
        getCommand("gpos2").setExecutor(new PositionTwo());
        getCommand("gundo").setExecutor(new Undo());
        getCommand("gstack").setExecutor(new Stack());
        getCommand("gwand").setExecutor(new Wand());
        getCommand("gset").setExecutor(new Set());
        getCommand("gsave").setExecutor(new SaveCommand());
        getCommand("gload").setExecutor(new LoadCommand());
        getCommand("gblockid").setExecutor(new BlockIDCommand());
        getCommand("gschematic").setExecutor(new SchematicCommand());
        schematics = new File(plugin.getDataFolder(), "schematics/");

        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
            schematics.mkdir();
        }
    }

    @Override
    public void onDisable() {

    }

    public static GEdit getInstance() {
        return GEdit.plugin;
    }

}

