/*
© Copyright Nick Williams 2021.
Credit should be given to the original author where this code is used.
 */

package xyz.nsgw.nsys.storage;

public class DbData {
    public static final String[] PROFILE_PK = {"uuid","VARCHAR(36)"};
    // DISCORD , TRACK TP , HOMES , NOTES , MUTE
    public static final String[][] PROFILE_COLUMNS = {{"discordid","VARCHAR(32)"},{"tracktp","BOOLEAN"},{"homes","TEXT"},{"privatenotes","TEXT"},{"mute","VARCHAR(256)"}};

    public static final String[] WARP_PK = {"name","VARCHAR(36)"};
    //
    public static final String[][] WARP_COLUMNS = {{"owneruuid","VARCHAR(36)"},{"tracktp","BOOLEAN"},{"location","VARCHAR(255)"}};

    public static final String NOTES_SEP = "/sEp_/";
}
