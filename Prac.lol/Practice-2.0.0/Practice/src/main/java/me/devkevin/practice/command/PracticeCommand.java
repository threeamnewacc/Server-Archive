package me.devkevin.practice.command;

import lombok.Getter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.util.command.CommandArgs;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public abstract class PracticeCommand {

    @Getter private static final Practice plugin = Practice.getInstance();

    public PracticeCommand() {
        plugin.getFramework().registerCommands(this, null);
    }

    public abstract void onCommand(CommandArgs command);
}