package net.rebuildcraft.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.event.world.ChunkWatchEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by netchip on 5/5/14.
 */
public class ChunkWatchEventListener {
    private List<EntityPlayerMP> entityList = new ArrayList<EntityPlayerMP>();
    private Map<ChunkCoordIntPair, List<EntityPlayerMP>> map = new HashMap<ChunkCoordIntPair, List<EntityPlayerMP>>();

    @SubscribeEvent
    void chunkWatchListener(ChunkWatchEvent.Watch watch) {
        if(map.containsKey(watch.chunk)) {
            entityList = map.get(watch.chunk);
            entityList.add(watch.player);
            map.remove(watch.chunk);
            map.put(watch.chunk, entityList);
        } else {
            entityList.add(watch.player);
            System.out.println(watch.player);
            map.put(watch.chunk, entityList);
        }
    }

    @SubscribeEvent
    void chunkUnWatchListener(ChunkWatchEvent.UnWatch unwatch) {

    }
}
