package me.devkevin.practice.util;

import org.bukkit.Location;

import java.math.BigDecimal;

/**
 * Copyright 02/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class MathUtil {

    public static String convertTicksToMinutes(int ticks) {
        long minute = (long) ticks / 1200L;
        long second = (long) ticks / 20L - (minute * 60L);

        String secondString = Math.round(second) + "";

        if (second < 10) {
            secondString = 0 + secondString;
        }
        String minuteString = Math.round(minute) + "";

        if (minute == 0) {
            minuteString = 0 + "";
        }

        return minuteString + ":" + secondString;
    }

    public static String convertToRomanNumeral(int number) {
        switch (number) {
            case 1:
                return "I";
            case 2:
                return "II";
        }

        return null;
    }

    public static double roundToHalves(double d) {
        return Math.round(d * 2.0D) / 2.0D;
    }

    public static double roundInt(int numberA, int numberB, int i) {
        return numberA == 0 || numberB == 0 ? 0.0 : ((double)numberA / (double)numberB + 1.0 - (double)((int)((double)numberA / (double)numberB)) == 1.0 ? (double)numberA / (double)numberB : BigDecimal.valueOf((double)numberA / (double)numberB).setScale(i, 4).doubleValue());
    }

    public static Location getMiddle(Location a, Location b){

        double x = (a.getBlockX() + b.getBlockX()) /2;
        double y = a.getBlockY();
        double z = (a.getBlockZ() + b.getBlockZ()) /2;

        return new Location(a.getWorld(), x,y,z);
    }
}
