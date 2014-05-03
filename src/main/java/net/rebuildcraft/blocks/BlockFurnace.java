package net.rebuildcraft.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.rebuildcraft.RebuildCraft;
import net.rebuildcraft.tiles.TileFurnace;

/**
 * Created by netchip on 5/2/14.
 */

public class BlockFurnace extends Block {
    public BlockFurnace() {
        super(Material.rock);
        this.setHardness(3.0F);
        this.setResistance(100.0F);
        this.setStepSound(soundTypePiston);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(RebuildCraft.modid + ":blockFurnace");
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileFurnace();
    }
}
