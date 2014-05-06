package net.rebuildcraft.containers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.rebuildcraft.tiles.TileMechanicalFurnace;
import net.rebuildcraft.util.Util;

/**
 * Created by netchip on 5/2/14.
 */
public class ContainerMechanicalFurnace extends Container {
    private TileMechanicalFurnace tileFurnace;
    private int lastTicks = 0;

    public ContainerMechanicalFurnace(InventoryPlayer invPlayer, TileMechanicalFurnace te) {
        this.tileFurnace = te;

        // item
        this.addSlotToContainer(new SlotFurnace(te, 0, 56, 17));
        // fuel
        this.addSlotToContainer(new Slot(te, 1, 56, 53));
        // output
        this.addSlotToContainer(new SlotOutput(te, 2, 116, 35));

        // player inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // hotbar
        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
        }

    }

    @Override
    public boolean canInteractWith(EntityPlayer var1) {
        return tileFurnace.isUseableByPlayer(var1);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotID)
    {
        ItemStack is = null;
        Slot slot = (Slot) inventorySlots.get(slotID);
        if(slot == null)
            return null;

        if(slot.getHasStack()) {
            ItemStack copy = slot.getStack();
            is = copy.copy();
            if(slotID < tileFurnace.getSizeInventory()) {
                if(!(mergeItemStack(copy, tileFurnace.getSizeInventory(), inventorySlots.size(), true)))
                    return null;
            } else {
                boolean canSmelt = Util.fitsFurnaceSlot(copy);
                if(!canSmelt)
                    return null;
                if(!(mergeItemStack(copy, 0, tileFurnace.getSizeInventory(), false)))
                    return null;
            }
            if(copy.stackSize == 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();
        }

        return is;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for(int i = 0; i < crafters.size(); i++) {
            ICrafting ic = (ICrafting) crafters.get(i);
            if(lastTicks != tileFurnace.ticks)
                ic.sendProgressBarUpdate(this, 0, tileFurnace.ticks);
        }
        lastTicks = tileFurnace.ticks;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        switch(id) {
            case 0:
                tileFurnace.ticks = data;
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting ic) {
        super.addCraftingToCrafters(ic);
        ic.sendProgressBarUpdate(this, 0, tileFurnace.ticks);
    }
}
