/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.storage.objects.locations;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.utils.LocationUtils;

public class Loc extends Location {

    private String name;

    public Loc(Location location, final String name) {
        super(location.getWorld(),location.getX(),location.getY(),location.getZ(),location.getYaw(),location.getPitch());
        this.name = name;
    }
    public String simplify() {
        return LocationUtils.simplifyLocation(this);
    }
    public void setLocation(Location loc) {
        this.setWorld(loc.getWorld());
        this.setX(loc.getX());
        this.setY(loc.getY());
        this.setZ(loc.getZ());
        this.setYaw(loc.getYaw());
        this.setPitch(loc.getPitch());
    }
    public void teleport(Player p, boolean loud) {
        p.teleport(this);
        if(loud) p.sendMessage(ChatColor.GREEN+"Teleported!");
    }

    @Override
    public String toString() {
        return "X = " + this.getBlockX() + ", Y = " + this.getBlockY() + ", Z = " + this.getBlockZ()
                + ", World = " + this.getWorld().getName();
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name){
        this.name = name;
    }
}
