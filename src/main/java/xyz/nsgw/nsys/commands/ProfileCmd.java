package xyz.nsgw.nsys.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;

@CommandAlias("profile|prof")
public class ProfileCmd extends BaseCommand {

    private final NSys pl;

    public ProfileCmd(NSys plugin) {
        pl = plugin;
    }

    @Default
    public void onProfile(Player player) {
        pl.guiHandler().profile(NSys.sql().wrapProfile(player), player, true);
    }

}
