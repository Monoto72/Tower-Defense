package me.monoto.towerdefense.listeners;

import me.monoto.towerdefense.TowerDefense;
import me.monoto.towerdefense.player.PlayerClass;
import me.monoto.towerdefense.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static org.bukkit.Bukkit.getPluginManager;

public class JoinEvent implements Listener {

    public JoinEvent(TowerDefense main) {
        getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (PlayerManager.getPlayerManager().get(player.getUniqueId()) == null) {
            PlayerManager.getPlayerManager().put(player.getUniqueId(), new PlayerClass(player));
        }
    }
}
