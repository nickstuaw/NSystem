package xyz.nsgw.nsys.commands.warps;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.objects.locations.Warp;
import xyz.nsgw.nsys.utils.LocationUtils;

@CommandAlias("setwarp")
//@CommandPermission("nsys.warps.set")
public class SetWarpCmd extends BaseCommand {

    private final NSys pl;

    public SetWarpCmd(NSys pl) {this.pl=pl;
    }

    @Default
    @CommandCompletion("@warps")
    public void onSetWarp(Player p, String warpName) {
        Warp warp = NSys.sql().wrapWarp(warpName);
        warp.setOwnerUuid(p.getUniqueId());
        warp.setLocation(p.getLocation());
        NSys.sql().validateWarp(warp);
        NSys.sql().wrapList("warps").add(warpName);
        p.sendMessage(ChatColor.YELLOW + "Warp set: \"" + warp + "\", "+ warp.simplify() +".");
    }

    @CatchUnknown
    public void onUnknown(CommandSender sender) {
        sender.sendMessage("You aren't a player!");
    }

}
