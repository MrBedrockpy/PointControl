package ru.mrbedrockpy.pointcontrol.network.packet;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import ru.mrbedrockpy.pointcontrol.client.PointHud;

public class AddPointPacket extends S2CPacket {

    private final String id;
    private final ResourceKey<Level> level;
    private final Vector3f pos;
    private final double radius;

    public AddPointPacket(String id, ResourceKey<Level> level, Vector3f pos, double radius) {
        this.id = id;
        this.level = level;
        this.pos = pos;
        this.radius = radius;
    }

    public AddPointPacket(FriendlyByteBuf buf) {
        this.id = buf.readUtf();
        this.level = buf.readResourceKey(Registries.DIMENSION);
        this.pos = buf.readVector3f();
        this.radius = buf.readDouble();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(id);
        buf.writeResourceKey(level);
        buf.writeVector3f(pos);
        buf.writeDouble(radius);
    }

    @Override
    protected void onHandle() {
        PointHud.addPoint(id, level, pos, radius);
    }
}