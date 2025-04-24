package me.devkevin.practice.profile.task;

import lombok.RequiredArgsConstructor;
import me.devkevin.practice.Practice;
import me.devkevin.practice.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

/**
 * Copyright 09/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@RequiredArgsConstructor
public class ProfileSaveDataTask implements Runnable {

    private final Practice plugin = Practice.getInstance();
    private static final ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        int amount = 0;

        for (Profile profile : this.plugin.getProfileManager().getAllData()) {
            this.plugin.getProfileManager().saveData(profile);
            ++amount;
        }

        console.sendMessage("saved " + amount + " players databases");
    }
}
