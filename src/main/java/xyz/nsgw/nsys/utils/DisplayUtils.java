package xyz.nsgw.nsys.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import xyz.nsgw.nsys.storage.objects.Profile;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DisplayUtils {

    public static Component profileMeta(Profile profile, OfflinePlayer human) {
        return txt(rawProfileMeta(profile,human));
    }

    public static List<Component> loreProfileMeta(Profile profile, OfflinePlayer human) {
        List<Component> res = new ArrayList<>();
        for(String ln : rawProfileMeta(profile,human).split("\n"))
            res.add(txt(ln));
        return res;
    }

    public static String rawProfileMeta(Profile profile, OfflinePlayer human) {
        return ChatColor.LIGHT_PURPLE+(human.isOnline()?ChatColor.GREEN+"Online"+(profile.isAfk()?ChatColor.YELLOW+""+ChatColor.BOLD+" (AFK)":""):"Last seen: "+ ChatColor.GREEN+ time(System.currentTimeMillis() - human.getLastSeen())+" ago.") +"\n"+
                ChatColor.LIGHT_PURPLE+"First joined: "+ChatColor.GREEN+ date(human.getFirstPlayed())+"\n"+
                ChatColor.LIGHT_PURPLE+"Total logins: "+ChatColor.GREEN+ profile.getMaxLogins();
    }

    public static String rawAdminProfileMeta(Profile profile, OfflinePlayer human) {
        return rawProfileMeta(profile,human)+"\n"
                +(profile.isMuted()?ChatColor.DARK_RED+"Muted"+(profile.isShadowMute()?" (Shadow).\n":".\n"):ChatColor.DARK_GREEN+"Not muted.\n")
                +(profile.isTrackingTeleports()?ChatColor.GREEN+"Tracking teleports.":ChatColor.RED+"Not tracking teleports.");
    }

    public static Component txt(String txt) {
        return Component.text(txt);
    }

    public static String date(Long date) {
        return Date.from(Instant.ofEpochMilli(date)).toString();
    }

    public static String time(Long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis),
        hours = TimeUnit.MILLISECONDS.toHours(millis) -
                TimeUnit.DAYS.toHours(days),
        minutes = TimeUnit.MILLISECONDS.toMinutes(millis) -
                TimeUnit.HOURS.toMinutes(hours),
        seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(minutes);
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

}
