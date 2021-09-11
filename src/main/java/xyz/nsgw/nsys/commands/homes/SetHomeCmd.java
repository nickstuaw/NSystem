package xyz.nsgw.nsys.commands.homes;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.objects.Profile;

@CommandAlias("sethome")
//@CommandPermission("nsys.homes.set")
public class SetHomeCmd extends BaseCommand {

    private final NSys pl;

    public SetHomeCmd(NSys pl) {this.pl=pl;
    }

    @Default
    @CommandCompletion("@homes")
    public void onSetHome(Player p, @Default("homes") String home) {
        Profile profile = NSys.sql().wrapProfile(p.getUniqueId());
        profile.setHome(home, p.getLocation());
        p.sendMessage(ChatColor.GREEN + "Home '" + home + "' set.");
    }

    @CatchUnknown
    public void onUnknown(CommandSender sender) {
        sender.sendMessage("You aren't a player!");
    }

}
