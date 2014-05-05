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
    public void chunkWatchListener(ChunkWatchEvent.Watch watch) {
        if(map.containsKey(watch.chunk)) {
            entityList = map.get(watch.chunk);
            entityList.add(watch.player);
            map.remove(watch.chunk);
            map.put(watch.chunk, entityList);
        } else {
            entityList.add(watch.player);
            map.put(watch.chunk, entityList);
        }
    }

    @SubscribeEvent
    public void chunkUnWatchListener(ChunkWatchEvent.UnWatch unwatch) {
        if(map.containsKey(unwatch.chunk)) {
            entityList = map.get(unwatch.chunk);
            if(entityList.size() <= 1)
                map.remove(unwatch.chunk);
            else {
                entityList.remove(unwatch.player);
                map.remove(unwatch.chunk);
                map.put(unwatch.chunk, entityList);
            }
        }
    }

    public List<EntityPlayerMP> returnPlayersWatchingChunk(ChunkCoordIntPair chunk) {
        if(map.containsKey(chunk))
            return map.get(chunk);
        return new ArrayList<EntityPlayerMP>(0);
    }
}
