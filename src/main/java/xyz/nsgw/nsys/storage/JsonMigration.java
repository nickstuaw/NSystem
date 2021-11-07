package xyz.nsgw.nsys.storage;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import xyz.nsgw.nsys.storage.objects.Profile;
import xyz.nsgw.nsys.storage.objects.SettingsList;
import xyz.nsgw.nsys.storage.objects.SettingsMap;
import xyz.nsgw.nsys.storage.objects.locations.Home;
import xyz.nsgw.nsys.storage.objects.locations.Warp;

import java.io.*;
import java.time.Instant;
import java.util.*;

public class JsonMigration {

    private static final Gson gson = new Gson();

    private final File dir, profile_dir, warp_dir;

    public JsonMigration(File datafolder) {
        dir = new File(datafolder, "json");
        if(!dir.exists()) dir.mkdir();
        profile_dir = new File(dir, "profiles");
        if(dir.exists() && !profile_dir.exists()) profile_dir.mkdir();
        warp_dir = new File(dir,"warps");
        if(dir.exists() && !warp_dir.exists()) warp_dir.mkdir();
    }

    public void storeMap(SettingsMap map) {
        storeMapInJson("map_"+map.getKey()+".json", map);
    }

    public SettingsMap getMap(String key) {
        return getMapFromJson("map_"+key+".json");
    }

    public void storeList(SettingsList list) {
        storeListInJson("list_"+list.getKey()+".json", list);
    }

    public SettingsList getList(String key) {
        return getListFromJson("list_"+key+".json");
    }

    public void storeWarp(Warp warp) {
        storeWarpInJson("warp_" + warp.getName() + ".json", warp);
    }

    public Warp getWarp(String name) {
        return getWarpFromJson("warp_" + name + ".json");
    }

    public void storeProfile(Profile profile) {
        storeProfileInJson(profile.getKey().toString() + ".json", profile);
    }

    public Profile getProfile(String key) {
        return getProfileFromJson(key + ".json");
    }

    private void storeMapInJson(String path, SettingsMap map) {
        try {
            String json = gson.toJson(map);
            FileWriter writer = new FileWriter(new File(dir, path));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SettingsMap getMapFromJson(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(dir, path)));
            SettingsMap res = gson.fromJson(reader, SettingsMap.class);
            reader.close();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void storeListInJson(String path, SettingsList list) {
        try {
            String json = gson.toJson(list);
            FileWriter writer = new FileWriter(new File(dir, path));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SettingsList getListFromJson(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(dir, path)));
            SettingsList res = gson.fromJson(reader, SettingsList.class);
            reader.close();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void storeWarpInJson(String path, Warp warp) {
        try {
            String json = gson.toJson(parseWarp(warp));
            FileWriter writer = new FileWriter(new File(warp_dir, path));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Warp getWarpFromJson(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(warp_dir, path)));
            JWarp jw = gson.fromJson(reader, JWarp.class);
            reader.close();
            return parseJWarp(jw);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void storeProfileInJson(String path, Profile profile) {
        try {
            String json = gson.toJson(parseProfile(profile));
            FileWriter writer = new FileWriter(new File(profile_dir, path));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Profile getProfileFromJson(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(profile_dir, path)));
            JProfile jp = gson.fromJson(reader, JProfile.class);
            reader.close();
            return parseJProfile(jp);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JWarp parseWarp(Warp warp) {
        return new JWarp(warp.getName(), warp, warp.getOwnerUuid(), warp.isForSale(), warp.isTrackingTeleports(), warp.getPrice());
    }
    private Warp parseJWarp(JWarp jw) {
        Warp warp = new Warp(jw.name);
        warp.setLocation(jw.location.toLocation());
        warp.setOwnerUuid(UUID.fromString(jw.ownerUuid));
        warp.setForSale(jw.forSale);
        warp.setTrackingTeleports(jw.tpTracking);
        warp.setPrice(jw.price);
        return warp;
    }
    private JProfile parseProfile(Profile profile) {
        JProfile jp = new JProfile(profile.getHomes());
        jp.uuid = profile.getKey().toString();
        jp.discord = profile.getDiscord();
        jp.privNotes = profile.getPrivateNotesString();
        jp.mute = profile.getMute();
        jp.maxLogins = profile.getMaxLogins();
        jp.isAfk = profile.isAfk();
        jp.afkLocation = new JLocation(profile.getAfkLocation());
        jp.lastActiveTime = profile.getLastActive().getTime();
        jp.lastName = profile.getLastName();
        jp.trackTpLevel = profile.getTrackingTeleportsLevel();
        jp.ownedWarps = profile.getOwnedWarpsString();
        return jp;
    }
    private Profile parseJProfile(JProfile jp) {
        Profile p = new Profile(UUID.fromString(jp.uuid));
        p.setDiscord(jp.discord);
        p.setTrackingTeleports(jp.tpTracking);
        p.setHomesMap(jp.getHomes());
        p.setPrivateNotes(jp.privNotes);
        p.setMute(jp.mute);
        p.setMaxLogins(jp.maxLogins);
        p.setAfk(jp.isAfk, true);
        p.setAfkLocation(jp.afkLocation.toLocation());
        p.setLastActive(Date.from(Instant.ofEpochMilli(jp.lastActiveTime)));
        p.setLastName(jp.lastName);
        p.setTrackingTeleportsLevel(jp.trackTpLevel);
        p.setOwnedWarps(jp.ownedWarps);
        return p;
    }
}
class JProfile {
    public String uuid;
    public String discord;
    public boolean tpTracking;
    public Map<String,JHome> homes;
    public String privNotes;
    public String mute;
    public int maxLogins;
    public boolean isAfk;
    public JLocation afkLocation;
    public Long lastActiveTime;
    public String lastName;
    public int trackTpLevel;
    public String ownedWarps;
    public JProfile(HashMap<String, Home> hs) {
        homes = new HashMap<>();
        for(String item : hs.keySet()) {
            homes.put(item, new JHome(hs.get(item)));
        }
    }
    public HashMap<String,Home> getHomes() {
        HashMap<String,Home> hs = new HashMap<>();
        for(String s : homes.keySet()) {
            hs.put(s, new Home(homes.get(s).location.toLocation(), homes.get(s).name));
        }
        return hs;
    }
}
class JHome {
    public JLocation location;
    public String name;
    public JHome(Home home) {
        location = new JLocation(home);
        name = home.getName();
    }
}
class JWarp {
    public JLocation location;
    public String name;
    public String ownerUuid;
    public boolean forSale, tpTracking;
    public double price;
    public JWarp(String nm, Location loc, UUID owner, boolean fSale, boolean trackingTp, double cost) {
        name = nm;
        location = new JLocation(loc);
        ownerUuid = owner.toString();
        forSale = fSale;
        tpTracking = trackingTp;
        price = cost;
    }
}
class JLocation {
    public String worldUuid;
    public double x, y, z;
    public float yaw, pitch;
    public JLocation(Location loc) {
        worldUuid = loc.getWorld().getUID().toString();
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
        yaw = loc.getYaw();
        pitch = loc.getPitch();
    }
    public Location toLocation() {
        return new Location(Bukkit.getWorld(UUID.fromString(worldUuid)),
                x, y, z, yaw, pitch);
    }
}