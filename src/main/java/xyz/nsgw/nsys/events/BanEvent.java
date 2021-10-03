/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.utils.alerts.StaffAlert;

public class BanEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean isCancelled;

    public BanEvent(final Player sender, final boolean ban, final OfflinePlayer target) {
        StaffAlert alert = new StaffAlert(3,true,"ban",
                sender.getName()+" "+(ban?"un":"")+"banned "+target.getName());
        NSys.alerts().send(alert);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}
