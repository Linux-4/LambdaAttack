package org.spacehq.mc.protocol.packet.ingame.client.window;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;

import java.io.IOException;

import org.spacehq.mc.protocol.data.game.entity.metadata.ItemStack;
import org.spacehq.mc.protocol.packet.MinecraftPacket;
import org.spacehq.mc.protocol.util.NetUtil;

public class ClientEditBookPacket extends MinecraftPacket {
    private ItemStack book;
    private boolean isSigning;

    @SuppressWarnings("unused")
    private ClientEditBookPacket() {
    }

    public ClientEditBookPacket(ItemStack book, boolean isSigning) {
        this.book = book;
        this.isSigning = isSigning;
    }

    public ItemStack getBook() {
        return this.book;
    }

    public boolean getIsSigning() {
        return this.isSigning;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.book = NetUtil.readItem(in);
        this.isSigning = in.readBoolean();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        NetUtil.writeItem(out, this.book);
        out.writeBoolean(this.isSigning);
    }
}
