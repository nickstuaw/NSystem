/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.utils.gui.items;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ClickableSkullItem extends ActiveItem {

    private final UUID OWNER;

    public ClickableSkullItem(@NotNull String name, @NotNull UUID owner, @NotNull GuiAction<InventoryClickEvent> action, @NotNull List<Component> lore) {
        super(name, Material.PLAYER_HEAD, action);
        this.setLore(lore);
        this.OWNER = owner;
    }

    @Override
    public GuiItem build() {
        return ItemBuilder.skull()
                .owner(Bukkit.getOfflinePlayer(OWNER))
                .name(getName())
                .amount(getQuantity())
                .lore(getLore())
                .asGuiItem();
    }
}
