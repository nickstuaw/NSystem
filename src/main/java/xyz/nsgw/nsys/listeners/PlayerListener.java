package xyz.nsgw.nsys.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.config.settings.GeneralSettings;
import xyz.nsgw.nsys.storage.objects.Profile;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        if(e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ() && e.getFrom().getY() == e.getTo().getY()) return;

        if(!NSys.sh().gen().getProperty(GeneralSettings.CANCEL_AFK_ON_MOVE)) {
            e.getHandlers().unregister(this);
            NSys.log().info("Unregistered move listener as it's not needed.");
        }

        Profile p = NSys.sql().wrapProfileIfLoaded(e.getPlayer().getUniqueId());
        if(p!=null) p.updateActivity();
    }

}
