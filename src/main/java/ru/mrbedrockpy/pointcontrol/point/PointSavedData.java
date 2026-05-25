package ru.mrbedrockpy.pointcontrol.point;

import lombok.Getter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.mrbedrockpy.pointcontrol.client.AssimilationAnimation;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PointSavedData extends SavedData {

    private static final String DATA_NAME = "pointcontrol";

    private final Map<String, Point> points = new HashMap<>();

    public static PointSavedData get() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.overworld().getDataStorage().computeIfAbsent(PointSavedData::load, PointSavedData::new, DATA_NAME);
    }

    public static PointSavedData load(CompoundTag tag) {
        PointSavedData data = new PointSavedData();
        ListTag list = tag.getList("points", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag p = list.getCompound(i);
            String id = p.getString("id");
            ResourceKey<Level> level = ResourceKey.create(Registries.DIMENSION, ResourceLocation
                    .fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, p.getString("level")));
            Vector3f pos = new Vector3f(p.getFloat("x"), p.getFloat("y"), p.getFloat("z"));
            Point point = new Point(id, level, pos, p.getInt("radius"));
            point.loadAssimilationDuration(p.getInt("assimilation-duration"));
            point.loadAssimilationAnimation(AssimilationAnimation.getByName(p.getString("assimilation-animation")));
            data.points.put(id, point);
        }
        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        ListTag list = new ListTag();
        for (Point point : points.values()) {
            CompoundTag p = new CompoundTag();
            p.putString("id", point.getId());
            p.putString("level", point.getLevel().location().toString());
            p.putFloat("x", point.getPosition().x);
            p.putFloat("y", point.getPosition().y);
            p.putFloat("z", point.getPosition().z);
            p.putDouble("radius", point.getRadius());
            p.putInt("assimilation-duration", point.getAssimilationDuration());
            p.putString("assimilation-animation", point.getAssimilationAnimation().name());
            list.add(p);
        }
        tag.put("points", list);
        return tag;
    }
}