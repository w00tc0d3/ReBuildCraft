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
import net.minecraftforge.common.util.ForgeDirection;
import net.rebuildcraft.RebuildCraft;
import net.rebuildcraft.net.PacketMechanicalFurnace;
import net.rebuildcraft.util.Util;

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
    @MjBattery(maxCapacity = 20000, maxReceivedPerCycle = 128)
    public double mjStored;
    private ItemStack[] itemStacks = new ItemStack[3];
    private String nameInv = "InventoryMechanicFurnace";
    public boolean working = false;

    public int ticks = 0;
    public int ticksToProcess = 60;
    private int mjUsePerTick = 1; // 1 MJ/t

    @Override
    public void updateEntity() {
        if(canCook()) {
            if(mjStored < mjUsePerTick)
                return;

            working = true;
            mjStored -= mjUsePerTick;
            ticks += 1;

            if(ticks == ticksToProcess) {
                smeltItem();
                ticks = 0;
                working = false;
            }
            updateClient();
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
        ForgeDirection fd = ForgeDirection.getOrientation(var1);
        int[] accessSlots = new int[1];
        if(fd == ForgeDirection.UP) {
            accessSlots[0] = 0;
            return accessSlots;
        }
        accessSlots[0] = 2;
        return accessSlots;
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack is, int side) {
        if(is == null)
            return false;
        if(slotID != 0)
            return false;
        if(!Util.fitsFurnaceSlot(is))
            return false;
        if(itemStacks[slotID] == null)
            return true;
        if(is.isItemEqual(itemStacks[slotID]) && (is.stackSize + itemStacks[slotID].stackSize) < getInventoryStackLimit())
            return true;
        return false;
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack is, int side) {
        if(ForgeDirection.getOrientation(side) == ForgeDirection.UP)
            return false;
        if(slotID != 2)
            return false;
        if(itemStacks[slotID] != null)
            return true;
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
    public boolean isItemValidForSlot(int slotID, ItemStack is) {
        if(is == null)
            return false;
        if(slotID != 0)
            return false;
        if(!Util.fitsFurnaceSlot(is))
            return false;
        if(itemStacks[slotID] == null)
            return true;
        if(is.isItemEqual(itemStacks[slotID]))
            return true;
        return false;
    }

    public boolean canCook() {
        if(getStackInSlot(0) == null)
            return false;

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
        PacketMechanicalFurnace packet = new PacketMechanicalFurnace(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, working, mjStored);
        RebuildCraft.packetPipeline.sendToAllWatching(packet, xCoord, zCoord, worldObj);
    }
}
