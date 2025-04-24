package me.devkevin.practice.match.menu;

import me.devkevin.practice.Practice;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright 09/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class MatchDetailSnapshot {

    private final Practice plugin = Practice.getInstance();

    private final Map<UUID, MatchDetailsMenu> snapshots = new HashMap<>();

    public void addSnapshot(MatchDetailsMenu snapshot) {
        this.snapshots.put(snapshot.getSnapshotId(), snapshot);
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> MatchDetailSnapshot.this.removeSnapshot(snapshot.getSnapshotId()), 3200L);
    }

    public void removeSnapshot(UUID snapshotId) {
        MatchDetailsMenu snapshot = this.snapshots.get(snapshotId);
        if (snapshot != null) {
            this.snapshots.remove(snapshotId);
        }
    }

    public MatchDetailsMenu getSnapshot(UUID snapshotId) {
        return this.snapshots.get(snapshotId);
    }

}
