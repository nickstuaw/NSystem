package xyz.nsgw.nsys.commands.homes;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.objects.Profile;


@CommandAlias("delhome")
//@CommandPermission("nsys.homes.delete")
public class DelHomeCmd extends BaseCommand {

    private final NSys pl;

    public DelHomeCmd(NSys pl) {
        this.pl = pl;
    }

    @Default
    @CommandCompletion("@homes")
    public void onDelHome(Player p, @Default("home") String home) {
        Profile profile = pl.sql().wrapProfile(p.getUniqueId());
        profile.delHome(home);
        p.sendMessage(ChatColor.GREEN + "Home '"+home+"' deleted.");
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
