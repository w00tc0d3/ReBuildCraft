package net.rebuildcraft.tiles;

import buildcraft.api.mj.MjBattery;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.rebuildcraft.RebuildCraft;
import net.rebuildcraft.net.PacketMechanicalFurnace;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
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
    // in percentages, so from 1% to 100%
    public int progress = 0;
    public boolean working = false;

    private int ticksToProcess = 60;
    private int mjUsePerTick = 1; // 1 MJ/t

    private boolean doOnce = true;

    @Override
    public void updateEntity() {
        if(doOnce) {
            doOnce = false;
        }

        if(canCook()) {
            working = true;
            ticksToProcess -= 1;
            mjStored -= mjUsePerTick;
            if(ticksToProcess == 0) {
                smeltItem();
                ticksToProcess = 100;
                working = false;
            }
        }

        ticks++;
        if(ticks == 10) {
            updateClient();
            ticks = 0;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setDouble("mjStored", mjStored);
        tag.setBoolean("working", working);

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
        working = tag.getBoolean("working");

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

    public boolean canCook() {
        if(getStackInSlot(0) == null)
            return false;

        if(mjStored <= 0) {
            mjStored = 0;
            progress = 0;
            working = false;
            return false;
        }

        ItemStack is = FurnaceRecipes.smelting().getSmeltingResult(getStackInSlot(0));

        if(is == null)
            return false;
        if(getStackInSlot(2) == null)
            return true;
        if(!(is.isItemEqual(getStackInSlot(2))))
            return false;
        if(getStackInSlot(2).stackSize >= getInventoryStackLimit())
            return false;

        int result = getStackInSlot(2).stackSize + is.stackSize;
        return result <= getInventoryStackLimit() && result <= this.getStackInSlot(2).getMaxStackSize();
    }

    /*
     * Returns true on success.
     */
    public boolean smeltItem() {
        if(!(canCook()))
            return false;

        ItemStack is = FurnaceRecipes.smelting().getSmeltingResult(itemStacks[0]);
        if(itemStacks[2] == null)
            itemStacks[2] = is.copy();
        else
            itemStacks[2].stackSize += is.stackSize;
        itemStacks[0].stackSize -= 1;

        if(itemStacks[0].stackSize <= 0)
            itemStacks[0] = null;

        return true;
    }

    public void updateClient() {
        PacketMechanicalFurnace packet = new PacketMechanicalFurnace(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, working);
        RebuildCraft.packetPipeline.sendToAllWatching(packet, xCoord, zCoord, worldObj);
    }
}
