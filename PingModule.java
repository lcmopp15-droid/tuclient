package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

public class PingModule extends HudModule {

    public PingModule() {
        super("ping", "Ping", "Tu latencia con el servidor.", false);
        setDefaultPosition(6, 86);
    }

    @Override
    public String getLabel() {
        return "Ping";
    }

    @Override
    public String getValue() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.getNetworkHandler() == null) {
            return "-";
        }
        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return entry == null ? "-" : entry.getLatency() + " ms";
    }
}
