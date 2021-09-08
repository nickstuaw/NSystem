/*
© Copyright Nick Williams 2021.
Credit should be given to the original author where this code is used.
 */

package xyz.nsgw.nsys.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.config.settings.GeneralSettings;
import xyz.nsgw.nsys.storage.Profile;

public class LoadingListener implements Listener {

    private final NSys pl;

    public LoadingListener(final NSys plugin) {pl = plugin;}

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        Profile profile = pl.sql().wrapProfile(e.getPlayer().getUniqueId());
        pl.sql().validateProfile(profile);
        pl.getLogger().info(e.getPlayer().getName()+" teleport tracking: " + profile.isTrackingTeleports());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        Profile profile = pl.sql().wrapProfileIfLoaded(e.getPlayer().getUniqueId());
        pl.sql().invalidateProfile(profile);
        pl.getLogger().info(e.getPlayer().getName()+" teleport tracking: " + profile.isTrackingTeleports());
    }

    @EventHandler
    public void onTp(final PlayerTeleportEvent e) {
        Profile profile = pl.sql().wrapProfileIfLoaded(e.getPlayer().getUniqueId());
        assert profile != null;
        pl.sql().invalidateProfile(profile);
        if(!profile.isTrackingTeleports()) return;
        String out = "[/!\\] "+ e.getPlayer().getName() +" teleported ";
        switch(pl.getGenSettings().getProperty(GeneralSettings.TRACKTP_PLAYER_MODE)){
            case 1:
                out += "to "+ e.getTo().getWorld().getName() +" "+ e.getTo().getBlockX() +" "+ e.getTo().getBlockY() +" "+ e.getTo().getBlockZ();
                break;
            case 2:
                out += "from "+ e.getFrom().getWorld().getName() +" "+ e.getFrom().getBlockX() +" "+ e.getFrom().getBlockY() +" "+ e.getFrom().getBlockZ();
                break;
            default:
                out += " to "+ e.getTo().getWorld().getName() +" "+ e.getTo().getBlockX() +" "+ e.getTo().getBlockY() +" "+ e.getTo().getBlockZ()
                        +" from "+ e.getFrom().getWorld().getName() +" "+ e.getFrom().getBlockX() +" "+ e.getFrom().getBlockY() +" "+ e.getFrom().getBlockZ();
                break;
        }
        pl.getLogger().info(out);// change this to an alert
    }

    @EventHandler
    public void onChat(final AsyncChatEvent e) {
        Profile profile = pl.sql().wrapProfileIfLoaded(e.getPlayer().getUniqueId());
        if(profile==null) return;
        // muting
        if(profile.isMuted()) {
            if(profile.checkMute()){
                e.setCancelled(true);
                if(!profile.isShadowMute()) {
                    e.getPlayer().sendMessage(ChatColor.RED + "You are muted.");return;
                } else {
                    e.getPlayer().sendMessage(e.message());
                }
            }
        }
        // spamming
        // spamTimeCounter is incremented by 1 when the player sends a message within secondsLimit secs of their last message.
        int secondsLimit = 2;
        if(profile.isSpamTimeCounterLow(5) && profile.getSecsSinceChat() < secondsLimit) {
            if (profile.getSecsSinceChat() < secondsLimit) {
                profile.increaseSpamTimeCounter();
            } else {
                profile.decreaseSpamTimeCounter();
            }
        } else if (profile.getSecsSinceChat() < secondsLimit) {
            e.setCancelled(true);
            //notify mods about the spam attempt once when the counter has reached 4 (this can be the default value) (which is a speed of over 1 message per second)
        }
        if(profile.isDupeMsgCounterLow(5)) {
            if (e.message().toString().equals(profile.getLastChatMsg())) {
                if (profile.getSecsSinceChat() < 5) {
                    profile.increaseDupeMsgCounter();
                    // message match found within 5 seconds of last chat - add a config option
                } else if (profile.getDuplicateMessageCounter() > 0) {
                    profile.resetDupeMsgCounter();
                    // they stopped spamming
                }
            }
        } else {
            e.setCancelled(true);
            //notify mods about the spam attempt when the counter has reached 4
        }
        profile.recordChatAttempt();
    }
}
