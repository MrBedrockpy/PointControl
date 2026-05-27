package ru.mrbedrockpy.pointcontrol;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import ru.mrbedrockpy.pointcontrol.command.PointCommand;
import ru.mrbedrockpy.pointcontrol.config.Config;
import ru.mrbedrockpy.pointcontrol.network.NetworkManager;
import ru.mrbedrockpy.pointcontrol.point.PointManager;

@Mod(PointControl.MOD_ID)
public class PointControl {

    public static final String MOD_ID = "pointcontrol";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PointControl() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        NetworkManager.register();
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) PointManager.tick();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        new PointCommand().register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        Config.load();

        LOGGER.info("Capture points: {}", PointManager.getPoints().size());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        Config.save();
    }
}
