package me.monoto.towerdefense;

import me.monoto.towerdefense.listeners.UseItemEvent;
import me.monoto.towerdefense.tower.TowerManager;
import me.monoto.towerdefense.utils.DependencyManager;
import me.monoto.towerdefense.utils.VaultManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TowerDefense extends JavaPlugin {

    public static Economy economy;

    @Override
    public void onEnable() {

        DependencyManager dependencyManager = new DependencyManager(this); // Checks all dependants off the plugin
        economy = dependencyManager.getEconomy();

        getDataFolder().mkdirs();

        // Plugin startup logic
        new UseItemEvent(this);
        new TowerManager(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
