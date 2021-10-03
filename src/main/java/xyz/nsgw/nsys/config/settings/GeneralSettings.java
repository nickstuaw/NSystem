/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.config.settings;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class GeneralSettings implements SettingsHolder {

    private static final String PFX_GAMEPLAY = "gameplay.";
    private static final String PFX_MODERATION = "moderation.";
    private static final String PFX_CHAT = "chat.";
    private static final String PFX_CHAT_ANTISPAM = "anti-spam.";
    private static final String PFX_TRACKTP = "tracktp.";
    private static final String PFX_PLAYER = "player.";
    private static final String PFX_AFK = "afk.";
    private static final String PFX_WARP = "warp.";
    private static final String PFX_WARPS = "warps.";
    private static final String PFX_HOMES = "homes.";

    private static final String SUFF_DEFAULT = "default";
    // File heading
    @Override
    public void registerComments(CommentsConfiguration conf) {
        conf.setComment("",
                "Changes to these settings can be applied by reloading the config.");
    }
    // MODERATION
    // TRACKTP
    @Comment("An alert is sent when a player teleports and has track-tp on or teleports to/at a warp with track-tp" +
            " on.")
    public static final Property<Boolean> TRACKTP_PLAYER_DEFAULT =
            newProperty(PFX_MODERATION + PFX_TRACKTP + PFX_PLAYER + SUFF_DEFAULT, false);
    @Comment("m = mode. Show x or y of the location when any player teleports [m=1: to x, m=2: from x, m=3: from x" +
            " to y].")
    public static final Property<Integer> TRACKTP_PLAYER_MODE =
            newProperty(PFX_MODERATION + PFX_TRACKTP + PFX_PLAYER +suffix.MODE, 3);

    public static final Property<Boolean> TRACKTP_WARP_DEFAULT =
            newProperty(PFX_MODERATION + PFX_TRACKTP + PFX_WARP + suffix.DEFAULT, false);
    @Comment("m = mode, Show x or y of the location when any player teleports [m=1: to x, m=2: from x, m=3: from x" +
            " to y].")
    public static final Property<Integer> TRACKTP_WARP_MODE =
            newProperty(PFX_MODERATION + PFX_TRACKTP + PFX_WARP + suffix.MODE, 1);

    @Comment("How many messages a player can send before being prevented from sending more messages that match.")
    public static final Property<Integer> CHAT_MESSAGE_REPEAT_LIMIT =
            newProperty(PFX_MODERATION + PFX_CHAT + PFX_CHAT_ANTISPAM + "repetition-limit", 5);
    @Comment("The amount of time (in seconds) a player is prevented from sending the same message they previously" +
            " sent.")
    public static final Property<Integer> CHAT_MESSAGE_REPEAT_COOLDOWN =
            newProperty(PFX_MODERATION + PFX_CHAT + PFX_CHAT_ANTISPAM + "repetition-cooldown", 10);

    @Comment("This is to prevent fast spammers. The maximum number of consecutive messages to count " +
            "which are each sent within the time interval below (timed-limit-of-messages-within-max-interval).")
    public static final Property<Integer> CHAT_MESSAGE_SPEED_LIMIT =
            newProperty(PFX_MODERATION + PFX_CHAT + PFX_CHAT_ANTISPAM
                    + "timed-limit-of-messages-within-max-interval", 10);
    @Comment("The specified length of time between every message.")
    public static final Property<Integer> CHAT_MESSAGE_SPEED_SECS =
            newProperty(PFX_MODERATION + PFX_CHAT + PFX_CHAT_ANTISPAM
                    + "maximum-consecutive-messages-breaking-the-interval", 3);

    // ----------
    // Q . O . L . Configurations
    // ----------

    public static final Property<Integer> HOMES_MAXIMUM_DEFAULT =
            newProperty(PFX_GAMEPLAY + PFX_PLAYER + PFX_HOMES + "serverwide-maximum", 10);

    @Comment("The amount of time spent idle by a player before being recognised as AFK.")
    public static final Property<Integer> MINIMUM_AFK_TIME =
            newProperty(PFX_GAMEPLAY + PFX_PLAYER + PFX_AFK + "minimum-time", 120);
    public static final Property<Boolean> CANCEL_AFK_ON_MOVE =
            newProperty("gameplay.player.afk.cancel-on-move", true);
}
class suffix {
    public static final String DEFAULT = "default";
    public static final String MODE = "mode";
}