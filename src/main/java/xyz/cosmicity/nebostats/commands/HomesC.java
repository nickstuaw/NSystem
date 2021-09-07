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

public class HomesC implements CommandExecutor {

    private final NeboStats pl;

    public HomesC(NeboStats plg) {
        pl = plg;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Profile p = pl.sql().wrap(((Player)sender).getUniqueId());
            if(p != null) {
                sender.sendMessage(ChatColor.GREEN + "Homes: "+ChatColor.RESET+ String.join(", ", p.getHomes().keySet()));
            }
        } else {
            sender.sendMessage("This command is limited to players only.");
        }
        return true;
    }
}
