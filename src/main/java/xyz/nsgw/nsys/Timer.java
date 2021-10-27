/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.config.settings.GeneralSettings;
import xyz.nsgw.nsys.storage.objects.Profile;

public class Timer implements Runnable {

    @Override
    public void run() {

        for(Player player : Bukkit.getOnlinePlayers()) {
            Profile p = NSys.sql().wrapProfileIfLoaded(player);
            if(p!=null) checkActivity(p);
        }

    }

    private void checkActivity(Profile p) {
        if(!p.isAfk() && p.getLastActive().getTime() + (NSys.sh().gen().getProperty(GeneralSettings.MINIMUM_AFK_TIME) * 1000) < System.currentTimeMillis()) {
            p.setAfk(true, false);
        }
    }
}
