/*
© Copyright Nick Williams 2021.
Credit should be given to the original author where this code is used.
 */

package xyz.cosmicity.nsys.storage;

public class DbData {
    public static final String[] PROFILE_PK = {"uuid","VARCHAR(36)"};
    // DISCORD , TRACK TP , HOMES , NOTES , MUTE
    public static final String[][] PROFILE_COLUMNS = {{"discordid","VARCHAR(32)"},{"tracktp","BOOLEAN"},{"homes","TEXT"},{"privatenotes","TEXT"},{"mute","VARCHAR(256)"}};

    public static final String NOTES_SEP = "/sEp_/";
}
