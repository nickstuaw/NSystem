/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.commands.homes;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.objects.Profile;

@CommandAlias("homes")
@CommandPermission("nsys.cmd.homes")
public class HomesCmd extends BaseCommand {

    private final NSys pl;

    public HomesCmd(NSys pl) {
        this.pl = pl;
    }

    @Default
    public void onHomes(Player p) {
        Profile profile = NSys.sql().wrapProfile(p.getUniqueId());
        pl.guiHandler().homes(profile,p);
    }

}
