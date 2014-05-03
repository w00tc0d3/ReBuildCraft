package net.rebuildcraft.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

/**
 * Created by netchip on 5/2/14.
 */
public class RegisterBlocks {
    public final static Block blockFurnace = new BlockFurnace().setBlockName("Mechanic Furnace");
    public static void registerBlocks() {
        GameRegistry.registerBlock(blockFurnace, "mechanicFurnace");
    }
}
