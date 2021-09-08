package xyz.cosmicity.nsys.config.settings;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class GeneralSettings implements SettingsHolder {

    private static final String PFX_MODERATION = "moderation.";
    private static final String PFX_TRACKTP = "tracktp.";
    private static final String PFX_PLAYER = "player.";
    private static final String PFX_WARP = "warp.";

    private static final String SUFF_DEFAULT = "default";
    // File heading
    @Override
    public void registerComments(CommentsConfiguration conf) {
        conf.setComment("",
                "Changes to these settings can be applied by reloading the config.");
    }
    // MODERATION
    // TRACKTP
    @Comment("An alert is sent when a player teleports and has track-tp on or teleports to/at a warp with track-tp on.")
    public static final Property<Boolean> TRACKTP_PLAYER_DEFAULT =
            newProperty(PFX_MODERATION + PFX_TRACKTP + PFX_PLAYER + SUFF_DEFAULT, false);
    @Comment("m = mode. Show x or y of the location when any player teleports [m=1: to x, m=2: from x, m=3: from x to y].")
    public static final Property<Integer> TRACKTP_PLAYER_MODE =
            newProperty(PFX_MODERATION + PFX_TRACKTP + PFX_PLAYER +suffix.MODE, 1);


    public static final Property<Boolean> TRACKTP_WARP_DEFAULT =
            newProperty(PFX_MODERATION + PFX_TRACKTP + PFX_WARP + suffix.DEFAULT, false);
    @Comment("m = mode, Show x or y of the location when any player teleports [m=1: to x, m=2: from x, m=3: from x to y].")
    public static final Property<Integer> TRACKTP_WARP_MODE =
            newProperty(PFX_MODERATION + PFX_TRACKTP + PFX_WARP + suffix.MODE, 1);
}
class suffix {
    public static final String DEFAULT = "default";
    public static final String MODE = "mode";
}