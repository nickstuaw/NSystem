package xyz.nsgw.nsys.commands.warps;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.storage.objects.locations.Warp;

@CommandAlias("warp")
//@CommandPermission("nsys.homes.teleport")
public class WarpCmd extends BaseCommand {

    @Default
    @CommandCompletion("@warps")
    public static void onWarp(Player p, Warp warp) {
        p.teleport(warp);
        p.sendMessage(ChatColor.YELLOW+"Teleported!");
    }


    @CatchUnknown
    public static void onUnknown(CommandSender sender) {
        sender.sendMessage("UNKNOWN! You aren't a player...!");
    }

}
