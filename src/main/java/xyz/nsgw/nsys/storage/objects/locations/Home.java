/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.storage.objects.locations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Home extends Loc {
    public Home(Location location, String name) {
        super(location, name);
    }

    @Override
    public void teleport(Player p, boolean loud) {
        super.teleport(p, loud);
        Bukkit.getLogger().info(p.getName() + " teleported to a home.");
    }
}
