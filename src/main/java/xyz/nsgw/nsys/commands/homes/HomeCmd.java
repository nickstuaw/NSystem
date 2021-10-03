/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.commands.homes;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.objects.OnlineHome;
import xyz.nsgw.nsys.storage.objects.Profile;
import xyz.nsgw.nsys.storage.objects.locations.Home;

@CommandAlias("home")
@CommandPermission("nsys.cmd.home")
public class HomeCmd extends BaseCommand {

    @Subcommand("tp")
    @CommandPermission("nsys.cmd.home.tp")
    @CommandCompletion("@homes")
    public static void onHome(Player p, Home home) {
        p.sendMessage(ChatColor.GREEN + "You're teleporting...");
        p.teleport(home);
    }

    @Subcommand("set")
    @CommandPermission("nsys.cmd.home.set")
    @CommandCompletion("@homes")
    public void onSetHome(Player p, @Default("homes") String home) {
        if(home.contains(":")) {
            p.sendMessage(ChatColor.RED + "Home names cannot contain ':'.");
            return;
        }
        Profile profile = NSys.sql().wrapProfile(p.getUniqueId());
        if(profile.setHome(home, p.getLocation())) {
            p.sendMessage(ChatColor.GREEN + "Home '" + home + "' set.");
        } else {
            p.sendMessage(ChatColor.RED + "You have reached your maximum amount of homes.");
        }
    }

    @Subcommand("delete")
    @CommandPermission("nsys.cmd.home.delete")
    @CommandCompletion("@homes")
    public void onDelHome(Player p, @Default("home") String home) {
        Profile profile = NSys.sql().wrapProfile(p.getUniqueId());
        profile.delHome(home);
        p.sendMessage(ChatColor.GREEN + "Home '"+home+"' deleted.");
    }

    @Subcommand("list|ls")
    @CommandPermission("nsys.cmd.home.list")
    public void onHomes(Player p) {
        Profile profile = NSys.sql().wrapProfile(p.getUniqueId());
        p.sendMessage(ChatColor.BLUE +""+profile.getHomes().size()+ "/"+profile.getMaxHomes()+ChatColor.GREEN+" Homes: "+ChatColor.RESET+ String.join(", ", profile.getHomes().keySet()));
    }

    @Subcommand("forcetp|ftp")
    @CommandPermission("nsys.cmd.home.visit")
    @CommandCompletion("@onlinehomes")
    public static void onVisit(Player p, OnlineHome target) {
        Home home = target.getHome();
        p.teleport(home);
    }

}
