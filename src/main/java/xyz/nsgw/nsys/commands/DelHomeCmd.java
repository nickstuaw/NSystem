package xyz.nsgw.nsys.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.Profile;


@CommandAlias("delhome")
public class DelHomeCmd extends BaseCommand {

    private final NSys pl;

    public DelHomeCmd(NSys pl) {
        this.pl = pl;
    }

    @Default
    @CommandCompletion("@home")
    public void onDelHome(Player p, @Default("home") String home) {
        Profile profile = pl.sql().wrapProfile(p.getUniqueId());
        profile.delHome(home);
        p.sendMessage(ChatColor.GREEN + "Home '"+home+"' deleted.");
    }

    @CatchUnknown
    public void onUnknown(CommandSender sender) {
        sender.sendMessage("You aren't a player!");
    }

/*
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
    }*/
}
