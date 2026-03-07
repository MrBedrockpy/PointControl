package ru.mrbedrockpy.pointcontrol.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import ru.mrbedrockpy.pointcontrol.client.PointHud;

public class UpdateDominatorPacket extends S2CPacket {

    private final String id;
    private final String team;

    public UpdateDominatorPacket(String id, String team) {
        this.id = id;
        this.team = team;
    }

    public UpdateDominatorPacket(FriendlyByteBuf buf) {
        this.id = buf.readUtf();
        this.team = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(id);
        buf.writeUtf(team);
    }

    @Override
    protected void onHandle() {
        PointHud.updateDominator(id, team);
    };
}