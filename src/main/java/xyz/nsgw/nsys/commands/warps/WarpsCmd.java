package xyz.nsgw.nsys.commands.warps;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.objects.Profile;

@CommandAlias("warps")
//@CommandPermission("nsys.homes.view")
public class WarpsCmd extends BaseCommand {

    private final NSys pl;

    public WarpsCmd(NSys pl) {
        this.pl = pl;
    }

    @Default
    public void onWarps(Player p) {
        pl.guiHandler().warps(p);
    }

    @CatchUnknown
    public void onUnknown(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW+"Warps: "+ String.join(", ",pl.sql().wrapList("warps").getList()));
        sender.sendMessage("You aren't a player!");
    }

    @HelpCommand
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
