package org.spacehq.mc.protocol.packet.ingame.server.world;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;

import java.io.IOException;

import org.spacehq.mc.protocol.data.MagicValues;
import org.spacehq.mc.protocol.data.game.world.map.MapData;
import org.spacehq.mc.protocol.data.game.world.map.MapIcon;
import org.spacehq.mc.protocol.data.game.world.map.MapIconType;
import org.spacehq.mc.protocol.data.message.Message;
import org.spacehq.mc.protocol.packet.MinecraftPacket;

public class ServerMapDataPacket extends MinecraftPacket {
    private int mapId;
    private byte scale;
    private boolean trackingPosition;
    private boolean locked;
    private MapIcon icons[];

    private MapData data;

    @SuppressWarnings("unused")
    private ServerMapDataPacket() {
    }

    public ServerMapDataPacket(int mapId, byte scale, boolean trackingPosition, boolean locked, MapIcon icons[]) {
        this(mapId, scale, trackingPosition, locked, icons, null);
    }

    public ServerMapDataPacket(int mapId, byte scale, boolean trackingPosition, boolean locked, MapIcon icons[], MapData data) {
        this.mapId = mapId;
        this.scale = scale;
        this.trackingPosition = trackingPosition;
        this.locked = locked;
        this.icons = icons;
        this.data = data;
    }

    public int getMapId() {
        return this.mapId;
    }

    public byte getScale() {
        return this.scale;
    }

    public boolean getTrackingPosition() {
        return this.trackingPosition;
    }

    public boolean isLocked() {
        return locked;
    }

    public MapIcon[] getIcons() {
        return this.icons;
    }

    public MapData getData() {
        return this.data;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.mapId = in.readVarInt();
        this.scale = in.readByte();
        this.trackingPosition = in.readBoolean();
        this.locked = in.readBoolean();
        this.icons = new MapIcon[in.readVarInt()];
        for(int index = 0; index < this.icons.length; index++) {
            int type = in.readVarInt();
            int x = in.readUnsignedByte();
            int z = in.readUnsignedByte();
            int rotation = in.readUnsignedByte();
            Message displayName = null;
            if (in.readBoolean()) {
                displayName = Message.fromString(in.readString());
            }
            this.icons[index] = new MapIcon(x, z, MagicValues.key(MapIconType.class, type), rotation, displayName);
        }

        int columns = in.readUnsignedByte();
        if(columns > 0) {
            int rows = in.readUnsignedByte();
            int x = in.readUnsignedByte();
            int y = in.readUnsignedByte();
            byte data[] = in.readBytes(in.readVarInt());
            this.data = new MapData(columns, rows, x, y, data);
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.mapId);
        out.writeByte(this.scale);
        out.writeBoolean(this.trackingPosition);
        out.writeBoolean(this.locked);
        out.writeVarInt(this.icons.length);
        for(int index = 0; index < this.icons.length; index++) {
            MapIcon icon = this.icons[index];
            int type = MagicValues.value(Integer.class, icon.getIconType());
            out.writeVarInt(type);
            out.writeByte(icon.getCenterX());
            out.writeByte(icon.getCenterZ());
            out.writeByte(icon.getIconRotation());
            if (icon.getDisplayName() != null) {
                out.writeBoolean(false);
                out.writeString(icon.getDisplayName().toJsonString());
            } else {
                out.writeBoolean(true);
            }
        }

        if(this.data != null && this.data.getColumns() != 0) {
            out.writeByte(this.data.getColumns());
            out.writeByte(this.data.getRows());
            out.writeByte(this.data.getX());
            out.writeByte(this.data.getY());
            out.writeVarInt(this.data.getData().length);
            out.writeBytes(this.data.getData());
        } else {
            out.writeByte(0);
        }
    }
}
