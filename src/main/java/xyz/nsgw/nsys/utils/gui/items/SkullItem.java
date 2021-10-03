/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.utils.gui.items;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class SkullItem extends MyItem {

    private final UUID OWNER;

    public SkullItem(@NotNull String name, @NotNull UUID owner, @NotNull List<Component> lore) {
        super(name, Material.PLAYER_HEAD);
        this.setLore(lore);
        this.OWNER = owner;
    }

    @Override
    public ItemStack build() {
        return ItemBuilder.skull()
                .owner(Bukkit.getOfflinePlayer(OWNER))
                .name(getName())
                .amount(getQuantity())
                .lore(getLore())
                .build();
    }
}
