/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.commands.warps;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;

@CommandAlias("warps")
@CommandPermission("nsys.cmd.warps")
public class WarpsCmd extends BaseCommand {

    private final NSys pl;

    public WarpsCmd(NSys pl) {
        this.pl = pl;
    }

    @Default
    public void onWarps(Player p) {
        pl.guiHandler().warps(p);
    }

}
