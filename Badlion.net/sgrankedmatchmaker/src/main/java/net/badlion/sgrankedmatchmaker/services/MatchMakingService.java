package net.badlion.sgrankedmatchmaker.services;

import java.util.UUID;

public interface MatchMakingService {

    public void add(UUID uuid);

    public void addToTopPriority(UUID uuid);

    public void increasePriority(UUID uuid);

    public Integer remove(UUID uuid);

}
