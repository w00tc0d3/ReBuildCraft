package net.rebuildcraft.tiles;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Created by netchip on 5/2/14.
 */
public class RegisterTiles {
    public static void registerTiles() {
        GameRegistry.registerTileEntity(TileFurnace.class, "mechanicFurnace");
    }
}
