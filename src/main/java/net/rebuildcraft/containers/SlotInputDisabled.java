package net.rebuildcraft.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.rebuildcraft.tiles.TileMechanicalFurnace;

/**
 * Created by netchip on 5/5/14.
 */
public class SlotInputDisabled extends Slot {
    public SlotInputDisabled(IInventory par1IInventory, int var1, int var2, int var3) {
        super(par1IInventory, var1, var2, var3);
    }

    @Override
    public boolean isItemValid(ItemStack is) {
        return false;
    }
}
