package me.monoto.towerdefense.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.monoto.towerdefense.tower.TowerClass;
import me.monoto.towerdefense.utils.WorldEditManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class UpgradeTowerMenu {
    private static final int guiSize = 6;

    public static void initialise(Player player, TowerClass tower, Location location) {
        Gui gui = Gui.gui(GuiType.CHEST).title(Component.text(tower.getType() + " " + tower.getTier())).rows(guiSize).create();

        gui.setDefaultClickAction(event -> event.setCancelled(true));
        populateMenu(gui, player, tower, location);

        gui.open(player);
    }

    private static void populateMenu(Gui gui, Player player, TowerClass tower, Location location) {
        gui.setItem(guiSize, 5, MenuUtils.getCloseButton());
        if (tower.getPlayer() == player) {
            gui.setItem(6, 9, removeTowerButton(gui, location, tower));
        }

    }

    private static GuiItem removeTowerButton(Gui gui, Location location, TowerClass tower) {
        return ItemBuilder.from(Material.BARRIER).asGuiItem(event -> {
            boolean towerCanBeRemoved = WorldEditManager.removeTower(location);
            Player player = (Player) event.getWhoClicked();
            if (towerCanBeRemoved) {
                player.sendMessage("You have sold a(n) " + tower.getType());
            } else {
                player.sendMessage(Component.text().content("Failed to remove " + tower.getType()).color(NamedTextColor.RED).build());
            }
            gui.close(event.getWhoClicked());
        });
    }
}
