package me.monoto.towerdefense.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.monoto.towerdefense.tower.TowerManager;
import me.monoto.towerdefense.utils.WorldEditManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Arrays;

public class PlaceTowerMenu {
    private static final int guiSize = 5;

    public static void initialise(Player player, Location location) {
        Gui gui = Gui.gui(GuiType.CHEST).title(Component.text("Tower Menu")).rows(guiSize).create();
        populateMenu(gui, location, player);

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        gui.open(player);
    }

    private static void populateMenu(Gui gui, Location location, Player player) {
        ArrayList<String> towerName = new ArrayList<>(Arrays.asList("Archer Tower", "Ice Tower", "Mage Tower", "Artillery Tower", "Sorcerer Tower", "Zeus Tower", "Quake Tower", "Poison Tower", "Turret Tower", "Leach Tower", "Necromancer Tower"));
        ArrayList<Integer> towerSize = new ArrayList<>(Arrays.asList(3, 3, 3, 3, 3, 3, 3, 3, 5, 5, 5));
        ArrayList<Material> materialType = new ArrayList<>(Arrays.asList(Material.BOW, Material.SNOW_BLOCK, Material.DRAGON_EGG, Material.TNT, Material.ENDER_EYE, Material.BEACON, Material.DIRT, Material.POTION, Material.PISTON, Material.NETHER_STAR, Material.GHAST_SPAWN_EGG));
        ArrayList<Integer> placementSlot = new ArrayList<>(Arrays.asList(2, 3, 5, 6, 11, 12, 14, 15, 21, 22, 23));

        for (int index = 0; index < towerName.size(); index++) {
            int finalIndex = index;
            GuiItem item = ItemBuilder.from(materialType.get(index)).flags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS)
                    .name(Component.text().content(towerName.get(index))
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false)
                            .build()
                    ).asGuiItem(event -> {
                        boolean placeable = TowerManager.checkIfCanBePlaced(location, towerSize.get(finalIndex));

                        if (placeable) {
                            WorldEditManager.getSchematic(
                                    true,
                                    towerName.get(finalIndex),
                                    1,
                                    location.getWorld(),
                                    location,
                                    (Player) event.getWhoClicked()
                            );
                        } else {
                            event.getWhoClicked().sendMessage("Could not place a tower there.");
                        }
                        gui.close(player);
                    });

            if (placementSlot.get(index) == 2) {
                item.getItemStack().addEnchantment(Enchantment.ARROW_DAMAGE, 1);
            }

            gui.setItem(placementSlot.get(index), item);
        }
        gui.setItem(guiSize, 5, MenuUtils.getCloseButton());
    }

}
