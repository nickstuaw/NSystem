package xyz.nsgw.nsys.storage.objects.locations;

import org.bukkit.Location;
import xyz.nsgw.nsys.utils.LocationUtils;

public class Loc extends Location {
    public Loc(Location location) {
        super(location.getWorld(),location.getX(),location.getY(),location.getZ(),location.getYaw(),location.getPitch());
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
    public boolean exists() {
        return this.getWorld()!=null;
    }
}
