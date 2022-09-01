package me.monoto.towerdefense.tower;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.monoto.towerdefense.utils.WorldEditManager;
import me.monoto.towerdefense.utils.WorldGuardUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class TowerManager {

    static Plugin plugin;
    static ArrayList<TowerClass> towersPlaced = new ArrayList<>();

    public TowerManager(Plugin main) {
        plugin = main;
    }

    public static void createTower(Player player, String type, String id, ProtectedRegion wgRegion, Location botLocation, Location topLocation, int maxTier) {
        TowerClass tower = new TowerClass(plugin);

        tower.setPlayer(player);
        tower.setType(type);
        tower.setID(id);
        tower.setWgRegion(wgRegion);
        tower.setMaxTier(maxTier);
        tower.setBottomMiddlePoint(botLocation);
        tower.setTopMiddlePoint(topLocation);
        tower.setWorld(botLocation.getWorld());
        tower.setTimeout(20L);

        towersPlaced.add(tower);
    }

    public static void upgradeTower(ProtectedRegion wgRegion, Player player) {
        TowerManager.getTowersPlaced().forEach(tower -> {
            if (wgRegion.equals(tower.getWgRegion())) {
                DefaultDomain domain = wgRegion.getOwners();
                if (domain.getPlayerDomain().contains(player.getUniqueId())) {
                    if (tower.getTier() < tower.getMaxTier() && !tower.getTimeout()) {
                        tower.setTier(tower.getTier() + 1);
                        tower.setTimeout(20L);
                        tower.setWgRegion(wgRegion);

                        WorldEditManager.getSchematic(false, tower.getType(), tower.getTier(), tower.getWorld(), tower.getBottomMiddlePoint(), tower.getPlayer());
                        createHologram(tower);
                    }
                } else {
                    player.sendMessage("Question. Why are you upgrading the opposing teams towers?");
                }
            }
        });
    }

    public static boolean checkIfCanBePlaced(Location location, int size) {
        int newSizeNeg = size == 3 ? -1 : -2;
        int newSizePos = size == 3 ? 1 : 2;

        for (int x = newSizeNeg; x <= newSizePos; x++) {
            for (int z = newSizeNeg; z <= newSizePos; z++) {
                Block block = new Location(location.getWorld(),
                        location.getBlockX() + x,
                        location.getBlockY() + 1,
                        location.getBlockZ() + z
                ).getBlock();

                if (block.getType() != Material.AIR) {
                    // deniedArea(location);
                    System.out.println("denied");
                    return false;
                }
            }
        }
        return true;
    }

    private static void deniedArea(Location location) {
        AtomicInteger count = new AtomicInteger();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            switchBlocksOut(location);

            if (count.get() < 3) {
                Bukkit.getScheduler().cancelTasks(plugin);
            }

            count.getAndIncrement();

        }, 20L, 20L * 5);
    }

    private static void switchBlocksOut(Location location) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Block block = new Location(location.getWorld(),
                        location.getBlockX() + x,
                        location.getBlockY() + 1,
                        location.getBlockZ() + z
                ).getBlock();

                if (block.getType() != Material.AIR) {
                    if (x % 2 == 0) {
                        block.setType(Material.RED_WOOL);
                    } else {
                        block.setType(Material.WHITE_WOOL);
                    }
                }
            }
        }
    }

    public static void unstuckPlayer(Player player) {
        TowerManager.getTowersPlaced().forEach(tower -> {
            ProtectedRegion protectedRegion = WorldGuardUtil.getRegionManager(tower.getWorld()).getRegion(tower.getID());
            if (protectedRegion != null) {
                if (protectedRegion.equals(tower.getWgRegion())) {

                    int bottom = tower.getBottomMiddlePoint().getBlockY() + 1;
                    int top = tower.getTopMiddlePoint().getBlockY() + 1;

                    for (int y = top; y > bottom; y--) {
                        Location location = new Location(tower.getWorld(),
                                tower.getBottomMiddlePoint().getBlockX(),
                                y,
                                tower.getTopMiddlePoint().getBlockZ()
                        );

                        Block blockCheck = location.getBlock();
                        Block blockAbove = blockCheck.getRelative(BlockFace.UP, 1);

                        if (!blockCheck.getType().isAir() && blockAbove.getType().isAir()) {
                            Location teleportLocation = new Location(tower.getWorld(),
                                    tower.getBottomMiddlePoint().getBlockX(),
                                    y + 1,
                                    tower.getTopMiddlePoint().getBlockZ()
                            ).setDirection(player.getLocation().getDirection()).add(0.5, 0, 0.5);

                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                player.teleport(teleportLocation);
                            });
                            return;
                        }
                    }
                }
            }
        });
    }

    public static void createHologram(TowerClass tower) {
        System.out.println(tower.getID());
        System.out.println(tower.getHologram());

        int bottom = tower.getBottomMiddlePoint().getBlockY() + 1;
        int top = tower.getTopMiddlePoint().getBlockY() + 1;

        for (int y = top; y > bottom; y--) {
            Location location = new Location(tower.getWorld(),
                    tower.getBottomMiddlePoint().getBlockX(),
                    y,
                    tower.getTopMiddlePoint().getBlockZ()
            );

            Block blockCheck = location.getBlock();
            Block blockAbove = blockCheck.getRelative(BlockFace.UP, 1);

            if (!blockCheck.getType().isAir() && blockAbove.getType().isAir()) {
            Component component = Component.text(tower.getType() + " " + tower.getTier())
                    .color(NamedTextColor.GREEN)
                    .decoration(TextDecoration.ITALIC, false);

                Hologram hologram;

                if (tower.getHologram() == null) {
                    hologram = HologramsAPI.createHologram(plugin, location.add(0.5, 2, 0.5));
                    hologram.appendTextLine(LegacyComponentSerializer.legacySection().serialize(component));
                } else {
                    hologram = tower.getHologram();

                    hologram.insertTextLine(1, LegacyComponentSerializer.legacySection().serialize(component));
                    hologram.teleport(location.add(0.5, 2, 0.5));
                }
                tower.setHologram(hologram);

                return;
            }
        }
    }

    public static TowerClass getTower(Location location) {
        for (TowerClass tower : getTowersPlaced()) {
            if (location.equals(tower.getBottomMiddlePoint())) {
                return tower;
            }
        }
        return null;
    }

    public static ArrayList<TowerClass> getTowersPlaced() {
        return towersPlaced;
    }

}



