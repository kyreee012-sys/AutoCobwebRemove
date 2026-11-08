package pl.example.autocobwebremove;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AutoCobwebRemove extends JavaPlugin implements Listener {

    private static final long WAIT_TICKS = 15L * 20L;
    private static final int ANIMATION_TICKS = 20;
    private final Map<BlockKey, BukkitTask> pending = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("AutoCobwebRemove enabled.");
    }

    @Override
    public void onDisable() {
        for (BukkitTask task : pending.values()) {
            task.cancel();
        }
        pending.clear();
    }

    @EventHandler
    public void onCobwebPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.COBWEB) {
            return;
        }

        Block block = event.getBlockPlaced();
        BlockKey key = BlockKey.of(block);

        BukkitTask previous = pending.remove(key);
        if (previous != null) {
            previous.cancel();
        }

        Bukkit.getScheduler().runTaskLater(this, () -> {
            Block current = getBlock(key);
            if (current == null || current.getType() != Material.COBWEB) {
                pending.remove(key);
                return;
            }
            startBreakingAnimation(key);
        }, WAIT_TICKS);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        BlockKey key = BlockKey.of(event.getBlock());
        BukkitTask task = pending.remove(key);
        if (task != null) {
            task.cancel();
        }
    }

    private void startBreakingAnimation(BlockKey key) {
        final int[] tick = {0};

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(this, () -> {
            Block block = getBlock(key);
            if (block == null || block.getType() != Material.COBWEB) {
                clearCracks(key);
                BukkitTask current = pending.remove(key);
                if (current != null) current.cancel();
                return;
            }

            float progress = Math.min(0.99f, tick[0] / (float) ANIMATION_TICKS);
            sendCracks(key, progress);
            tick[0]++;

            if (tick[0] > ANIMATION_TICKS) {
                clearCracks(key);
                block.setType(Material.AIR, false);
                BukkitTask current = pending.remove(key);
                if (current != null) current.cancel();
            }
        }, 0L, 1L);

        pending.put(key, task);
    }

    private void sendCracks(BlockKey key, float progress) {
        Location location = key.location();
        World world = location.getWorld();
        if (world == null) return;

        for (Player player : world.getPlayers()) {
            if (player.getLocation().distanceSquared(location) <= 64 * 64) {
                player.sendBlockDamage(location, progress);
            }
        }
    }

    private void clearCracks(BlockKey key) {
        Location location = key.location();
        World world = location.getWorld();
        if (world == null) return;

        for (Player player : world.getPlayers()) {
            player.sendBlockDamage(location, -1.0f);
        }
    }

    private Block getBlock(BlockKey key) {
        World world = Bukkit.getWorld(key.worldId());
        if (world == null) return null;
        return world.getBlockAt(key.x(), key.y(), key.z());
    }

    private record BlockKey(UUID worldId, int x, int y, int z) {
        static BlockKey of(Block block) {
            return new BlockKey(
                    block.getWorld().getUID(),
                    block.getX(),
                    block.getY(),
                    block.getZ()
            );
        }

        Location location() {
            World world = Bukkit.getWorld(worldId);
            return world == null ? new Location(Bukkit.getWorlds().get(0), x, y, z)
                    : new Location(world, x, y, z);
        }
    }
}
