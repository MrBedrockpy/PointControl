package ru.mrbedrockpy.pointcontrol.point;

import lombok.Getter;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.mrbedrockpy.pointcontrol.PointControl;
import ru.mrbedrockpy.pointcontrol.api.EventManager;
import ru.mrbedrockpy.pointcontrol.api.event.PointCaptureEvent;
import ru.mrbedrockpy.pointcontrol.api.event.PointCapturedTickEvent;
import ru.mrbedrockpy.pointcontrol.api.event.PointLostEvent;
import ru.mrbedrockpy.pointcontrol.client.AssimilationAnimation;
import ru.mrbedrockpy.pointcontrol.config.Config;
import ru.mrbedrockpy.pointcontrol.network.NetworkManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class Point {

    private final String id;
    private final ResourceKey<Level> level;
    private final Vector3f position;
    private final double radius;
    private Team owner = null;
    private Team dominator = null;
    private int assimilation = 0;
    private AssimilationAnimation assimilationAnimation = AssimilationAnimation.UP;
    private int assimilationDuration = 30;

    private int tick = 0;

    public Point(String id, ResourceKey<Level> level, Vector3f position, double radius) {
        this.id = id;
        this.level = level;
        this.position = position;
        this.radius = radius;
    }

    public void loadAssimilationDuration(int assimilationDuration) {
        this.assimilationDuration = assimilationDuration;
    }

    public void loadAssimilationAnimation(AssimilationAnimation assimilationAnimation) {
        this.assimilationAnimation = assimilationAnimation;
    }

    public void setAssimilationDuration(int assimilationDuration) {
        this.assimilationDuration = assimilationDuration;
        NetworkManager.updateAssimilationDuration(id, this.assimilationDuration);
        PointSavedData.get().setDirty();
    }

    public void setAssimilationAnimation(AssimilationAnimation assimilationAnimation) {
        this.assimilationAnimation = assimilationAnimation;
        NetworkManager.updatePointAnimation(id, this.assimilationAnimation);
        PointSavedData.get().setDirty();
    }

    public void tick() {
        this.tick++;
        if (this.tick % 10 != 0) return;
        this.logic();
        this.spawnBorders();
    }

    private void logic() {
        if (this.owner != null) EventManager.POINT_CAPTURED_TICK.call(
                new PointCapturedTickEvent(this, this.owner));
        Map<Team, Integer> teams = this.getTeams();
        if (teams == null) return;
        Team newDominator = getDominator(teams);
        if (newDominator != null) {
            int newAssimilation = this.assimilation;
            if (Objects.equals(newDominator, this.dominator)) {
                if (this.assimilation < assimilationDuration) {
                    newAssimilation++;
                    if (newAssimilation == assimilationDuration) {
                        EventManager.POINT_CAPTURE.call(new PointCaptureEvent(this, this.dominator));
                        this.owner = newDominator;
                    }
                }
            } else {
                if (this.assimilation > 0) {
                    newAssimilation--;
                    if (newAssimilation == 0) {
                        if (this.owner != null) EventManager.POINT_LOST.call(
                                new PointLostEvent(this, newDominator, this.owner));
                        this.dominator = null;
                        this.owner = null;
                        NetworkManager.updateDominator(this.id, "null");
                    }
                } else if (this.assimilation == 0) {
                    this.dominator = newDominator;
                    newAssimilation++;
                    NetworkManager.updateDominator(this.id, this.dominator.getName());
                }
            }
            if (this.assimilation != newAssimilation) {
                NetworkManager.updatePointAssimilation(this.id, newAssimilation);
                this.assimilation = newAssimilation;
                PointSavedData.get().setDirty();
            }
        } else if (this.owner == null || Config.getInstance().enableDeassimilationWhenNoPlayers) {
            if (this.assimilation > 0 && this.tick % (10 * Config.getInstance().deassimilationPeriod) == 0) {
                this.assimilation--;
                NetworkManager.updatePointAssimilation(this.id, this.assimilation);
                PointSavedData.get().setDirty();
            }
            if (this.assimilation == 0) {
                this.dominator = null;
                this.owner = null;
                NetworkManager.updateDominator(this.id, "null");
            }
        }
    }

    private void spawnBorders() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerLevel level = server.getLevel(this.level);
        if (level == null) return;
        DustParticleOptions dust = getDustParticleOptions();
        double minX = this.position.x - this.radius;
        double maxX = this.position.x + this.radius;
        double minZ = this.position.z - this.radius;
        double maxZ = this.position.z + this.radius;
        double y = this.position.y + 0.2;
        double step = 0.5;
        for (double x = minX; x <= maxX; x += step) {
            level.sendParticles(dust, x, y, minZ, 1, 0, 0, 0, 0);
            level.sendParticles(dust, x, y, maxZ, 1, 0, 0, 0, 0);
        }
        for (double z = minZ; z <= maxZ; z += step) {
            level.sendParticles(dust, minX, y, z, 1, 0, 0, 0, 0);
            level.sendParticles(dust, maxX, y, z, 1, 0, 0, 0, 0);
        }
    }

    private @NotNull DustParticleOptions getDustParticleOptions() {
        Vector3f color;
        if (this.owner != null) {
            Integer rgb = this.owner.getColor().getColor();
            if (rgb != null) color = new Vector3f(
                    ((rgb >> 16) & 0xFF) / 255f,
                    ((rgb >> 8) & 0xFF) / 255f,
                    (rgb & 0xFF) / 255f);
            else color = new Vector3f(1.0f, 1.0f, 1.0f);
        } else color = new Vector3f(1.0f, 1.0f, 1.0f);
        return new DustParticleOptions(color, 1.2f);
    }

    private Map<Team, Integer> getTeams() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerLevel level = server.getLevel(this.level);
        if (level == null) {
            PointControl.LOGGER.warn("Level is null");
            return null;
        }
        AABB box = new AABB(
                this.position.x - this.radius, this.position.y - this.radius, this.position.z - this.radius,
                this.position.x + this.radius, this.position.y + this.radius, this.position.z + this.radius
        );
        Map<Team, Integer> teams = new HashMap<>();
        level.getEntitiesOfClass(ServerPlayer.class, box, player ->
                        player.position().distanceTo(toVec3(this.position)) <= this.radius)
                .stream().map(Entity::getTeam).filter(Objects::nonNull)
                .forEach(team -> teams.put(team, teams.getOrDefault(team, 0) + 1));
        return teams;
    }

    private Team getDominator(Map<Team, Integer> teams) {
        boolean isDraw = true;
        Team dominator = null;
        int dominatorCount = 0;
        for (Map.Entry<Team, Integer> entry : teams.entrySet()) {
            if (dominatorCount < entry.getValue()) {
                dominator = entry.getKey();
                dominatorCount = entry.getValue();
                isDraw = false;
            }
            else if (dominatorCount == entry.getValue()) isDraw = true;
        }
        return isDraw ? null : dominator;
    }

    public static Vec3 toVec3(Vector3f position) {
        return new Vec3(position.x, position.y, position.z);
    }
}
