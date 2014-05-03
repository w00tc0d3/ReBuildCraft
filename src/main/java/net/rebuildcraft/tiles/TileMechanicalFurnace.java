package net.rebuildcraft.tiles;

import buildcraft.api.mj.MjBattery;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by netchip on 5/2/14.
 */

public class TileMechanicalFurnace extends TileEntity implements ISidedInventory {
    @MjBattery(maxCapacity = 20000)
    private double mjStored;
    private ItemStack[] itemStacks = new ItemStack[3];
    private int ticks = 0;
    private String nameInv = "InventoryMechanicFurnace";

    @Override
    public void updateEntity() {
        if(ticks == 20) {
            ticks = 0;
        }
        ticks++;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setDouble("mjStored", mjStored);

        NBTTagList nbtTagList = new NBTTagList();
        for(int i = 0; i < itemStacks.length; i++) {
            if(itemStacks[i] != null) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setByte("Slot", (byte) i);
                itemStacks[i].writeToNBT(nbt);
                nbtTagList.appendTag(nbt);
            }
        }
        tag.setTag("Items", nbtTagList);
        tag.setString("CustomName", nameInv);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        mjStored = tag.getDouble("mjStored");

        NBTTagList nbtTagList = tag.getTagList("Items", 10);
        itemStacks = new ItemStack[getSizeInventory()];

        if (tag.hasKey("CustomName", 8)) {
            nameInv = tag.getString("CustomName");
        }

        for (int i = 0; i < nbtTagList.tagCount(); i++) {
            NBTTagCompound nbt = nbtTagList.getCompoundTagAt(i);
            int j = nbt.getByte("Slot") & 255;

            if (j >= 0 && j < itemStacks.length) {
                itemStacks[j] = ItemStack.loadItemStackFromNBT(nbt);
            }
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int var1, ItemStack var2, int var3) {
        return false;
    }

    @Override
    public boolean canExtractItem(int var1, ItemStack var2, int var3) {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return itemStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int var1) {
        return itemStacks[var1];
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2) {
        if(itemStacks[var1] == null)
            return null;
        ItemStack itemStack;
        if(itemStacks[var1].stackSize <= var2) {
            itemStack = itemStacks[var1];
            itemStacks[var1] = null;
            this.markDirty();
            return itemStack;
        }
        itemStack = itemStacks[var1].splitStack(var2);

        if (itemStacks[var1].stackSize == 0)
            itemStacks[var1] = null;

        this.markDirty();
        return itemStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1) {
        if(itemStacks[var1] == null)
            return null;

        ItemStack itemStack;
        itemStack = itemStacks[var1];
        itemStacks[var1] = null;

        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2) {
        itemStacks[var1] = var2;

        if (var2 != null && var2.stackSize > this.getInventoryStackLimit()) {
            var2.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return nameInv;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1) {
        if(worldObj.getTileEntity(xCoord, yCoord, zCoord) != this)
            return false;

        return var1.getDistanceSq((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        return false;
    }


}
