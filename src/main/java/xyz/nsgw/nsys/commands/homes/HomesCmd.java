/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.commands.homes;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.jorel.commandapi.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.commands.CommandHandler;
import xyz.nsgw.nsys.storage.objects.OnlineHome;
import xyz.nsgw.nsys.storage.objects.Profile;
import xyz.nsgw.nsys.storage.objects.locations.Home;

@CommandAlias("homes")
@CommandPermission("nsys.cmd.homes")
public class HomesCmd extends BaseCommand {

    private final NSys pl;

    public HomesCmd(final NSys plugin) {
        this.pl = plugin;
    }

    @Default
    public void onDefault(Player p) {
        Profile profile = NSys.sql().wrapProfile(p.getUniqueId());
        pl.guiHandler().homes(profile,p);
    }

    @Subcommand("tp")
    @CommandPermission("nsys.cmd.home.tp")
    @CommandCompletion("@homes")
    public void onHome(Player p, Home home) {
        tp(p, home);
    }

    @Subcommand("set")
    @CommandPermission("nsys.cmd.home.set")
    @CommandCompletion("@homes")
    public void onSetHome(Player p, @Default("homes") String home) {
        NSys.sql().wrapProfile(p.getUniqueId()).setHomeHere(home);
    }

    @Subcommand("delete")
    @CommandPermission("nsys.cmd.home.delete")
    @CommandCompletion("@homes")
    public void onDelHome(Player p, @Default("home") String homeName) {
        if(NSys.sql().wrapProfile(p.getUniqueId()).delHome(homeName) == null) {
            p.sendMessage(ChatColor.RED + homeName + " does not exist.");
        } else {
            p.sendMessage(ChatColor.GREEN + "Home deleted.");
            CommandAPI.updateRequirements(p);
        }
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

    @Subcommand("offlineftp|oftp")
    @CommandPermission("nsys.cmd.home.visit")
    public static void onVisit(Player p, String target) {
        String[] split = target.split(":");
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayerIfCached(split[0]);
        if(targetPlayer == null) {
            p.sendMessage(ChatColor.RED+"Player not found.");
            return;
        }
        Profile targetProfile = NSys.sql().wrapProfile(targetPlayer.getUniqueId());
        Home home = targetProfile.getHome(split[1]);
        if(home == null) {
            p.sendMessage(ChatColor.RED+"No home found.");
            return;
        }
        tp(p, home);
    }

    private static void tp(final Player p, final Home home) {
        p.sendMessage(ChatColor.GREEN+"You're teleporting...");
        p.teleport(home);
    }

}
