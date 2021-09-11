package xyz.nsgw.nsys.commands.warps;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.objects.locations.Warp;


@CommandAlias("delwarp")
//@CommandPermission("nsys.warps.delete")
public class DelWarpCmd extends BaseCommand {

    private final NSys pl;

    public DelWarpCmd(NSys pl) {
        this.pl = pl;
    }

    @Default
    @CommandCompletion("@warps")
    public void onDelWarp(CommandSender s, Warp warp) {
        NSys.sql().invalidateAndDeleteWarp(warp);
        NSys.sql().wrapList("warps").del(warp.getKey());
        s.sendMessage(ChatColor.YELLOW + "Warp \"" + warp + "\" deleted.");
    }

}
