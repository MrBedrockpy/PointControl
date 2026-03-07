package ru.mrbedrockpy.pointcontrol.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import ru.mrbedrockpy.pointcontrol.client.PointHud;

public class UpdateAssimilationDurationPacket extends S2CPacket {

    private final String pointId;
    private final int assimilation;

    public UpdateAssimilationDurationPacket(String pointId, int assimilation) {
        this.pointId = pointId;
        this.assimilation = assimilation;
    }

    public UpdateAssimilationDurationPacket(FriendlyByteBuf buf) {
        this.pointId = buf.readUtf();
        this.assimilation = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(pointId);
        buf.writeInt(assimilation);
    }

    @Override
    protected void onHandle() {
        PointHud.setAssimilationDuration(pointId, assimilation);
    }
}