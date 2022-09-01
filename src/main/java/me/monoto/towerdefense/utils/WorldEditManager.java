package me.monoto.towerdefense.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.util.WorldEditRegionConverter;
import me.monoto.towerdefense.TowerDefense;
import me.monoto.towerdefense.tower.TowerClass;
import me.monoto.towerdefense.tower.TowerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

public class WorldEditManager {

    public static Plugin plugin = TowerDefense.getPlugin(TowerDefense.class);

    public static void getSchematic(boolean newTower, String type, int tier, World world, Location location, Player player) {
        File towersDir = new File(plugin.getDataFolder().getAbsolutePath() + "/towers/" + type.replace(' ', '-').toLowerCase(Locale.ROOT) + "/");
        File[] towerFiles = towersDir.listFiles();

        Location newLocation = location.clone();

        if (towerFiles != null) {
            if (tier > towerFiles.length) return;
            ClipboardFormat format = ClipboardFormats.findByFile(towerFiles[tier-1]);
            if (format != null) {
                try {
                    ClipboardReader reader = format.getReader(new FileInputStream(towerFiles[tier-1]));
                    Clipboard clipboard = reader.read();

                    com.sk89q.worldedit.world.World adapterWorld = BukkitAdapter.adapt(world);
                    EditSession editSession = WorldEdit.getInstance().newEditSession(adapterWorld);

                    Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(
                            BlockVector3.at(
                                    location.getBlockX(),
                                    location.getBlockY() + 1,
                                    location.getBlockZ()
                            )
                    ).build();

                    String towerID = "Tower-" + TowerManager.getTowersPlaced().size();

                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    com.sk89q.worldedit.world.World wgLocation = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));

                    if (newTower) {
                        ProtectedCuboidRegion region = new ProtectedCuboidRegion(
                                towerID,
                                BlockVector3.at(location.getBlockX() + clipboard.getRegion().getLength() / 2, location.getBlockY() + 1, location.getBlockZ() + clipboard.getRegion().getWidth() / 2),
                                BlockVector3.at(location.getBlockX() - clipboard.getRegion().getLength() / 2, location.getBlockY() + clipboard.getRegion().getHeight(), location.getBlockZ() - clipboard.getRegion().getWidth() / 2)
                        );

                        DefaultDomain owners = new DefaultDomain();

                        owners.addPlayer(player.getUniqueId());
                        region.setOwners(owners);

                        Objects.requireNonNull(container.get(wgLocation)).addRegion(region);

                        Location towerBottomBlock = new Location(newLocation.getWorld(), newLocation.getBlockX(), newLocation.getBlockY(), newLocation.getBlockZ());
                        Location towerTopBlock = new Location(newLocation.getWorld(), newLocation.getBlockX(), newLocation.getBlockY() + clipboard.getRegion().getHeight(), newLocation.getBlockZ());

                        TowerManager.createTower(player, type, towerID, region, towerBottomBlock, towerTopBlock, towerFiles.length);
                        stuckCheck(region);

                        TowerClass newTowerClass = TowerManager.getTower(towerBottomBlock);
                        if (newTowerClass != null) TowerManager.createHologram(newTowerClass);
                    } else {
                        TowerClass tower = TowerManager.getTower(newLocation);

                        if (tower != null) {
                            RegionManager regionManager = WorldGuardUtil.getRegionManager(location.getWorld());
                            ProtectedRegion towerRegion = regionManager.getRegion(tower.getID());

                            if (towerRegion != null) {
                                ProtectedCuboidRegion region = new ProtectedCuboidRegion(
                                        tower.getID(),
                                        BlockVector3.at(location.getBlockX() + clipboard.getRegion().getLength() / 2, location.getBlockY() + 1, location.getBlockZ() + clipboard.getRegion().getWidth() / 2),
                                        BlockVector3.at(location.getBlockX() - clipboard.getRegion().getLength() / 2, location.getBlockY() + clipboard.getRegion().getHeight(), location.getBlockZ() - clipboard.getRegion().getWidth() / 2)
                                );

                                region.copyFrom(tower.getWgRegion());
                                Objects.requireNonNull(container.get(wgLocation)).addRegion(region);
                                TowerManager.upgradeTower(region, player);
                                System.out.println(region.getPoints());
                                stuckCheck(region);
                            }
                        }

                    }

                    try {
                        Operations.complete(operation);
                        editSession.close();
                        player.playSound(location, Sound.BLOCK_ANVIL_USE, 1f, 1f);

                    } catch (WorldEditException exception) {
                        exception.printStackTrace();
                    }

                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public static boolean removeTower(Location location) {
        ApplicableRegionSet set = WorldGuardUtil.getRegionSet(Objects.requireNonNull(location));

        for (ProtectedRegion region : set) {
            for (TowerClass tower : TowerManager.getTowersPlaced()) {
                if (region.getPoints().equals(tower.getWgRegion().getPoints())) {
                    com.sk89q.worldedit.world.World adapterWorld = BukkitAdapter.adapt(tower.getWorld());
                    EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(adapterWorld).maxBlocks(-1).build();

                    BlockType type = BlockTypes.AIR;

                    if (type != null) {
                        BlockState state = type.getDefaultState();
                        BaseBlock block = state.toBaseBlock();

                        try {
                            editSession.setBlocks(WorldEditRegionConverter.convertToRegion(region), block);
                            editSession.close();

                            RegionManager regionManager = WorldGuardUtil.getRegionManager(location.getWorld());
                            regionManager.removeRegion(tower.getID());

                            if (tower.getHologram() != null) {
                                tower.getHologram().delete();
                                tower.setHologram(null);
                            }
                            return true;
                        } catch (MaxChangedBlocksException exception) {
                            exception.printStackTrace();
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void stuckCheck(ProtectedRegion region) { // Needs rework for tower#updateTower()
        for (Player playerInRegion : Bukkit.getOnlinePlayers()) {

            com.sk89q.worldedit.util.Location location = BukkitAdapter.adapt(playerInRegion.getLocation());
            if (region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> TowerManager.unstuckPlayer(playerInRegion));
            }
        }
    }
}
