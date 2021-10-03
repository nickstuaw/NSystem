/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.utils.gui.items;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import xyz.nsgw.nsys.utils.DisplayUtils;

import java.util.Arrays;
import java.util.List;

public class MyItem {

    private Component name;
    private int quantity;
    private Material material;
    private List<Component> lore;

    public MyItem(@NotNull final String name, @NotNull final Material material, @NotNull final Component... lore) {
        setName(DisplayUtils.txt(name));
        setMaterial(material);
        setLore(Arrays.asList(lore));
        setQuantity(1);
    }

    public Component getName() {
        return name;
    }

    public void setName(Component name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public List<Component> getLore() {
        return lore;
    }

    public void setLore(List<Component> lore) {
        this.lore = lore;
    }

    public GuiItem build() {
        return ItemBuilder.from(material)
                .name(name)
                .amount(quantity)
                .lore(lore)
                .asGuiItem();
    }
}
