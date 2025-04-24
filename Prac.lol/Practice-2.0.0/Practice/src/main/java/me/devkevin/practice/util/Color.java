package me.devkevin.practice.util;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright 04/06/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class Color {

    public static String translate( String str ){
        return ChatColor.translateAlternateColorCodes('&', str);
    }


    public static List<String> translate(List<String> list){
        return list.stream().map(Color::translate).collect(Collectors.toList());
    }
}
