package xyz.nsgw.nsys.storage;

import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Warp {

    private final String key;

    private UUID ownerUuid;
    private boolean trackingTeleports;
    private Location location;

    public Warp(final String name) {
        key = name;
    }

    public String getKey() {return key;}

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

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location loc) {
        location = loc;
    }

    public Warp loadAttributes(final SQLTable table) {
        List<Object> row = SQLUtils.getRow(table, "\""+key+"\"", Arrays.stream(DbData.WARP_COLUMNS).map(c->c[0]).collect(Collectors.toList()).toArray(String[]::new));
        setOwnerUuid(UUID.fromString((String) row.get(0)));
        setTrackingTeleports((Boolean) row.get(1));
        setLocation(SQLUtils.stringToLocation((String) row.get(2)));
        return this;
    }

    public Object[] getDbValues() {
        return new Object[]{
                /*OWNER UUID*/getOwnerUuid().toString(),
                /*TRACK TELEPORTS*/SQLUtils.getBoolInSQL(isTrackingTeleports()),
                /*LOCATION*/SQLUtils.locationToString(getLocation())
        };
    }

}
