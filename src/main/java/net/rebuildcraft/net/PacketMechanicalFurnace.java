package net.rebuildcraft.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.rebuildcraft.tiles.TileMechanicalFurnace;

/**
 * Created by netchip on 5/4/14.
 */
public class PacketMechanicalFurnace extends AbstractPacket {
    private int  dim, x, y, z;
    private boolean working;

    public PacketMechanicalFurnace() {

    }

    public PacketMechanicalFurnace(int dim, int x, int y, int z, boolean working) {
        this.dim = dim;
        this.x = x;
        this.y = y;
        this.z = z;
        this.working = working;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        buffer.writeInt(dim);
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeBoolean(working);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        dim = buffer.readInt();
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        working = buffer.readBoolean();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        TileEntity te = player.worldObj.getTileEntity(x, y, z);
        if(te instanceof TileMechanicalFurnace) {
            ((TileMechanicalFurnace) te).working = this.working;
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {

    }
}
