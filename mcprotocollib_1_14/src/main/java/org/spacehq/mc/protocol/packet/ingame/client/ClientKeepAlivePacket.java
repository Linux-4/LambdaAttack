package org.spacehq.mc.protocol.packet.ingame.client;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;

import java.io.IOException;

import org.spacehq.mc.protocol.packet.MinecraftPacket;

public class ClientKeepAlivePacket extends MinecraftPacket {
    private long id;

    @SuppressWarnings("unused")
    private ClientKeepAlivePacket() {
    }

    public ClientKeepAlivePacket(long id) {
        this.id = id;
    }

    public long getPingId() {
        return this.id;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.id = in.readLong();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeLong(this.id);
    }
}
