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
    public void onDelWarp(CommandSender s, @Default("warp") String warp) {
        Warp w = pl.sql().wrapWarpIfLoaded(warp);
        if(w != null) {
            pl.sql().invalidateAndDeleteWarp(w);
            s.sendMessage(ChatColor.YELLOW + "Warp \"" + warp + "\" deleted.");
        } else {
            s.sendMessage(ChatColor.RED+"The warp \""+warp+"\" doesn't exist.");
        }
    }

    @HelpCommand
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
