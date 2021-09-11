package xyz.nsgw.nsys.utils.alerts;

import net.kyori.adventure.text.Component;
//todo
public class StaffAlert {

    // Recipients according to priority level (all means players and console):
    // 0 = force all
    // 1 = all (optional per player)
    // 2 = force players only
    // 3 = players only (optional per player)
    // 4 = console only
    private int priority;

    private String identifier;

    private Component component;

    public StaffAlert() {}

    public String identifier() {
        return identifier;
    }

    public void setIdentifier(final String id) {
        this.identifier = id;
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
