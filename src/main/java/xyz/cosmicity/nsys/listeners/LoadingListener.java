/*
© Copyright Nick Williams 2021.
Credit should be given to the original author where this code is used.
 */

package xyz.cosmicity.nsys.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import xyz.cosmicity.nsys.NSys;
import xyz.cosmicity.nsys.storage.Profile;

public class LoadingListener implements Listener {

    private final NSys pl;

    public LoadingListener(final NSys plugin) {pl = plugin;}

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        Profile profile = pl.sql().wrap(e.getPlayer().getUniqueId());
        pl.sql().validate(profile);
        pl.getLogger().info(e.getPlayer().getName()+" teleport tracking: " + profile.isTrackingTeleports());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        Profile profile = pl.sql().wrapIfLoaded(e.getPlayer().getUniqueId());
        pl.sql().invalidate(profile);
        pl.getLogger().info(e.getPlayer().getName()+" teleport tracking: " + profile.isTrackingTeleports());
    }

    @EventHandler
    public void onTp(final PlayerTeleportEvent e) {
        Profile profile = pl.sql().wrapIfLoaded(e.getPlayer().getUniqueId());
        pl.sql().validate(profile);
        if(!profile.isTrackingTeleports()) return;
        pl.getLogger().info("[/!\\] "+ e.getPlayer().getName() +" teleported" +
                " to "+ e.getTo().getWorld().getName() +" "+ e.getTo().getBlockX() +" "+ e.getTo().getBlockY() +" "+ e.getTo().getBlockZ()
                +" from "+ e.getFrom().getWorld().getName() +" "+ e.getFrom().getBlockX() +" "+ e.getFrom().getBlockY() +" "+ e.getFrom().getBlockZ());
    }

    @EventHandler
    public void onChat(final AsyncChatEvent e) {
        Profile profile = pl.sql().wrapIfLoaded(e.getPlayer().getUniqueId());
        pl.sql().validate(profile);
        if(profile.isMuted()) {
            if(profile.checkMute()){
                e.setCancelled(true);
                if(!profile.isShadowMute()) {
                    if (profile.getSecsSinceChat() < 2)
                        e.getPlayer().sendMessage(ChatColor.RED + "Do not spam.\nYou are muted.");
                    else e.getPlayer().sendMessage(ChatColor.RED + "You are muted.");
                    profile.recordChatAttempt();
                } else {
                    e.getPlayer().sendMessage(e.message());
                }
            }
        }
    }
}
