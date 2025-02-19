package us.thezircon.play.autopickup.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.utils.PickupObjective;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemSpawnEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    ///////////////////////////////////// Custom items \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawn(ItemSpawnEvent e) {
        boolean doBlacklist = PLUGIN.getBlacklistConf().getBoolean("doBlacklisted");
        List<String> blacklist = PLUGIN.getBlacklistConf().getStringList("Blacklisted");

        if (AutoPickup.worldsBlacklist!=null && AutoPickup.worldsBlacklist.contains(e.getLocation().getWorld().getName())) {
            return;
        }

        // Ignores items dropped by players
        UUID uuid = e.getEntity().getUniqueId();
        if (AutoPickup.droppedItems.contains(uuid)) {
            AutoPickup.droppedItems.remove(uuid);
            return;
        }

        if (doBlacklist) { // Checks if blacklist is enabled
            if (blacklist.contains(e.getEntity().getItemStack().getType().toString())) { // Stop resets the loop skipping the item & not removing it
                return;
            }
        }

        Location loc = e.getLocation();
        String key = loc.getBlockX()+";"+loc.getBlockY()+";"+loc.getBlockZ()+";"+loc.getWorld();
        if (AutoPickup.customItemPatch.containsKey(key)) {

//            UUID testUUID = e.getEntity().getThrower();
//            if (testUUID!=null && testUUID.equals(new UUID(0, 0))) {
//                return;
//            }
            PickupObjective po = AutoPickup.customItemPatch.get(key);
            ItemStack item = e.getEntity().getItemStack();
            Player player = po.getPlayer();
            boolean doSmelt = PLUGIN.auto_smelt_blocks.contains(player);
            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(item);
            if (leftOver.keySet().size()<=0) {
                e.getEntity().remove();
            }
//            if (leftOver.keySet().size()>0) {
//                for (ItemStack items : leftOver.values()) {
//                    Item dropped = player.getWorld().dropItemNaturally(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).add(.5,0,.5), items);
//                    dropped.setThrower(new UUID(0, 0));
//                }
//            }
//            if (player.getInventory().firstEmpty()!=-1) {
//                e.getEntity().remove();
//                if (doSmelt) {
//                    player.getInventory().addItem(AutoSmelt.smelt(item, player));
//                } else {
//                    player.getInventory().addItem(item);
//                }
//            }
        }
    }

}
