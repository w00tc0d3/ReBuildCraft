package net.rebuildcraft.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

/**
 * Created by netchip on 5/6/14.
 */
public class SlotFurnace extends Slot {
    public SlotFurnace(IInventory par1IInventory, int par2, int par3, int par4) {
        super(par1IInventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(ItemStack is) {
        if(is == null)
            return false;
        ItemStack smelt = FurnaceRecipes.smelting().getSmeltingResult(is);
        if(smelt == null)
            return false;
        return true;
    }
}
