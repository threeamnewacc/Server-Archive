package me.devkevin.practice.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright 05/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class Animation {

    @Getter
    private static final Map<UUID, Map<String, Animation>> animations = Maps.newHashMap();

    private final List<String> lines = Lists.newArrayList();
    private final String name;
    private final UUID uuid;
    private final long seconds;
    private long current;
    private int index;
    private String lineCurrent;

    public Animation(String name, UUID uuid, int seconds){
        this.name = name;
        this.uuid = uuid;
        this.seconds = seconds * 20L;

        Map<String, Animation> map = Maps.newHashMap();

        map.put(name, this);

        if(!animations.containsKey(uuid)){
            animations.put(uuid, map);
        }else{
            animations.get(uuid).put(name, this);
        }
    }

    public Animation(String name, UUID uuid, long seconds){
        this.name = name;
        this.uuid = uuid;
        this.seconds = seconds;

        Map<String, Animation> map = Maps.newHashMap();

        map.put(name, this);

        if(!animations.containsKey(uuid)){
            animations.put(uuid, map);
        }else{
            animations.get(uuid).put(name, this);
        }
    }

    public String getLine() {
        if (lineCurrent == null || lineCurrent.equals("")){
            return lineCurrent = lines.get(0);
        }
        if (current == seconds) {
            int newIndex = ++index;
            if(newIndex >= lines.size()){
                newIndex = 0;
                index = 0;
            }
            current = 0;
            lineCurrent = lines.get(newIndex);
        }
        current++;
        return lineCurrent;
    }

    public static Animation getAnimation(UUID uuid, String name){
        if (animations.get(uuid) == null){
            return null;
        }
        return animations.get(uuid).get(name);
    }
}
