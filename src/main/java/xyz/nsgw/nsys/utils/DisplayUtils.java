/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import xyz.nsgw.nsys.storage.objects.Profile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisplayUtils {

    public static Component profileMeta(Profile profile, OfflinePlayer human) {
        return txt(rawProfileMeta(profile,human));
    }

    public static List<Component> loreProfileMeta(Profile profile, boolean admin) {
        OfflinePlayer human = Bukkit.getOfflinePlayer(profile.getKey());
        List<Component> res = new ArrayList<>();
        for(String ln :
                (admin ? rawAdminProfileMeta(profile,human) : rawProfileMeta(profile,human)).split("\n"))
            res.add(txt(ln));
        return res;
    }

    public static String rawProfileMeta(Profile profile, OfflinePlayer human) {
        return ChatColor.LIGHT_PURPLE+(profile.isOnline()?ChatColor.GREEN+"Online"+(profile.isAfk()?ChatColor.YELLOW+""+ChatColor.BOLD+" (AFK)":""):"Last seen: "+ ChatColor.GREEN+ timeDiff(human.getLastSeen())+" ago.") +"\n"+
                ChatColor.LIGHT_PURPLE+"First joined: "+ChatColor.GREEN+ date(human.getFirstPlayed())+"\n"+
                ChatColor.LIGHT_PURPLE+"Total logins: "+ChatColor.GREEN+ profile.getMaxLogins();
    }

    public static String rawAdminProfileMeta(Profile profile, OfflinePlayer human) {
        return rawProfileMeta(profile,human)+"\n"
                +(profile.isMuted()?ChatColor.DARK_RED+"Muted"+(profile.isShadowMute()?" (Shadow).\n":".\n"):ChatColor.DARK_GREEN+"Not muted.\n")
                +(profile.isTrackingTeleports()?ChatColor.GREEN+"Tracking teleports.":ChatColor.RED+"Not tracking teleports.");
    }

    public static Component txt(String txt) {
        return MiniMessage.get().parse(ChatColor.YELLOW+txt);
    }

    public static String date(Long date) {
        return Date.from(Instant.ofEpochMilli(date)).toString();
    }

    public static String formatTimeInterval(Long start, Long end) {
        long diff = end - start;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long days = diff / daysInMilli;
        diff = diff % daysInMilli;

        long hours = diff / hoursInMilli;
        diff = diff % hoursInMilli;

        long minutes = diff / minutesInMilli;
        diff = diff % minutesInMilli;

        long seconds = diff / secondsInMilli;

        String res = "";
        if(days > 0) {
            res += days + " day"+(days!=1?"s ":" ");
        }
        if(hours > 0) {
            res += hours + " hour"+(hours!=1?"s ":" ");
        }
        if(minutes > 0) {
            res += minutes + " minute"+(minutes!=1?"s ":" ");
        }
        if(seconds > 0) {
            res += seconds + " second"+(seconds!=1?"s":"");
        }
        return res;
    }

    public static String timeDiff(Long millis) {
        return formatTimeInterval(millis, System.currentTimeMillis());
    }

}
