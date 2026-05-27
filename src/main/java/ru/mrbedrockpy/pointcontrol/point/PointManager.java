package ru.mrbedrockpy.pointcontrol.point;

import lombok.Getter;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.mrbedrockpy.pointcontrol.api.EventManager;
import ru.mrbedrockpy.pointcontrol.api.event.PointManagerTickEvent;
import ru.mrbedrockpy.pointcontrol.network.NetworkManager;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class PointManager {

    @Getter private static final Map<String, Point> points = new HashMap<>();

    public static void tick() {
        points.values().forEach(Point::tick);
        EventManager.POINT_MANAGER_TICK.call(new PointManagerTickEvent());
    }

    public static boolean addPoint(Point point) {
        if (points.containsKey(point.getId())) return false;
        points.put(point.getId(), point);
        PointSavedData.get().setDirty();
        NetworkManager.addPoint(point.getId(), point.getLevel(), point.getPosition(), point.getRadius());
        return true;
    }

    public static void removePoint(String id) {
        points.remove(id);
        PointSavedData.get().setDirty();
        NetworkManager.removePoint(id);
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        points.clear();
        PointSavedData.get();
        points.values().forEach(PointManager::sendAllPointPackets);
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        points.values().forEach(PointManager::sendAllPointPackets);
    }

    private static void sendAllPointPackets(Point point) {
        NetworkManager.addPoint(point.getId(), point.getLevel(), point.getPosition(), point.getRadius());
        NetworkManager.updatePointAssimilation(point.getId(), point.getAssimilation());
        NetworkManager.updateDominator(point.getId(), point.getDominatorName());
        NetworkManager.updateAssimilationDuration(point.getId(), point.getAssimilationDuration());
        NetworkManager.updatePointAnimation(point.getId(), point.getAssimilationAnimation());
        NetworkManager.updateRadius(point.getId(), point.getRadius());
    }
}