/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.utils.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.config.settings.GeneralSettings;
import xyz.nsgw.nsys.events.BanEvent;
import xyz.nsgw.nsys.storage.objects.Profile;
import xyz.nsgw.nsys.storage.objects.locations.Loc;
import xyz.nsgw.nsys.utils.DisplayUtils;
import xyz.nsgw.nsys.utils.gui.items.ActiveItem;
import xyz.nsgw.nsys.utils.gui.items.ClickableSkullItem;

import java.util.Collection;
import java.util.UUID;

import static xyz.nsgw.nsys.utils.DisplayUtils.txt;

public class GUIHandler {

    public GUIHandler() {}

    public void profile(Profile profile, Player player, boolean self) {

        OfflinePlayer human = self ? player : Bukkit.getOfflinePlayer(profile.getKey());
        UUID uuid = human.getUniqueId();
        String name = profile.getLastName();
        boolean isAdmin = player.hasPermission("nsys.profiles.admin");

        PaginatedGui gui = Gui.paginated()
                .title(txt(ChatColor.RED+name+"'s profile"))
                .rows(6)
                .pageSize(45)
                .disableAllInteractions()
                .create();

        gui.setItem(6, 3, new GuiItem(ItemBuilder.from(Material.PAPER).name(DisplayUtils.txt("Previous")).build(), event -> gui.previous()));

        gui.setItem(6, 7, new GuiItem(ItemBuilder.from(Material.PAPER).name(DisplayUtils.txt("Next")).build(), event -> gui.next()));

        gui.addItem(new ClickableSkullItem(name, uuid, e -> {
        }, DisplayUtils.loreProfileMeta(profile, isAdmin)).build());

        if(isAdmin) {
            gui.setItem(6, 8, new ActiveItem("<red>Kick "+name,Material.FIRE_CHARGE,e -> {
                if(profile.isOnline()) {
                    profile.player().kick(DisplayUtils.txt("<red>You've been kicked."));
                }
            }, DisplayUtils.txt("<red>You've been kicked.")).build());
            gui.setItem(6, 9, new ActiveItem("<red><bold>"+(profile.isBanned()?"Unban":"Ban")+" "+name,Material.BARRIER,e -> {
                BanEvent banEvent = new BanEvent(player, !profile.isBanned(), profile.offlinePlayer());
                Bukkit.getPluginManager().callEvent(banEvent);
                if(!banEvent.isCancelled()) {
                    if (!profile.isBanned()) {
                        profile.offlinePlayer().banPlayer("You have been banned indefinitely. " + NSys.sh().gen().getProperty(GeneralSettings.BAN_SUFFIX));
                    } else {
                        Bukkit.getBanList(BanList.Type.NAME).pardon(profile.getLastName());
                    }
                    gui.close(player);
                }
            }, DisplayUtils.txt(profile.isBanned()?"":"<red>Indefinite ban. You can unban after.")).build());
        }

        gui.open(player);
    }

    public void locs(Player player, @Nullable Profile profile) {

        boolean areHomes = profile != null;

        PaginatedGui locs = Gui.paginated()
                .title(Component.text(areHomes?"Your Homes":"Warps"))
                .rows(6)
                .pageSize(45)
                .create();

        locs.setItem(6, 3, new GuiItem(ItemBuilder.from(Material.PAPER).name(DisplayUtils.txt("Previous")).build(), event -> locs.previous()));

        locs.setItem(6, 7, new GuiItem(ItemBuilder.from(Material.PAPER).name(DisplayUtils.txt("Next")).build(), event -> locs.next()));

        Collection<String> names = areHomes ? profile.getHomes().keySet() : NSys.sql().wrapList("warps").getList();

        ActiveItem activeItem;
        for(String name : (areHomes ? profile.getHomes().keySet() : NSys.sql().wrapList("warps").getList())) {
            activeItem = new ActiveItem("<gradient:#33FFAA:#2233FF>"+name,
                areHomes ? Material.ITEM_FRAME : Material.ENDER_PEARL,
                e -> {
                    Loc loc = areHomes ? profile.getHome(name) : NSys.sql().wrapWarp(name);
                    if(loc == null) return;
                    locs.close(player);
                    loc.teleport(player);
                });
            locs.addItem(activeItem.build());
        }

        locs.open(player);
    }

    public void homes(Profile profile, Player player) {
        this.locs(player,profile);
    }

    public void warps(Player p) {
        this.locs(p,null);
    }
}
