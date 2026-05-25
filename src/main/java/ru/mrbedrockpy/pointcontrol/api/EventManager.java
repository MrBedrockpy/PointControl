package ru.mrbedrockpy.pointcontrol.api;

import ru.mrbedrockpy.pointcontrol.api.event.PointCaptureEvent;
import ru.mrbedrockpy.pointcontrol.api.event.PointCapturedTickEvent;
import ru.mrbedrockpy.pointcontrol.api.event.PointLostEvent;

public class EventManager {

    public static final ListenerStack<PointCaptureEvent> POINT_CAPTURE = new ListenerStack<>();
    public static final ListenerStack<PointCapturedTickEvent> POINT_CAPTURED_TICK = new ListenerStack<>();
    public static final ListenerStack<PointLostEvent> POINT_LOST = new ListenerStack<>();

}
