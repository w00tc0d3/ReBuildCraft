package net.rebuildcraft.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.rebuildcraft.containers.ContainerMechanicFurnace;
import net.rebuildcraft.tiles.TileFurnace;
import org.lwjgl.opengl.GL11;

/**
 * Created by netchip on 5/3/14.
 */
public class GuiMechanicFurnace extends GuiContainer {
    public GuiMechanicFurnace(InventoryPlayer invPlayer, TileFurnace te) {
        super(new ContainerMechanicFurnace(invPlayer, te));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        drawDefaultBackground();
    }
}
