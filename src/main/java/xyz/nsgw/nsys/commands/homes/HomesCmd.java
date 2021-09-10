package xyz.nsgw.nsys.commands.homes;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.objects.Profile;
import xyz.nsgw.nsys.utils.GUIHandler;

@CommandAlias("homes")
//@CommandPermission("nsys.homes.view")
public class HomesCmd extends BaseCommand {

    private final NSys pl;

    public HomesCmd(NSys pl) {
        this.pl = pl;
    }

    @Default
    public void onHomes(Player p) {
        Profile profile = pl.sql().wrapProfile(p.getUniqueId());
        pl.guiHandler().homes(profile, p);
        //p.sendMessage(ChatColor.BLUE +""+profile.getHomes().size()+ "/10 "+ChatColor.GREEN+"Homes: "+ChatColor.RESET+ String.join(", ", profile.getHomes().keySet()));
    }

    @CatchUnknown
    public void onUnknown(CommandSender sender) {
        sender.sendMessage("You aren't a player!");
    }

    @HelpCommand
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
