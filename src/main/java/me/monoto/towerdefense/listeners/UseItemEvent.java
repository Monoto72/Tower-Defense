package me.monoto.towerdefense.listeners;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.monoto.towerdefense.TowerDefense;
import me.monoto.towerdefense.menus.PlaceTowerMenu;
import me.monoto.towerdefense.menus.UpgradeTowerMenu;
import me.monoto.towerdefense.tower.TowerClass;
import me.monoto.towerdefense.tower.TowerManager;
import me.monoto.towerdefense.utils.WorldGuardUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;

import static org.bukkit.Bukkit.getPluginManager;

public class UseItemEvent implements Listener {

    public UseItemEvent(TowerDefense main){
        getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK
                || event.getHand() == EquipmentSlot.HAND
        ) return;

        Player player = event.getPlayer();

        ApplicableRegionSet set = WorldGuardUtil.getRegionSet(Objects.requireNonNull(event.getClickedBlock()).getLocation());

        for (ProtectedRegion region : set) {
            for (TowerClass tower : TowerManager.getTowersPlaced()) {
                if (region.getPoints().equals(tower.getWgRegion().getPoints())) {
                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.isSneaking()) {
                        TowerManager.upgradeTower(tower.getWgRegion(), player);
                    } else {
                        UpgradeTowerMenu.initialise(player, tower, event.getClickedBlock().getLocation());
                    }
                    return;
                }
            }
        }
        PlaceTowerMenu.initialise(player, Objects.requireNonNull(event.getClickedBlock()).getLocation());
    }
}
