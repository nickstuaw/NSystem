package xyz.cosmicity.nsys.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.cosmicity.nsys.NSys;
import xyz.cosmicity.nsys.storage.Profile;

public class HomesC implements CommandExecutor {

    private final NSys pl;

    public HomesC(NSys plg) {
        pl = plg;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Profile p = pl.sql().wrap(((Player)sender).getUniqueId());
            if(p != null) {
                sender.sendMessage(ChatColor.BLUE +""+p.getHomes().size()+ "/10 "+ChatColor.GREEN+"Homes: "+ChatColor.RESET+ String.join(", ", p.getHomes().keySet()));
            }
        } else {
            sender.sendMessage("This command is limited to players only.");
        }
        return true;
    }
}
