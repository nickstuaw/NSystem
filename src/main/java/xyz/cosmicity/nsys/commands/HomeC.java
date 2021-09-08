package xyz.cosmicity.nsys.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.cosmicity.nsys.NSys;
import xyz.cosmicity.nsys.storage.Profile;

import java.util.ArrayList;

public class HomeC implements CommandExecutor {

    private final NSys pl;

    public HomeC(NSys plg) {
        pl = plg;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Profile p = pl.sql().wrap(((Player)sender).getUniqueId());
            if(p != null) {
                if(args.length > 0) {
                    Location loc = p.getHome(args[0]);
                    if (loc!=null){
                        teleport((Player) sender, loc);
                    }
                } else if(p.getHomes().size()>0) {
                    teleport((Player) sender, new ArrayList<>(p.getHomes().values()).get(0));
                } else {
                    sender.sendMessage(ChatColor.GOLD + "You have no homes.");
                }
            }
        } else {
            sender.sendMessage("This command is limited to players only.");
        }
        return true;
    }

    private void teleport(@NotNull Player player, @NotNull Location location) {
        player.teleport(location);
        player.sendMessage(ChatColor.GREEN+"Teleported!");
    }
}
