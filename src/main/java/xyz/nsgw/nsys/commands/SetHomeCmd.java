package xyz.nsgw.nsys.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.Home;
import xyz.nsgw.nsys.storage.Profile;

@CommandAlias("sethome")
public class SetHomeCmd extends BaseCommand {

    private final NSys pl;

    public SetHomeCmd(NSys pl) {this.pl=pl;}

    @Default
    @CommandCompletion("@home")
    public void onSetHome(Player p, @Default("home") String home) {
        Profile profile = pl.sql().wrapProfile(p.getUniqueId());
        profile.setHome(home, p.getLocation());
        p.sendMessage(ChatColor.GREEN + "Home '" + home + "' set.");
    }

    @CatchUnknown
    public void onUnknown(CommandSender sender) {
        sender.sendMessage("You aren't a player!");
    }

    /*private final NSys pl;

    public SetHomeC(NSys plg) {
        pl = plg;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Profile p = pl.sql().wrapProfile(((Player)sender).getUniqueId());
            if(p != null) {
                Location loc = ((Player) sender).getLocation();
                if(args.length > 0) {
                    if(p.getHomes().size() < 10) {
                        p.setHome(args[0], loc);
                        sender.sendMessage(ChatColor.GREEN + "Home '" + args[0] + "' set.");
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "You have reached the maximum home limit.");
                    }
                } else {
                    if(p.getHomes().size() < 10) {
                        p.setHome("home", loc);
                        sender.sendMessage(ChatColor.GREEN + "Home 'home' set.");
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "You have reached the maximum home limit.");
                    }
                }
            }
        } else {
            sender.sendMessage("This command is limited to players only.");
        }
        return true;*/
}
