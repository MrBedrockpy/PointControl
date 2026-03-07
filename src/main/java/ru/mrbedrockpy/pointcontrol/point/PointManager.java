package ru.mrbedrockpy.pointcontrol.point;

import lombok.Getter;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.mrbedrockpy.pointcontrol.network.NetworkManager;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class PointManager {

    @Getter private static final Map<String, Point> points = new HashMap<>();

    public static void tick() {
        points.values().forEach(Point::tick);
    }

    public static boolean addPoint(Point point) {
        if (points.containsKey(point.getId())) return false;
        points.put(point.getId(), point);
        PointSavedData data = PointSavedData.get();
        data.getPoints().put(point.getId(), point);
        data.setDirty();
        NetworkManager.addPoint(point.getId(), point.getLevel(), point.getPosition(), point.getRadius());
        return true;
    }

    public static void removePoint(String id) {
        points.remove(id);
        PointSavedData data = PointSavedData.get();
        data.getPoints().remove(id);
        data.setDirty();
        NetworkManager.removePoint(id);
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        points.clear();
        PointSavedData data = PointSavedData.get();
        points.putAll(data.getPoints());
        points.values().forEach(PointManager::sendAllPointPackets);
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        points.values().forEach(PointManager::sendAllPointPackets);
    }

    private static void sendAllPointPackets(Point point) {
        NetworkManager.addPoint(point.getId(), point.getLevel(), point.getPosition(), point.getRadius());
        NetworkManager.updatePointAssimilation(point.getId(), point.getAssimilation());
        NetworkManager.updateDominator(point.getId(), String.valueOf(point.getDominator()));
        NetworkManager.updateAssimilationDuration(point.getId(), point.getAssimilationDuration());
        NetworkManager.updatePointAnimation(point.getId(), point.getAssimilationAnimation());
    }
}