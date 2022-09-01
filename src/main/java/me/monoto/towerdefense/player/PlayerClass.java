package me.monoto.towerdefense.player;

import org.bukkit.entity.Player;

public class PlayerClass {

    private Player player;

    public PlayerClass (Player p) {
        this.player = p;
    }

    public void setPlayer(Player value) {
        this.player = value;
    }

    public Player getPlayer() {
        return this.player;
    }

    // More stuffs
}
