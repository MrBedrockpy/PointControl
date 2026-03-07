package ru.mrbedrockpy.pointcontrol.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import ru.mrbedrockpy.pointcontrol.client.PointHud;

public class RemovePointPacket extends S2CPacket {

    private final String id;

    public RemovePointPacket(String id) {
        this.id = id;
    }

    public RemovePointPacket(FriendlyByteBuf buf) {
        this.id = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(id);
    }

    @Override
    protected void onHandle() {
        PointHud.removePoint(id);
    }
}