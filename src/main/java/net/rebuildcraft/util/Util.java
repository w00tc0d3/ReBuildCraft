package net.rebuildcraft.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

/**
 * Created by netchip on 5/5/14.
 */
public class Util {
    public static boolean fitsFurnaceSlot(ItemStack is) {
        if(is == null)
            return false;
        ItemStack smelt = FurnaceRecipes.smelting().getSmeltingResult(is);
        if(smelt == null)
            return false;
        return true;
    }
}
