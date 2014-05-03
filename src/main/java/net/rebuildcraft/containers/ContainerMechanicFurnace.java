package net.rebuildcraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.rebuildcraft.tiles.TileFurnace;

/**
 * Created by netchip on 5/2/14.
 */
public class ContainerMechanicFurnace extends Container {
    private TileFurnace tileFurnace;

    public ContainerMechanicFurnace(InventoryPlayer invPlayer, TileFurnace te) {
        this.tileFurnace = te;

        // item
        this.addSlotToContainer(new Slot(te, 0, 56, 17));
        // fuel
        this.addSlotToContainer(new Slot(te, 1, 56, 53));
        // output
        this.addSlotToContainer(new Slot(te, 2, 116, 35));

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
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 < tileFurnace.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, tileFurnace.getSizeInventory(), inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, tileFurnace.getSizeInventory(), false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack) null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
