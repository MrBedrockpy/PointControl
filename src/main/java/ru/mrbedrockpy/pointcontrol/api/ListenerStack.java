package ru.mrbedrockpy.pointcontrol.api;

import ru.mrbedrockpy.pointcontrol.api.event.Event;

import java.util.ArrayList;
import java.util.List;

public class ListenerStack<E extends Event> {

    private final List<Listener<E>> listeners = new ArrayList<>();

    public void add(Listener<E> listener) {
        listeners.add(listener);
    }
    public void remove(Listener<E> listener) {
        listeners.remove(listener);
    }

    public void call(E event) {
        listeners.forEach(l -> l.onEvent(event));
    }
}
