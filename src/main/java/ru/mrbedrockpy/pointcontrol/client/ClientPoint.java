package ru.mrbedrockpy.pointcontrol.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;
import org.joml.Vector3f;

@Getter
@Setter
@RequiredArgsConstructor
public class ClientPoint {

    private final String id;
    private final ResourceKey<Level> level;
    private final Vector3f pos;
    private final double radius;

    private int assimilationDuration = 30;
    private AssimilationAnimation assimilationAnimation = AssimilationAnimation.UP;

    private int assimilation;
    private Team dominator;

    private float renderX = -1;
    private float renderY = -1;
    private float targetX;
    private float targetY;

    private float animTime = 0f;
    private float alpha = 0f;

    public int getColor() {
        if (this.dominator == null) return 0xFFFFFFFF;
        Integer rgb = this.dominator.getColor().getColor();
        return 0xFF000000 | (rgb != null ? rgb : 0xFFFFFF);
    }
}
