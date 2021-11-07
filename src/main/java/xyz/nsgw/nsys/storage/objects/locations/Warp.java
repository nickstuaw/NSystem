/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.storage.objects.locations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.config.settings.GeneralSettings;
import xyz.nsgw.nsys.storage.sql.DbData;
import xyz.nsgw.nsys.storage.sql.SQLTable;
import xyz.nsgw.nsys.storage.sql.SQLUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Warp extends Loc {

    private UUID ownerUuid;
    private boolean trackingTeleports;

    private boolean forSale;

    private double price = 0;

    public Warp(final String name, final Location location, final UUID uuid) {
        super(location, name);
        ownerUuid = uuid;
    }
    public Warp(final String name) {
        super(Bukkit.getServer().getWorlds().get(0).getSpawnLocation(), name);
        this.forSale = false;
    }

    public boolean exists() {
        return this.ownerUuid!=null;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }
    public void setOwnerUuid(UUID u) {
        ownerUuid = u;
    }

    public boolean isTrackingTeleports() {
        return trackingTeleports;
    }
    public void setTrackingTeleports(boolean b) {
        trackingTeleports = b;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isForSale() {
        return this.forSale;
    }

    public void setForSale(boolean forSale) {
        this.forSale = forSale;
    }

    public Warp loadAttributes(final SQLTable table) {
        List<Object> row = SQLUtils.getRow(table, "\""+getName()+"\"", Arrays.stream(DbData.WARP_COLUMNS).map(c->c[0]).collect(Collectors.toList()).toArray(String[]::new));
        setOwnerUuid(UUID.fromString((String) row.get(0)));
        setTrackingTeleports((Boolean) row.get(1));
        setLocation(SQLUtils.stringToLocation((String) row.get(2)));
        setPrice(row.get(3) == null ? NSys.sh().gen().getProperty(GeneralSettings.PRICE_WARPS) : Double.parseDouble((String) row.get(3)));
        setForSale(row.get(4)==null ? false : (Boolean) row.get(4));
        return this;
    }

    public Object[] getDbValues() {
        return new Object[]{
                /*OWNER UUID*/this.getOwnerUuid().toString(),
                /*TRACK TELEPORTS*/SQLUtils.getBoolInSQL(isTrackingTeleports()),
                /*LOCATION*/SQLUtils.locationToString(this),
                /*PRICE*/Double.toString(this.getPrice()),
                /*FOR SALE*/SQLUtils.getBoolInSQL(this.isForSale())
        };
    }

    @Override
    public void teleport(Player p, boolean loud) {
        super.teleport(p, loud);
        if(this.trackingTeleports)
            Bukkit.getLogger().info(p.getName() + " teleported to warp " + this.getName() + ".");
    }
}
