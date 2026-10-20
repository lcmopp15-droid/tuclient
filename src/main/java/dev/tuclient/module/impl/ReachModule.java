package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;

/** Muestra la distancia (ojos -> hitbox) del último golpe a una entidad. */
public class ReachModule extends HudModule {

    private static double lastReach = -1;

    public ReachModule() {
        super("reach", "Reach", true);
    }

    public static void registerHit(PlayerEntity player, Entity target) {
        Vec3d eye = player.getEyePos();
        Box box = target.getBoundingBox();

        // Punto de la hitbox más cercano a los ojos
        double dx = Math.max(Math.max(box.minX - eye.x, 0), eye.x - box.maxX);
        double dy = Math.max(Math.max(box.minY - eye.y, 0), eye.y - box.maxY);
        double dz = Math.max(Math.max(box.minZ - eye.z, 0), eye.z - box.maxZ);

        lastReach = Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public String getText() {
        if (lastReach < 0) {
            return "Reach: -";
        }
        return "Reach: " + String.format(Locale.ROOT, "%.2f", lastReach);
    }
}
