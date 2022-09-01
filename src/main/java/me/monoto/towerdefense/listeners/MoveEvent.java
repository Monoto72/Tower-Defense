package me.monoto.towerdefense.listeners;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.monoto.towerdefense.TowerDefense;
import me.monoto.towerdefense.tower.TowerManager;
import me.monoto.towerdefense.utils.WorldGuardUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.RayTraceResult;


import java.util.Objects;

import static org.bukkit.Bukkit.getPluginManager;

public class MoveEvent implements Listener {

    public MoveEvent(TowerDefense main){
        getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void on(PlayerMoveEvent event) { // Client side visuals
        int distance = 7;
        Player player = event.getPlayer();

        RayTraceResult rayTraceBlocks = player.rayTraceBlocks(distance);
        if (rayTraceBlocks != null) {
            Block rayTraceBlock = rayTraceBlocks.getHitBlock();
            if (rayTraceBlock != null) {
                Location blockLocation = rayTraceBlock.getLocation();
                ApplicableRegionSet set = WorldGuardUtil.getRegionSet(blockLocation);

                for (ProtectedRegion region : set) {
                    if (region.contains(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ())) {
                        System.out.println(region.getId());
                        TowerManager.getTowersPlaced().forEach(tower -> {
                            System.out.println(tower.getID() + "  dsad sa");
                            if (Objects.equals(tower.getID(), region.getId())) {
                                System.out.println("fuck you");
                                System.out.println(region.getId()); // broke
                            }
                        });
                    }
                }
            }
        }
    }
}
