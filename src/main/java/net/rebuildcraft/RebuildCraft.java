package net.rebuildcraft;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.rebuildcraft.blocks.RegisterBlocks;
import net.rebuildcraft.events.ChunkWatchEventListener;
import net.rebuildcraft.gui.GuiHandler;
import net.rebuildcraft.net.CommonProxy;
import net.rebuildcraft.net.PacketMechanicalFurnace;
import net.rebuildcraft.net.PacketPipeline;
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
    public static final PacketPipeline packetPipeline = new PacketPipeline();
    public static final ChunkWatchEventListener chunkListener = new ChunkWatchEventListener();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        RegisterBlocks.registerBlocks();
        RegisterTiles.registerTiles();
        MinecraftForge.EVENT_BUS.register(chunkListener);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerRenderers();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
        packetPipeline.initialise();

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        packetPipeline.postInitialise();
    }
}
