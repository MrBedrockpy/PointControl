package ru.mrbedrockpy.pointcontrol.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import ru.mrbedrockpy.pointcontrol.client.PointHud;

public class UpdateRadiusPacket extends S2CPacket {

    private final String pointId;
    private final double radius;

    public UpdateRadiusPacket(String pointId, double radius) {
        this.pointId = pointId;
        this.radius = radius;
    }

    public UpdateRadiusPacket(FriendlyByteBuf buf) {
        this.pointId = buf.readUtf();
        this.radius = buf.readDouble();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(pointId);
        buf.writeDouble(radius);
    }

    @Override
    protected void onHandle() {
        PointHud.setRadius(pointId, radius);
    }
}