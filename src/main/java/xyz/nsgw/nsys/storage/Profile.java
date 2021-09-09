/*
© Copyright Nick Williams 2021.
Credit should be given to the original author where this code is used.
 */

package xyz.nsgw.nsys.storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Profile {

    private final UUID key;
    private String discord;
    private boolean trackingTeleports;

    private List<String> privateNotes;

    private HashMap<String, Home> homes;

    private Date muteFrom;
    private int muteSeconds;
    private boolean shadowMute;

    // Temporary - not for saving/loading
    private Date lastChat;
    private int duplicateMessageCounter;
    private int spamTimeCounter;
    private String lastChatMsg;

    public Profile(final UUID uuid) {
        key = uuid;
        discord = "";
        trackingTeleports = false;
        homes = new HashMap<>();
        privateNotes = new ArrayList<>();
        muteFrom = null;
        muteSeconds = -1;
        lastChat = null;
        shadowMute = false;

        initiateSpamCounters(5,5);
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

    public void setHomes(String raw) {
        homes = new HashMap<>();
        if(raw.length()<"homename:worlduuid,x,y,z,yaw,pitch;".length()) {
            return;
        }
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

    public void setHome(final String homeName, final Location location) {
        homes.put(homeName, new Home(location));
    }
    public void delHome(final String homeName) {
        homes.remove(homeName);
    }

    @Nullable
    public Location getHome(final String homeName) {
        if(!homes.containsKey(homeName)) return null;
        return homes.get(homeName);
    }
    public boolean hasHome(String name) {
        return homes.containsKey(name);
    }
    public HashMap<String,Home> getHomes() {
        return homes;
    }

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
    public long getSecsSinceChat(){return lastChat==null?60:TimeUnit.SECONDS.convert(Math.abs((new Date()).getTime()- lastChat.getTime()),TimeUnit.MILLISECONDS);}
    public void recordChatAttempt(){lastChat=new Date();}
    public int getDuplicateMessageCounter() {return duplicateMessageCounter;}
    public boolean isDupeMsgCounterLow(int max) {return duplicateMessageCounter<max;}
    public void increaseDupeMsgCounter() {duplicateMessageCounter++;}
    public void decreaseDupeMsgCounter() {duplicateMessageCounter--;}
    public void resetDupeMsgCounter() {duplicateMessageCounter=0;}
    public String getLastChatMsg() {return lastChatMsg;}
    public void setLastChatMsg(String m) {lastChatMsg=m;}
    public int getSpamTimeCounter() {return duplicateMessageCounter;}
    public boolean isSpamTimeCounterLow(int max) {return duplicateMessageCounter<max;}
    public void increaseSpamTimeCounter() {duplicateMessageCounter++;}
    public void decreaseSpamTimeCounter() {duplicateMessageCounter--;}
    public void resetSpamTimeCounter() {duplicateMessageCounter=0;}

    private void initiateSpamCounters(int maxDupeMsg,int maxSpamInterval) {duplicateMessageCounter=maxDupeMsg;spamTimeCounter=maxSpamInterval;}


    public Profile loadAttributes(final SQLTable table) {
        List<Object> row = SQLUtils.getRow(table, "\""+key.toString()+"\"",Arrays.stream(DbData.PROFILE_COLUMNS).map(c->c[0]).collect(Collectors.toList()).toArray(String[]::new));
        // DISCORD , TRACK TP , HOMES , NOTES , MUTE
        setDiscord((String) row.get(0));
        setTrackingTeleports((Boolean) row.get(1));
        setHomes((String) row.get(2));
        setPrivateNotes((String) row.get(3));
        setMute((String) row.get(4));

        initiateSpamCounters(5,5);
        return this;
    }

    public Object[] getDbValues() {
        return new Object[] {
                // DISCORD , TRACK TP , HOMES , NOTES , MUTE
                /*DISCORD*/getDiscord(),
                /*TRACK TP*/SQLUtils.getBoolInSQL(isTrackingTeleports()),
                /*HOMES*/getHomesString(),
                /*NOTES*/getPrivateNotesString(),
                /*MUTE*/getMute()
        };
    }
}
