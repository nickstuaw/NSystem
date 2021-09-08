package xyz.nsgw.nsys.storage;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ModerationProfile {

    private final int key;
    private boolean trackingTeleports;
    private Integer mute;
    private Date muteStart;
    private String muteReason, kickReason;
    private String[] warnings;
    private HashMap<Date,String[]> warns;

    public ModerationProfile(final int k) {
        key = k;
        trackingTeleports = false;
        mute = 0;
        muteStart = null;
        muteReason = "";
        kickReason = "";
        warnings = new String[]{};
        warns = new HashMap<>();
    }
    public int getKey() {return key;}

    // Muted
    public boolean isMuted() {
        if(muteStart!=null) {
            if(getMuteSecsPassed() < mute) {
                return true;
            }
            // set muteStart to null as timer ended
            muteStart = null;
        }
        return false;
    }
    public double getMuteSecsPassed() {
        return TimeUnit.SECONDS.convert(Math.abs((new Date()).getTime()-muteStart.getTime()),TimeUnit.MILLISECONDS);
    }
    public void setMute(int seconds) {mute=seconds;}
    public void setMuteStart(Date s) {muteStart=s;}
    // Reasons
    public void setMuteReason(String r) {muteReason=r;}
    public void setKickReason(String r) {kickReason=r;}
    public String getMuteReason() {return muteReason;}
    public String getKickReason() {return kickReason;}
    // Warnings
    public HashMap<Date,String[]> getWarnings() {return warns;}
    public void setWarnings(HashMap<Date,String[]> w) {warns=w;}
    public String getWarningsRaw() {
        StringBuilder raw = new StringBuilder();
        String tmp;
        for(Date d : warns.keySet()) {
            tmp = d.getTime() + ";:#:;" +String.join(";:#:;",warns.get(d))+  ";-#=;";
            raw.append(tmp);
        }
        return raw.toString();
    }

    public ModerationProfile loadAttributes(final SQLTable table) {
        List<Object> row = SQLUtils.getRow(table, "\""+key+"\"", Arrays.stream(DbData.PROFILE_COLUMNS).map(c->c[0]).collect(Collectors.toList()).toArray(String[]::new));
        trackingTeleports = (Boolean) row.get(0);
        mute = (Integer) row.get(1);
        muteStart = Date.from(Instant.ofEpochMilli(Long.parseLong((String) row.get(2))));
        muteReason = (String) row.get(3);
        kickReason = (String) row.get(4);
        warnings = ((String) row.get(5)).split(";-#=;");
        String[] ws;
        for(String warning : warnings) {
            ws = warning.split(";:#:;");
            warns.put(Date.from(Instant.ofEpochMilli(Long.parseLong(ws[0]))), new String[]{ws[1],ws[2]});
        }
        return this;
    }

}
