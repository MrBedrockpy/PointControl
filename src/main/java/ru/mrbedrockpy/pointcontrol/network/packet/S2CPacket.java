package ru.mrbedrockpy.pointcontrol.network.packet;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class S2CPacket {

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::onHandle));
        ctx.get().setPacketHandled(true);
    }

    protected abstract void onHandle();

}
