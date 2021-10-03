/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.utils;

import org.bukkit.Location;

public class LocationUtils {
    
    public static String simplifyLocation(Location loc) {
        return loc.getWorld().getEnvironment().toString()+" "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ();
    }
    
}
