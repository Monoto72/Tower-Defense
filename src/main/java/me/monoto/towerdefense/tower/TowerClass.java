package me.monoto.towerdefense.tower;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class TowerClass {

    Plugin plugin;

    private Player player;
    private String id;
    private String type;
    private ProtectedRegion wgRegion;
    private int tier;
    private int maxTier;
    private int path;
    private World world;
    private Location bottomLocation;
    private Location topLocation;
    private boolean timeout;
    private Hologram hologram;

    public TowerClass(Plugin main) {
        this.plugin = main;

        this.tier = 1;
        this.path = 0;
        this.timeout = false;
        this.hologram = null;
    }

    public void setPlayer(Player value) {
        this.player = value;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getID() {
        return this.id;
    }

    public void setID(String value) {
        this.id = value;
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getType() {
        return this.type;
    }

    public void setWgRegion(ProtectedRegion value) {
        this.wgRegion = value;
    }

    public ProtectedRegion getWgRegion() {
        return this.wgRegion;
    }

    public void setTier(int value) {
        this.tier = value;
    }

    public int getTier() {
        return this.tier;
    }

    public void setMaxTier(int value) {
        this.maxTier = value;
    }

    public int getMaxTier() {
        return this.maxTier;
    }

    public void setPath(int value) {
        this.path = value;
    }

    public int getPath() {
        return this.path;
    }

    public void setWorld(World value) {
        this.world = value;
    }

    public World getWorld() {
        return this.world;
    }

    public void setBottomMiddlePoint(Location value) {
        this.bottomLocation = value;
    }

    public Location getBottomMiddlePoint() {
        return this.bottomLocation;
    }

    public void setTopMiddlePoint(Location value) {
        this.topLocation = value;
    }

    public Location getTopMiddlePoint() {
        return this.topLocation;
    }

    public void setTimeout(long value) {
        if (!timeout) {
            this.timeout = true;
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.timeout = false, value);
        }
    }

    public Boolean getTimeout() {
        return this.timeout;
    }

    public void setHologram(Hologram value) {
        this.hologram = value;
    }

    public Hologram getHologram() {
        return this.hologram;
    }
}
