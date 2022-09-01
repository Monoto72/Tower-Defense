package me.monoto.towerdefense.utils;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Objects;

public class DependencyManager {

    Plugin plugin;

    private Economy econ = null;

    public DependencyManager(Plugin main) {
        this.plugin = main;
        setUpDependencies();
    }

    private void setUpDependencies() {
        PluginDescriptionFile pdf = this.plugin.getDescription();
        pdf.getDepend().forEach(dependency -> {
            if (Objects.equals(dependency, "Vault")) {
                if (!setupEconomy()) {
                    Bukkit.getLogger().severe(dependency + " disabled! Could not set-up economy.");
                    this.plugin.getServer().getPluginManager().disablePlugin(plugin);
                }
            }
        });
    }

    public boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        this.econ = rsp.getProvider();
        return true;
    }

    public Economy getEconomy() {
        return this.econ;
    }
}
