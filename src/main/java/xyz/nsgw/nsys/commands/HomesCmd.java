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

@CommandAlias("homes")
public class HomesCmd extends BaseCommand {

    private final NSys pl;

    public HomesCmd(NSys pl) {
        this.pl = pl;
    }

    @Default
    public void onHomes(Player p) {
        Profile profile = pl.sql().wrapProfile(p.getUniqueId());
        p.sendMessage(ChatColor.BLUE +""+profile.getHomes().size()+ "/10 "+ChatColor.GREEN+"Homes: "+ChatColor.RESET+ String.join(", ", profile.getHomes().keySet()));
    }

    @CatchUnknown
    public void onUnknown(CommandSender sender) {
        sender.sendMessage("You aren't a player!");
    }

}
