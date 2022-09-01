package me.monoto.towerdefense.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public class MenuUtils {
    public static GuiItem getCloseButton() {
        return ItemBuilder.from(Material.BOOK).name(Component.text("Close Menu").color(NamedTextColor.GOLD)).asGuiItem();
    }
}
