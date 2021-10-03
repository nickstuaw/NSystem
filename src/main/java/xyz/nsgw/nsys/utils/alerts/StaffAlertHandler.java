/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.utils.alerts;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.nsgw.nsys.NSys;

import java.util.ArrayList;
import java.util.List;
//todo
public class StaffAlertHandler {

    private List<Player> onlineRecipients;

    private NSys pl;

    public StaffAlertHandler(final NSys plugin) {
        pl = plugin;
        onlineRecipients = new ArrayList<>();
    }

    public void addRecipient(@NotNull final Player p) {
        onlineRecipients.add(p);
    }

    public void rmRecipient(@NotNull final Player p) {
        onlineRecipients.remove(p);
    }

    public void send(@NotNull final StaffAlert alert) {
        Component component = alert.component();
        switch(alert.priority()) {
            // change all of these to select the targets
            case 0:
                consoleOut(component);
                playersOut(component);
                break;
            case 1:
                consoleOut(component);
                playersOutOpt(component);
                break;
            case 2:
                playersOut(component);
                break;
            case 3:
                playersOutOpt(component);
                break;
            default:
                consoleOut(component);
                break;
        }
    }

    private void playersOut(final Component c) {
        for(Player p : onlineRecipients) {
            p.sendMessage(c);
        }
    }

    private void playersOutOpt(final Component c) {
        for(Player p : onlineRecipients) {
            p.sendMessage(c);
        }
    }

    private void consoleOut(final Component c) {
        Bukkit.getServer().getLogger().info(c.toString());
    }

}
