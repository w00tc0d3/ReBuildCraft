package net.rebuildcraft.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.rebuildcraft.containers.ContainerMechanicalFurnace;
import net.rebuildcraft.tiles.TileMechanicalFurnace;

/**
 * Created by netchip on 5/3/14.
 */
public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te == null)
            return null;
        switch(ID) {
            case 0:
                if(!(te instanceof TileMechanicalFurnace))
                    return null;
            return new ContainerMechanicalFurnace(player.inventory, (TileMechanicalFurnace) te);

            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te == null)
            return null;
        switch(ID) {
            case 0:
                if(!(te instanceof TileMechanicalFurnace))
                    return null;
                return new GuiMechanicalFurnace(player.inventory, (TileMechanicalFurnace) te);

            default:
                return null;
        }
    }
}
