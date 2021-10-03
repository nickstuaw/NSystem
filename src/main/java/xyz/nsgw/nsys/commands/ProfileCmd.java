/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.objects.Profile;
import xyz.nsgw.nsys.utils.DisplayUtils;

import static xyz.nsgw.nsys.utils.DisplayUtils.txt;

@CommandAlias("profile|prof")
@CommandPermission("nsys.cmd.profile")
public class ProfileCmd extends BaseCommand {

    private final NSys pl;

    public ProfileCmd(NSys plugin) {
        pl = plugin;
    }

    @Default
    public void onProfile(Player player) {
        pl.guiHandler().profile(NSys.sql().wrapProfile(player), player, true);
    }

    @Subcommand("view")
    @CommandCompletion("@players")
    public void onView(CommandSender s, OfflinePlayer target) {
        Profile p = NSys.sql().wrapProfile(target.getUniqueId());
        if(s instanceof Player) {
            pl.guiHandler().profile(p, (Player) s, false);
            return;
        }
        s.sendMessage(txt(ChatColor.YELLOW+target.getName()+"'s profile:\n"+DisplayUtils.rawAdminProfileMeta(p, target)));
    }

}
