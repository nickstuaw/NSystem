package xyz.nsgw.nsys.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.objects.Profile;

@CommandAlias("afk")
public class AfkCmd extends BaseCommand {

    @Default
    public void onAfk(Player player) {
        Profile p = NSys.sql().wrapProfile(player);
        p.setAfk(!p.isAfk(), false);
    }
}
