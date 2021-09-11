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
import xyz.nsgw.nsys.storage.objects.Profile;
import xyz.nsgw.nsys.utils.ArithmeticUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LoadingListener implements Listener {

    private final NSys pl;

    private final HashMap<String, Chat> lastChats;

    public LoadingListener(final NSys plugin) {
        pl = plugin;
        lastChats = new HashMap<>();
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        Profile profile = NSys.sql().wrapProfile(e.getPlayer().getUniqueId());
        NSys.sql().validateProfile(profile);
        lastChats.put(e.getPlayer().getName(),new Chat(""));
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        Profile profile = NSys.sql().wrapProfile(e.getPlayer().getUniqueId());
        NSys.sql().invalidateProfile(profile);
        lastChats.remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onChat(final AsyncChatEvent e) {
        Profile profile = NSys.sql().wrapProfileIfLoaded(e.getPlayer().getUniqueId());
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
        Chat last = lastChats.get(e.getPlayer().getName());
        if (ArithmeticUtils.secondsSince(last.date) < NSys.sh().gen().getProperty(GeneralSettings.CHAT_MESSAGE_SPEED_SECS)) {
            if (last.spam == NSys.sh().gen().getProperty(GeneralSettings.CHAT_MESSAGE_SPEED_LIMIT)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED+ "Slow down!");
                return;
            } else {
                last.spam++;
            }
        } else if (last.spam> 1) {
            last.spam = 1;
        }
        if(last.dupes == NSys.sh().gen().getProperty(GeneralSettings.CHAT_MESSAGE_REPEAT_LIMIT)) {
            if (ArithmeticUtils.secondsSince(last.date) < NSys.sh().gen().getProperty(GeneralSettings.CHAT_MESSAGE_REPEAT_COOLDOWN) && e.originalMessage().toString().equals(last.msg)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "Stop spamming!");
                return;
            } else if (!e.originalMessage().toString().equals(last.msg)) {
                last.dupes = 1;
            }
        }else if(e.originalMessage().toString().equals(last.msg)) {
            last.dupes++;
        } else if(last.dupes > 1) {
            last.dupes = 1;
        }
        last.msg = e.originalMessage().toString();
        last.date = new Date();
        lastChats.put(e.getPlayer().getName(),last);
    }
}
class Chat {
    public Date date;
    public String msg;
    public int spam=1, dupes=1;
    public Chat(String msg) {date = new Date();this.msg=msg;}
}
