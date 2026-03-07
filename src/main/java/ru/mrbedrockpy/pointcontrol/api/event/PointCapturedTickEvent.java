package ru.mrbedrockpy.pointcontrol.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.scores.Team;
import ru.mrbedrockpy.pointcontrol.point.Point;

@Getter
@AllArgsConstructor
public class PointCapturedTickEvent implements Event {

    private final Point point;
    private final Team team;

}
