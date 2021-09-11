package xyz.nsgw.nsys.commands.homes;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.storage.objects.locations.Home;

@CommandAlias("home")
//@CommandPermission("nsys.homes.teleport")
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
