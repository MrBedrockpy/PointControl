package ru.mrbedrockpy.pointcontrol.client;

import lombok.AllArgsConstructor;
import net.minecraft.client.gui.GuiGraphics;

@AllArgsConstructor
public enum AssimilationAnimation {

    UP((g, size, gaps, x, y, fg, bg, progress) -> {
        g.fill(x + gaps, y + gaps + Math.round(size * (1 - progress)), x + gaps + size, y + gaps + size, fg);
    }),

    DOWN((g, size, gaps, x, y, fg, bg, progress) -> {
        g.fill(x + gaps, y + gaps, x + gaps + size, y + gaps + size + Math.round(1 - size * (1 - progress)), fg);
    }),

    OUT((g, size, gaps, x, y, fg, bg, progress) -> {
        int offset = Math.round(size * progress / 2);
        int startX = x + gaps + size / 2;
        int startY = y + gaps + size / 2;
        g.fill(startX - offset, startY - offset,
                startX + offset, startY + offset, fg);
    }),

    IN((g, size, gaps, x, y, fg, bg, progress) -> {
        g.fill(x + gaps, y + gaps, x + gaps + size, y + gaps + size, fg);
        int offset = Math.round(size * (1 - progress) / 2);
        int startX = x + gaps + size / 2;
        int startY = y + gaps + size / 2;
        g.fill(startX - offset, startY - offset,
                startX + offset, startY + offset, bg);
    }),
    
    ;

    private final IAssimilationAnimation animation;

    public void render(GuiGraphics g, int size, int gaps, int x, int y, int fg, int bg, float progress) {
        animation.render(g, size, gaps, x, y, fg, bg, progress);
    }

    public static AssimilationAnimation getByName(String name) {
        for (AssimilationAnimation animation : AssimilationAnimation.values()) {
            if (animation.name().equalsIgnoreCase(name)) return animation;
        }
        return null;
    }

    @FunctionalInterface
    public interface IAssimilationAnimation {
        void render(GuiGraphics g, int size, int gaps, int x, int y, int fg, int bg, float progress);
    }
}
