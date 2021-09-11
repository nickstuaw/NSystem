package xyz.nsgw.nsys.storage.objects.locations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.storage.sql.DbData;
import xyz.nsgw.nsys.storage.sql.SQLTable;
import xyz.nsgw.nsys.storage.sql.SQLUtils;
import xyz.nsgw.nsys.utils.LocationUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Warp extends Loc {

    private final String key;

    private UUID ownerUuid;
    private boolean trackingTeleports;

    public Warp(final String name, final Location location, final UUID uuid) {
        super(location);
        key = name;
        ownerUuid = uuid;
    }
    public Warp(final String name) {
        super(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        key = name;
    }

    public boolean exists() {
        return this.ownerUuid!=null;
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

    public void teleport(final Player player) {
        player.teleport(this);
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
                /*LOCATION*/SQLUtils.locationToString(this)
        };
    }

}
