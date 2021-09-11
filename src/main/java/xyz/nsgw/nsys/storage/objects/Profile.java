/*
© Copyright Nick Williams 2021.
Credit should be given to the original author where this code is used.
 */

package xyz.nsgw.nsys.storage.objects;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.config.settings.GeneralSettings;
import xyz.nsgw.nsys.storage.objects.locations.Home;
import xyz.nsgw.nsys.storage.sql.DbData;
import xyz.nsgw.nsys.storage.sql.SQLTable;
import xyz.nsgw.nsys.storage.sql.SQLUtils;
import xyz.nsgw.nsys.utils.ArithmeticUtils;
import xyz.nsgw.nsys.utils.LocationUtils;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Profile {

    private final UUID key;
    private String discord;
    private boolean trackingTeleports;
    private int trackingTeleportsLevel;

    private List<String> privateNotes;

    private List<String> staffAlerts;//todo

    private HashMap<String, Home> homes;

    private final int maxHomes;

    private int maxLogins;

    private Date muteFrom;
    private int muteSeconds;
    private boolean shadowMute;

    private boolean afk;
    private Location afkLocation;

    private Date lastActive;

    private String lastName;

    public Profile(final UUID uuid) {
        key = uuid;
        discord = "";
        trackingTeleports = true;
        trackingTeleportsLevel = NSys.sh().gen().getProperty(GeneralSettings.TRACKTP_PLAYER_MODE);
        homes = new HashMap<>();
        privateNotes = new ArrayList<>();
        muteFrom = null;
        muteSeconds = -1;
        shadowMute = false;
        maxHomes = NSys.sh().gen().getProperty(GeneralSettings.HOMES_MAXIMUM_DEFAULT);
        maxLogins = 0;
        afk = false;
        afkLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
        lastActive = new Date();
        lastName = "";
    }

    public UUID getKey() {
        return key;
    }
    public void setDiscord(final String dId) {
        discord = dId;
    }
    public String getDiscord() {
        return discord;
    }

    public int getMaxHomes() {
        return maxHomes;
    }
    public void setHomes(String raw) {
        homes = new HashMap<>();
        if(raw.isEmpty()) return;
        String[] values;
        // homename:worlduuid,x,y,z,yaw,pitch;
        for(String home : raw.split(";")) {
            values = home.split(":");
            homes.put(values[0].split(",")[0],new Home(SQLUtils.stringToLocation(values[1])));
        }
    }

    public String getHomesString() {
        if(homes.isEmpty()) return "";
        StringBuilder raw = new StringBuilder();
        String data;
        for(String homeName : homes.keySet()) {
            data = homeName + ":" + SQLUtils.locationToString(homes.get(homeName)) +";";
            raw.append(data);
        }
        return raw.toString();
    }

    public boolean setHome(final String homeName, final Location location) {
        if(homes.size() < maxHomes || (homes.size() == maxHomes && homes.containsKey(homeName))) {
            homes.put(homeName, new Home(location));
            return true;
        }
        return false;
    }
    public void delHome(final String homeName) {
        homes.remove(homeName);
    }

    @Nullable
    public Home getHome(final String homeName) {
        if(!homes.containsKey(homeName)) return null;
        return homes.get(homeName);
    }
    public boolean hasHome(String name) {
        return homes.containsKey(name);
    }
    public HashMap<String,Home> getHomes() {
        return homes;
    }
    public Set<String> getHomeNames() {return homes.keySet();}

    // Private notes
    public List<String> getPrivateNotes() {
        return privateNotes;
    }
    public String getPrivateNotesString() {
        return String.join(DbData.NOTES_SEP,privateNotes);
    }
    public void setPrivateNotes(String notes) {
        setPrivateNotes(Arrays.asList(notes.split(DbData.NOTES_SEP)));
    }
    public void setPrivateNotes(List<String> privateNotes) {
        this.privateNotes = privateNotes;
    }

    // Tracking teleports
    public boolean isTrackingTeleports() {return trackingTeleports;}
    public void setTrackingTeleports(boolean track) {trackingTeleports = track;}

    // Muting
    public String getMute() {
        return (muteFrom==null?"none":muteFrom.getTime())+"/"+muteSeconds;
    }
    public void setMute(String m) {
        String mFrom = m.split("/")[0];
        muteFrom = Objects.equals(mFrom, "none") ? null : Date.from(Instant.ofEpochMilli(Long.parseLong(mFrom)));
        muteSeconds = Integer.parseInt(m.split("/")[1]);
    }
    public boolean isMuted() {return muteFrom!=null && muteSeconds>-1;}
    public boolean isShadowMute() {return shadowMute;}
    public void setShadowMute(boolean sm) {shadowMute=sm;}
    public boolean checkMute() {
        if(muteSeconds == -1 || getMuteSecsPassed()>muteSeconds || muteFrom==null) {
            setMuted(false);
            return false;
        }return true;
    }
    public double getMuteSecsPassed() {
        if(muteFrom==null) return 0;
        return TimeUnit.SECONDS.convert(Math.abs((new Date()).getTime()-muteFrom.getTime()),TimeUnit.MILLISECONDS);
    }
    public Date getMuteFrom() {return muteFrom;}
    public void setMuteSeconds(int s){muteSeconds=s;}
    public void setMuted(boolean m) {muteFrom=(m?new Date():null);}

    public void login(String ign) {
        maxLogins++;
        setLastName(ign);
        updateActivity();
    }
    private void setMaxLogins(int mx) {
        maxLogins = mx;
    }
    public int getMaxLogins() {
        return maxLogins;
    }

    public void setAfk(boolean a, boolean silent) {
        if(!silent) Bukkit.broadcast(Component.text(ChatColor.GRAY+lastName+" is "+(a?"now":"no longer")+" AFK."));
        afk = a;
    }

    public boolean isAfk() {
        return afk;
    }

    public Location getAfkLocation() {
        return afkLocation;
    }

    public void setAfkLocation(Location afkLocation) {
        this.afkLocation = afkLocation;
    }

    public Date getLastActive() {
        return lastActive;
    }

    private void setLastActive(Date lA) {
        lastActive = lA;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String ign) {
        lastName = ign;
    }

    public void updateActivity() {
        lastActive = new Date();
        if(afk) setAfk(false,false);
    }
    public void updateActivitySilently() {
        lastActive = new Date();
    }

    public void setTrackingTeleportsLevel(int l) {
        trackingTeleportsLevel = l;
    }
    public int getTrackingTeleportsLevel() {
        return trackingTeleportsLevel;
    }

    public Profile loadAttributes(final SQLTable table) {
        List<Object> row = SQLUtils.getRow(table, "\""+key.toString()+"\"",Arrays.stream(DbData.PROFILE_COLUMNS).map(c->c[0]).collect(Collectors.toList()).toArray(String[]::new));
        // DISCORD , TRACK TP , HOMES , NOTES , MUTE, LOGINS, AFK LOC
        setDiscord((String) row.get(0));
        setTrackingTeleports((Boolean) row.get(1));
        setHomes((String) row.get(2));
        setPrivateNotes((String) row.get(3));
        setMute((String) row.get(4));
        setMaxLogins((Integer) row.get(5));
        setAfk((Boolean) row.get(6), true);
        setAfkLocation(SQLUtils.stringToLocation((String) row.get(7)));
        setLastActive(ArithmeticUtils.dateFromStr((String) row.get(8)));
        setLastName((String) row.get(9));
        setTrackingTeleportsLevel((Integer) row.get(10));
        return this;
    }

    public Object[] getDbValues() {
        return new Object[] {
                // DISCORD , TRACK TP , HOMES , NOTES , MUTE
                /*DISCORD*/getDiscord(),
                /*TRACK TP*/SQLUtils.getBoolInSQL(isTrackingTeleports()),
                /*HOMES*/getHomesString(),
                /*NOTES*/getPrivateNotesString(),
                /*MUTE*/getMute(),
                /*MAXLOGINS*/getMaxLogins(),
                /*IS AFK*/SQLUtils.getBoolInSQL(isAfk()),
                /*LAST LOCATION*/SQLUtils.locationToString(afkLocation),
                /*LAST ACTIVE*/Long.toString(getLastActive().getTime()),
                /*LAST NAME*/getLastName(),
                /*TRACK TP LVL*/getTrackingTeleportsLevel()
        };
    }
}
