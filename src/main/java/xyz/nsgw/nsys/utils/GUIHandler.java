package xyz.nsgw.nsys.utils;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.storage.objects.Profile;

public class GUIHandler {

    private PaginatedGui menu;

    public GUIHandler() {
    }

    public void homes(Profile profile, Player player) {
        PaginatedGui homes = Gui.paginated()
                .title(Component.text("Your Homes"))
                .rows(4)
                .create();
        Location home;
        for(String name : profile.getHomes().keySet()) {
            home = profile.getHome(name);
            assert home != null;
            final String asStr = LocationUtils.simplifyLocation(home);
            homes.addItem(new GuiItem(ItemBuilder.from(Material.ITEM_FRAME)
                    .name(Component.text(ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+ name))
                    .build(), event -> {
                Location loc = profile.getHome(name);
                if(loc==null) return;
                homes.close(player);
                player.teleport(loc);
            }));
        }
        homes.open(player);
    }

}
