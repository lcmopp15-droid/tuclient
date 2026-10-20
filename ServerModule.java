package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

public class ServerModule extends HudModule {

    public ServerModule() {
        super("server", "Servidor", "La dirección del servidor donde juegas.", false);
        setDefaultPosition(170, 126);
    }

    @Override
    public String getLabel() {
        return "IP";
    }

    @Override
    public String getValue() {
        ServerInfo info = MinecraftClient.getInstance().getCurrentServerEntry();
        return info == null ? "Un jugador" : info.address;
    }
}
