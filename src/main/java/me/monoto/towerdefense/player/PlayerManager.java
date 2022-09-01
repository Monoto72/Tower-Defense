package me.monoto.towerdefense.player;

import me.monoto.towerdefense.tower.TowerClass;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
    public static HashMap<UUID, PlayerClass> playerManager = new HashMap<>();

    public static HashMap<UUID, PlayerClass> getPlayerManager() {
        return playerManager;
    }
}
