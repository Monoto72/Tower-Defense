package me.monoto.towerdefense.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import java.util.Objects;

public class WorldGuardUtil {
    public static ApplicableRegionSet getRegionSet(org.bukkit.Location location) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        Location wgLocation = BukkitAdapter.adapt(Objects.requireNonNull(location));
        RegionQuery query = container.createQuery();
        return query.getApplicableRegions(wgLocation);
    }

    public static RegionManager getRegionManager(org.bukkit.World world) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World wgWorld = BukkitAdapter.adapt(Objects.requireNonNull(world));
        return container.get(wgWorld);
    }
}
