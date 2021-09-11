package xyz.nsgw.nsys.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.config.settings.GeneralSettings;
import xyz.nsgw.nsys.storage.objects.Profile;

public class TpListener implements Listener {

    @EventHandler
    public void onTp(final PlayerTeleportEvent e) {
        Profile profile = NSys.sql().wrapProfile(e.getPlayer().getUniqueId());
        profile.updateActivity();
        NSys.sql().validateProfile(profile);
        if(!profile.isTrackingTeleports()) return;
        String out = "[/!\\] "+ e.getPlayer().getName() +" teleported ";
        switch(NSys.sh().gen().getProperty(GeneralSettings.TRACKTP_PLAYER_MODE)){
            case 1:
                out += "to "+ e.getTo().getWorld().getName() +" "+ e.getTo().getBlockX() +" "+ e.getTo().getBlockY() +" "+ e.getTo().getBlockZ();
                break;
            case 2:
                out += "from "+ e.getFrom().getWorld().getName() +" "+ e.getFrom().getBlockX() +" "+ e.getFrom().getBlockY() +" "+ e.getFrom().getBlockZ();
                break;
            default:
                out += "to "+ e.getTo().getWorld().getName() +" "+ e.getTo().getBlockX() +" "+ e.getTo().getBlockY() +" "+ e.getTo().getBlockZ()
                        +" from "+ e.getFrom().getWorld().getName() +" "+ e.getFrom().getBlockX() +" "+ e.getFrom().getBlockY() +" "+ e.getFrom().getBlockZ();
                break;
        }
        NSys.log().info(out);// change this to an alert
    }
}
