package xyz.cosmicity.nebostats.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.cosmicity.nebostats.NeboStats;
import xyz.cosmicity.nebostats.storage.Profile;

import java.util.ArrayList;

public class SetHomeC implements CommandExecutor {

    private final NeboStats pl;

    public SetHomeC(NeboStats plg) {
        pl = plg;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Profile p = pl.sql().wrap(((Player)sender).getUniqueId());
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
        return true;
    }
}
