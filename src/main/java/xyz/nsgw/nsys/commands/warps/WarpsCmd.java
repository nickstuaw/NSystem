/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.commands.warps;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.objects.locations.Warp;
import xyz.nsgw.nsys.utils.EconUtils;

import java.util.List;

@CommandAlias("warps")
@CommandPermission("nsys.cmd.warps")
public class WarpsCmd extends BaseCommand {

    private final NSys pl;

    public WarpsCmd(final NSys plugin) {
        this.pl = plugin;
    }

    @Default
    public void onWarp(Player p) {
        pl.guiHandler().warps(p);
    }

    @Subcommand("tp")
    @CommandPermission("nsys.cmd.warp.tp")
    @CommandCompletion("@warps")
    @Description("Teleport to a warp.")
    public static void onWarp(Player p, Warp warp) {
        p.teleport(warp);
        p.sendMessage(ChatColor.GREEN+"Teleported!");
    }

    @Subcommand("set")
    @CommandPermission("nsys.cmd.warp.set")
    @CommandCompletion("@warps")
    @Description("Set a warp.")
    public void onSetWarp(Player p, String warpName) {
        NSys.newWarp(warpName, p, -1);
        p.sendMessage(ChatColor.GREEN + "Warp set: " + ChatColor.YELLOW + warpName +ChatColor.GREEN+ ".");
    }

    @Subcommand("delete")
    @CommandPermission("nsys.cmd.warp.delete")
    @CommandCompletion("@warps")
    @Description("Delete a warp.")
    public void onDelWarp(CommandSender s, Warp warp) {
        NSys.sql().invalidateAndDeleteWarp(warp);
        NSys.sql().wrapList("warps").del(warp.getKey());
        s.sendMessage(ChatColor.GREEN + "Warp " + ChatColor.YELLOW + warp.getKey() + ChatColor.GREEN + " deleted.");
    }

    @Subcommand("refund")
    @CommandPermission("nsys.cmd.warp.refund")
    @CommandCompletion("@warps")
    @Description("Delete a warp and refund the owner.")
    public void onRefWarp(CommandSender s, Warp warp) {
        EconUtils.give(Bukkit.getOfflinePlayer(warp.getOwnerUuid()), warp.getPrice(), true, true);
        NSys.sql().invalidateAndDeleteWarp(warp);
        NSys.sql().wrapList("warps").del(warp.getKey());
        s.sendMessage(ChatColor.GREEN + "Warp " + ChatColor.YELLOW + warp.getKey() + ChatColor.GREEN + " deleted.");
    }

    @Subcommand("list|ls")
    @CommandPermission("nsys.cmd.warp.list")
    @Description("List existing warps.")
    public void onListWarps(CommandSender s) {
        List<String> warps = NSys.sql().wrapList("warps").getList();
        s.sendMessage(ChatColor.GREEN +""+warps.size()+" warps: "+ChatColor.RESET+ String.join(", ", warps));
    }

    @HelpCommand
    public void onHelp(CommandSender s, CommandHelp help) {
        help.showHelp();
    }
}
