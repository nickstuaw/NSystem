package xyz.nsgw.nsys.commands.warps;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.objects.locations.Warp;

import java.util.List;

@CommandAlias("warp")
//@CommandPermission("nsys.homes.teleport")
public class WarpCmd extends BaseCommand {



    @Subcommand("tp")
    @CommandCompletion("@warps")
    @Description("Teleport to a warp.")
    public static void onWarp(Player p, Warp warp) {
        p.teleport(warp);
        p.sendMessage(ChatColor.YELLOW+"Teleported!");
    }

    @Subcommand("set")
    @CommandCompletion("@warps")
    @Description("Set a warp.")
    public void onSetWarp(Player p, String warpName) {
        Warp warp = NSys.sql().wrapWarp(warpName);
        warp.setOwnerUuid(p.getUniqueId());
        warp.setLocation(p.getLocation());
        NSys.sql().validateWarp(warp);
        NSys.sql().wrapList("warps").add(warpName);
        p.sendMessage(ChatColor.YELLOW + "Warp set: \"" + warp + "\", "+ warp.simplify() +".");
    }

    @Subcommand("delete")
    @CommandCompletion("@warps")
    @Description("Delete a warp.")
    public void onDelWarp(CommandSender s, Warp warp) {
        NSys.sql().invalidateAndDeleteWarp(warp);
        NSys.sql().wrapList("warps").del(warp.getKey());
        s.sendMessage(ChatColor.YELLOW + "Warp \"" + warp + "\" deleted.");
    }

    @Subcommand("list|ls")
    @Description("List existing warps.")
    public void onListWarps(CommandSender s) {
        List<String> warps = NSys.sql().wrapList("warps").getList();
        s.sendMessage(ChatColor.YELLOW +""+warps.size()+" warps: "+ChatColor.RESET+ String.join(", ", warps));
    }

    @HelpCommand
    public void onHelp(CommandSender s, CommandHelp help) {
        help.showHelp();
    }
}
