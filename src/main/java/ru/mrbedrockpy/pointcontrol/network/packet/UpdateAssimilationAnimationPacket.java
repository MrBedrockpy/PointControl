package ru.mrbedrockpy.pointcontrol.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import ru.mrbedrockpy.pointcontrol.client.AssimilationAnimation;
import ru.mrbedrockpy.pointcontrol.client.PointHud;

public class UpdateAssimilationAnimationPacket extends S2CPacket {

    private final String pointId;
    private final AssimilationAnimation animation;

    public UpdateAssimilationAnimationPacket(String pointId, AssimilationAnimation animation) {
        this.pointId = pointId;
        this.animation = animation;
    }

    public UpdateAssimilationAnimationPacket(FriendlyByteBuf buf) {
        this.pointId = buf.readUtf();
        this.animation = AssimilationAnimation.getByName(buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(pointId);
        buf.writeUtf(this.animation.name());
    }

    @Override
    protected void onHandle() {
        if (animation != null) PointHud.setAssimilationAnimation(pointId, animation);
    }
}
