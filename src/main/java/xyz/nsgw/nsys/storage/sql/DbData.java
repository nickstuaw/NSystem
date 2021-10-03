/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.storage.sql;

public class DbData {
    public static final String[] LISTS_PK = {"name","VARCHAR(36)"};
    //
    public static final String[][] LISTS_COLUMNS = {{"list","TEXT"}};

    public static final String[] MAPS_PK = {"id","VARCHAR(36)"};
    //
    public static final String[][] MAPS_COLUMNS = {{"map","TEXT"}};

    public static final String[] PROFILE_PK = {"uuid","VARCHAR(36)"};
    // DISCORD , TRACK TP , HOMES , NOTES , MUTE
    public static final String[][] PROFILE_COLUMNS = {{"discordid","VARCHAR(32)"},{"tracktp","BOOLEAN"},
            {"homes","TEXT"},{"privatenotes","TEXT"},{"mute","VARCHAR(256)"},{"cumulativelogons","INT"},
            {"afk","BOOLEAN"},{"afklocation","VARCHAR(128)"},{"lastactive","VARCHAR(32)"},{"lastname","VARCHAR(32)"},
            {"tracktplevel","TINYINT"}};

    public static final String[] WARP_PK = {"name","VARCHAR(36)"};
    //
    public static final String[][] WARP_COLUMNS = {{"owneruuid","VARCHAR(36)"},{"tracktp","BOOLEAN"},{"location","VARCHAR(255)"}};

    public static final String NOTES_SEP = "/sEp_/";
}
