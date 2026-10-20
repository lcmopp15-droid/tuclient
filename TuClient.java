package dev.tuclient;

import dev.tuclient.config.ConfigManager;
import dev.tuclient.gui.ClickGuiScreen;
import dev.tuclient.module.ModuleManager;
import dev.tuclient.module.impl.ComboModule;
import dev.tuclient.module.impl.ReachModule;
import dev.tuclient.module.impl.ZoomModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TuClient implements ClientModInitializer {

    public static final String MOD_ID = "tungtungclient";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding menuKey;

    @Override
    public void onInitializeClient() {
        // 1) Crear módulos y cargar ajustes guardados
        ModuleManager.init();
        ConfigManager.load();

        // 2) Teclas: menú (Right Shift) y zoom (C)
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tuclient.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.tuclient"
        ));
        ZoomModule.setKey(KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tuclient.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.tuclient"
        )));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ClickGuiScreen());
                }
            }
            ModuleManager.tick(client);
        });

        // 3) Dibujar el HUD y la mira
        HudRenderCallback.EVENT.register((ctx, tickDelta) -> ModuleManager.renderHud(ctx));

        // 4) Registrar cada golpe (reach y combo)
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (world.isClient && player == MinecraftClient.getInstance().player) {
                ReachModule.registerHit(player, entity);
                ComboModule.registerHit();
            }
            return ActionResult.PASS;
        });

        LOGGER.info("TungTungClient cargado");
    }
}
