package xyz.nsgw.nsys.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
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

    public SetHomeCmd(NSys pl) {this.pl=pl;
    }

    @Default
    @CommandCompletion("@home")
    public void onSetHome(Player p, @Default("homes") String home) {
        Profile profile = pl.sql().wrapProfile(p.getUniqueId());
        profile.setHome(home, p.getLocation());
        p.sendMessage(ChatColor.GREEN + "Home '" + home + "' set.");
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
