package ru.mrbedrockpy.pointcontrol.api;

import ru.mrbedrockpy.pointcontrol.api.event.Event;

@FunctionalInterface
public interface Listener<E extends Event> {
    void onEvent(E event);
}
