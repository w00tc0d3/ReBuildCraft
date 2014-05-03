package net.rebuildcraft;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.rebuildcraft.blocks.RegisterBlocks;
import net.rebuildcraft.gui.GuiHandler;
import net.rebuildcraft.net.CommonProxy;
import net.rebuildcraft.tiles.RegisterTiles;

@Mod(modid = RebuildCraft.modid, version = RebuildCraft.version, name = "RebuildCraft")
public class RebuildCraft {
    public static final String modid = "rebuildcraft";
    public static final String version = "0.0.1";

    @Mod.Instance(value = modid)
    public static RebuildCraft instance;

    @SidedProxy(modId = modid, clientSide = "net.rebuildcraft.net.ClientProxy", serverSide = "net.rebuildcraft.net.CommonProxy")
    public static CommonProxy proxy;

    private GuiHandler guiHandler = new GuiHandler();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        RegisterBlocks.registerBlocks();
        RegisterTiles.registerTiles();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerRenderers();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
}
