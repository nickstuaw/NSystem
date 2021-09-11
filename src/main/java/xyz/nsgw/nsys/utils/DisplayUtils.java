package xyz.nsgw.nsys.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import xyz.nsgw.nsys.storage.objects.Profile;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
        return ChatColor.LIGHT_PURPLE+(human.isOnline()?ChatColor.GREEN+"Online"+(profile.isAfk()?ChatColor.YELLOW+""+ChatColor.BOLD+" (AFK)":""):ChatColor.GREEN+"Last seen: "+ date(human.getLastSeen())) +"\n"+
                ChatColor.LIGHT_PURPLE+"Joined at: "+ date(human.getFirstPlayed())+"\n"+
                ChatColor.LIGHT_PURPLE+"Total logins: "+ profile.getMaxLogins();
    }

    public static Component txt(String txt) {
        return Component.text(txt);
    }

    public static String date(Long date) {
        return Date.from(Instant.ofEpochMilli(date)).toString();
    }

}
