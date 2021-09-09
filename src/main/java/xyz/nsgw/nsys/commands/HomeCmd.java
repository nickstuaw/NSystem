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

import java.util.ArrayList;

@CommandAlias("home")
public class HomeCmd extends BaseCommand {

    @Default
    @CommandCompletion("@homes")
    public static void onHome(Player p, Home home) {
        p.teleport(home);
        p.sendMessage(ChatColor.GREEN+"Teleported!");
    }

    @CatchUnknown
    public static void onUnknown(CommandSender sender) {
        sender.sendMessage("UNKNOWN! You aren't a player...!");
    }

}
