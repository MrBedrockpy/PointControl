package ru.mrbedrockpy.pointcontrol.network;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.joml.Vector3f;
import ru.mrbedrockpy.pointcontrol.client.AssimilationAnimation;
import ru.mrbedrockpy.pointcontrol.network.packet.*;

public class NetworkManager {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath("pointcontrol", "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int id = 0;

    public static void register() {
        CHANNEL.messageBuilder(UpdateAssimilationPacket.class, id++)
                .encoder(UpdateAssimilationPacket::encode)
                .decoder(UpdateAssimilationPacket::new)
                .consumerMainThread(UpdateAssimilationPacket::handle)
                .add();

        CHANNEL.messageBuilder(UpdateAssimilationDurationPacket.class, id++)
                .encoder(UpdateAssimilationDurationPacket::encode)
                .decoder(UpdateAssimilationDurationPacket::new)
                .consumerMainThread(UpdateAssimilationDurationPacket::handle)
                .add();

        CHANNEL.messageBuilder(UpdateRadiusPacket.class, id++)
                .encoder(UpdateRadiusPacket::encode)
                .decoder(UpdateRadiusPacket::new)
                .consumerMainThread(UpdateRadiusPacket::handle)
                .add();

        CHANNEL.messageBuilder(AddPointPacket.class, id++)
                .encoder(AddPointPacket::encode)
                .decoder(AddPointPacket::new)
                .consumerMainThread(AddPointPacket::handle)
                .add();

        CHANNEL.messageBuilder(RemovePointPacket.class, id++)
                .encoder(RemovePointPacket::encode)
                .decoder(RemovePointPacket::new)
                .consumerMainThread(RemovePointPacket::handle)
                .add();

        CHANNEL.messageBuilder(UpdateDominatorPacket.class, id++)
                .encoder(UpdateDominatorPacket::encode)
                .decoder(UpdateDominatorPacket::new)
                .consumerMainThread(UpdateDominatorPacket::handle)
                .add();

        CHANNEL.messageBuilder(UpdateAssimilationAnimationPacket.class, id++)
                .encoder(UpdateAssimilationAnimationPacket::encode)
                .decoder(UpdateAssimilationAnimationPacket::new)
                .consumerMainThread(UpdateAssimilationAnimationPacket::handle)
                .add();
    }

    public static void addPoint(String pointId, ResourceKey<Level> level, Vector3f pos, double radius) {
        sendToPlayers(new AddPointPacket(pointId, level, pos, radius));
    }

    public static void removePoint(String pointId) {
        sendToPlayers(new RemovePointPacket(pointId));
    }

    public static void updatePointAssimilation(String pointId, int newAssimilation) {
        sendToPlayers(new UpdateAssimilationPacket(pointId, newAssimilation));
    }

    public static void updateDominator(String pointId, String team) {
        sendToPlayers(new UpdateDominatorPacket(pointId, team));
    }

    public static void updateRadius(String pointId, double radius) {
        sendToPlayers(new UpdateRadiusPacket(pointId, radius));
    }

    public static void updateAssimilationDuration(String pointId, int assimilationDuration) {
        sendToPlayers(new UpdateAssimilationDurationPacket(pointId, assimilationDuration));
    }

    public static void updatePointAnimation(String pointId, AssimilationAnimation animation) {
        sendToPlayers(new UpdateAssimilationAnimationPacket(pointId, animation));
    }
    
    private static void sendToPlayers(Object packet) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers())
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
