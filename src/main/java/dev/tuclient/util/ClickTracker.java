package dev.tuclient.util;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

/** Guarda las marcas de tiempo de los clics del último segundo. */
public final class ClickTracker {

    private static final Deque<Long> LEFT = new ArrayDeque<>();
    private static final Deque<Long> RIGHT = new ArrayDeque<>();

    private ClickTracker() {
    }

    public static void register(int button) {
        long now = System.currentTimeMillis();
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            LEFT.addLast(now);
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            RIGHT.addLast(now);
        }
    }

    public static int left() {
        return count(LEFT);
    }

    public static int right() {
        return count(RIGHT);
    }

    private static int count(Deque<Long> clicks) {
        long limit = System.currentTimeMillis() - 1000;
        while (!clicks.isEmpty() && clicks.peekFirst() < limit) {
            clicks.pollFirst();
        }
        return clicks.size();
    }
}
