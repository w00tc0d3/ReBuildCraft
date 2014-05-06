package net.rebuildcraft.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.rebuildcraft.RebuildCraft;
import net.rebuildcraft.containers.ContainerMechanicalFurnace;
import net.rebuildcraft.tiles.TileMechanicalFurnace;
import org.lwjgl.opengl.GL11;

/**
 * Created by netchip on 5/3/14.
 */
public class GuiMechanicalFurnace extends GuiContainer {
    private TileMechanicalFurnace te;
    private float convertPercentage;
    private final ResourceLocation texture = new ResourceLocation(RebuildCraft.modid + ":textures/gui/container/furnace.png");

    public GuiMechanicalFurnace(InventoryPlayer invPlayer, TileMechanicalFurnace te) {
        super(new ContainerMechanicalFurnace(invPlayer, te));
        this.te = te;
    }

    // From @AEnterpise's Buildcraft Additions
    int headerColour = 0xe1c92f;
    int subheaderColour = 0xaaafb8;
    int textColour = 0x000000;

    @Override
    protected void drawGuiContainerForegroundLayer(int var1, int var2) {
        super.drawGuiContainerForegroundLayer(var1, var2);
        fontRendererObj.drawString("Mechanical Furnace", getOffset("Mechanical Furnace"), 5, textColour);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        convertPercentage = (float) te.ticks / (float) te.ticksToProcess;

        this.drawTexturedModalRect(x + 79, y + 34, 176, 14, (int) ((convertPercentage * 24) + 1), 16);
    }

    protected int getOffset(String s) {
        return(xSize - fontRendererObj.getStringWidth(s)) / 2;
    }
}
