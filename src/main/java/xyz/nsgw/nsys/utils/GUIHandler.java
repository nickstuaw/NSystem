package xyz.nsgw.nsys.utils;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import dev.triumphteam.gui.guis.ScrollingGui;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.nsgw.nsys.storage.objects.Profile;

public class GUIHandler {

    private PaginatedGui menu;

    public GUIHandler() {
    }

    public void profile(Profile profile, Player displayTo) {
        ScrollingGui gui = Gui.scrolling()
                .rows(6)
                .pageSize(45)
                .create();
        ItemStack skull = ItemBuilder.skull().owner(Bukkit.getOfflinePlayer(profile.getKey()))
                .lore().build();
    }

    public void profile(Profile profile) {
        ScrollingGui gui = Gui.scrolling()
                .rows(6)
                .pageSize(45)
                .create();
        ItemStack skull = ItemBuilder.skull().owner(Bukkit.getOfflinePlayer(profile.getKey()))
                .lore().build();
        gui.getFiller().fill(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text(ChatColor.DARK_GRAY+"-")).asGuiItem());
        gui.setItem(0,0,ItemBuilder.from(skull).name(Component.text(ChatColor.WHITE+Bukkit.getOfflinePlayer(profile.getKey()).getName())).asGuiItem());
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

    public void warps(Player player) {
    }

}
