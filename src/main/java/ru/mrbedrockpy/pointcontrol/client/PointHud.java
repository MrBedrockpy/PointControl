package ru.mrbedrockpy.pointcontrol.client;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;
import ru.mrbedrockpy.pointcontrol.PointControl;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = PointControl.MOD_ID, value = Dist.CLIENT)
public class PointHud {

    private static final int pointSize = 50;
    private static final int pointGaps = pointSize / 10;
    private static final int margin = 15;
    private static final int bgColor = 0x999A9A9A;

    private static final Map<String, ClientPoint> points = new HashMap<>();

    private static final float ANIMATION_DURATION = 60f; // in ticks

    public static void addPoint(String id, ResourceKey<Level> level, Vector3f pos, double radius) {
        ClientPoint p = new ClientPoint(id, level, pos, radius);
        Window window = Minecraft.getInstance().getWindow();
        p.setRenderX(window.getGuiScaledWidth() / 2f);
        p.setRenderY(-60);
        p.setAlpha(0f);
        p.setAnimTime(0f);
        points.put(id, p);
    }

    public static void removePoint(String id) {
        points.remove(id);
    }

    public static void updateAssimilation(String point, int assimilation) {
        ClientPoint p = points.get(point);
        if (p != null) p.setAssimilation(assimilation);
    }

    public static void updateDominator(String point, String teamName) {
        ClientPoint p = points.get(point);
        if (p != null) p.setDominator(getTeam(teamName));
    }

    private static Team getTeam(String teamName) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return null;
        for (PlayerTeam t : level.getScoreboard().getPlayerTeams()) {
            if (t.getName().equals(teamName)) return t;
        }
        return null;
    }

    public static void setAssimilationDuration(String pointId, int assimilationDuration) {
        ClientPoint p = points.get(pointId);
        if (p != null) p.setAssimilationDuration(assimilationDuration);
    }

    public static void setAssimilationAnimation(String pointId, AssimilationAnimation animation) {
        ClientPoint p = points.get(pointId);
        if (p != null) p.setAssimilationAnimation(animation);
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        try {
            if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) return;
            GuiGraphics g = event.getGuiGraphics();
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            Window window = mc.getWindow();
            int guiW = window.getGuiScaledWidth();
            int baseY = 20;
            int count = points.size();
            if (count == 0) return;
            int totalWidth = count * pointSize + (count - 1) * margin;
            float startX = (guiW - totalWidth) / 2f;
            int index = 0;
            for (ClientPoint point : points.values()) {
                float targetX = startX + index * (pointSize + margin);
                point.setTargetX(targetX);
                point.setTargetY(baseY);
                if (point.getRenderX() < 0) {
                    point.setRenderX(targetX);
                    point.setRenderY(-60);
                }
                point.setAnimTime(Math.min(point.getAnimTime() + 1f, ANIMATION_DURATION));
                float t = point.getAnimTime() / ANIMATION_DURATION;
                float easedMove = easeOutBack(t);
                float easedFade = easeInOut(t);
                point.setRenderX(lerp(point.getRenderX(), point.getTargetX(), easedMove * 0.2f));
                point.setRenderY(lerp(point.getRenderY(), point.getTargetY(), easedMove * 0.2f));
                point.setAlpha(easedFade);
                drawPoint(g, (int) point.getRenderX(), (int) point.getRenderY(), point);
                index++;
            }
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    private static void drawPoint(GuiGraphics g, int x, int y, ClientPoint point) {
        int bg = applyAlpha(bgColor, point.getAlpha());
        int fg = applyAlpha(point.getColor(), point.getAlpha());
        g.fill(x, y, x + pointSize, y + pointSize, bg);
        point.getAssimilationAnimation().render(g, pointSize - pointGaps * 2, pointGaps, x, y,
                fg, bg, (float) point.getAssimilation() / point.getAssimilationDuration());
        scaledText(g, 2f, x, y, String.valueOf(point.getId().toUpperCase().charAt(0)));
    }

    private static int applyAlpha(int color, float animAlpha) {
        int baseAlpha = (color >> 24) & 0xFF;
        int finalAlpha = (int)(baseAlpha * animAlpha);

        return (finalAlpha << 24) | (color & 0x00FFFFFF);
    }

    private static void scaledText(GuiGraphics g, float scale, int x, int y, String text) {
        g.pose().pushPose();
        g.pose().scale(scale, scale, scale);
        float scaledX = (x + pointSize / 2f) / scale;
        float scaledY = (y + pointSize / 2f - (9 * scale / 2f)) / scale;
        g.drawCenteredString(Minecraft.getInstance().font,
                text, (int)scaledX, (int)scaledY, 0xFFFFFFFF);
        g.pose().popPose();
    }

    private static float lerp(float current, float target, float speed) {
        return current + (target - current) * speed;
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        return 1 + c3 * (float)Math.pow(t - 1, 3) + c1 * (float)Math.pow(t - 1, 2);
    }

    private static float easeInOut(float t) {
        return t < 0.5f
                ? 2f * t * t
                : 1f - (float)Math.pow(-2f * t + 2f, 2f) / 2f;
    }
}
