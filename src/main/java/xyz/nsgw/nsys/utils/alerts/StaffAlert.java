/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.utils.alerts;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.nsgw.nsys.config.settings.GeneralSettings;
import xyz.nsgw.nsys.utils.DisplayUtils;

//todo
public class StaffAlert {

    // To be used for opting out
    private String id;

    // Recipients according to priority level (all means players and console):
    // 0 = force all
    // 1 = all (optional per player)
    // 2 = force players only
    // 3 = players only (optional per player)
    // 4 = console only
    private int priority;

    private Component component;

    private boolean warning;

    public StaffAlert(final int priority, final boolean warning, final String id, final @NotNull String msg) {
        this.id = id;
        this.priority = priority;
        this.component = DisplayUtils.txt((warning ? "<yellow>" : "") + GeneralSettings.STAFFALERT_PREFIX + msg);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int priority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Component component() {
        return this.component;
    }

    public void setComponent(final Component c) {
        this.component = c;
    }

}
