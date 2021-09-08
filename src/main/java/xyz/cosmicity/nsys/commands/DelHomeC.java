package xyz.cosmicity.nsys.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.cosmicity.nsys.NSys;
import xyz.cosmicity.nsys.storage.Profile;

public class DelHomeC implements CommandExecutor {

    private final NSys pl;

    public DelHomeC(NSys plg) {
        pl = plg;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Profile p = pl.sql().wrapProfile(((Player)sender).getUniqueId());
            if(p != null) {
                if(args.length > 0) {
                    p.delHome(args[0]);
                    sender.sendMessage(ChatColor.GREEN + "Home '"+args[0]+"' deleted.");
                } else if(p.getHomes().size()==1 && p.getHomes().containsKey("home")) {
                    p.delHome("home");
                    sender.sendMessage(ChatColor.GREEN + "Home 'home' deleted.");
                }
                else {
                    sender.sendMessage(ChatColor.GREEN + "Please specify a home to delete.");
                }
            }
        } else {
            sender.sendMessage("This command is limited to players only.");
        }
        return true;
    }
}
