package xyz.nsgw.nsys.commands.homes;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.objects.Profile;
import xyz.nsgw.nsys.storage.objects.locations.Home;

@CommandAlias("home")
//@CommandPermission("nsys.homes.teleport")
public class HomeCmd extends BaseCommand {

    @Subcommand("tp")
    @CommandCompletion("@homes")
    public static void onHome(Player p, Home home) {
        p.teleport(home);
    }

    @Subcommand("set")
    @CommandCompletion("@homes")
    public void onSetHome(Player p, @Default("homes") String home) {
        Profile profile = NSys.sql().wrapProfile(p.getUniqueId());
        if(profile.setHome(home, p.getLocation())) {
            p.sendMessage(ChatColor.GREEN + "Home '" + home + "' set.");
        } else {
            p.sendMessage(ChatColor.RED + "You have reached your maximum amount of homes.");
        }
    }

    @Subcommand("delete")
    @CommandCompletion("@homes")
    public void onDelHome(Player p, @Default("home") String home) {
        Profile profile = NSys.sql().wrapProfile(p.getUniqueId());
        profile.delHome(home);
        p.sendMessage(ChatColor.GREEN + "Home '"+home+"' deleted.");
    }

    @Subcommand("list|ls")
    public void onHomes(Player p) {
        Profile profile = NSys.sql().wrapProfile(p.getUniqueId());
        p.sendMessage(ChatColor.BLUE +""+profile.getHomes().size()+ "/"+profile.getMaxHomes()+ChatColor.GREEN+" Homes: "+ChatColor.RESET+ String.join(", ", profile.getHomes().keySet()));
    }


    @CatchUnknown
    public static void onUnknown(CommandSender sender) {
        sender.sendMessage("UNKNOWN! You aren't a player...!");
    }

}
