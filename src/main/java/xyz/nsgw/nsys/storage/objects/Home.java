package xyz.nsgw.nsys.storage.objects;

import org.bukkit.Location;
import org.bukkit.World;

public class Home extends Location {
    public Home(World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }
    public Home(Location loc) {
        super(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ(),loc.getYaw(),loc.getPitch());
    }
}
