package dev.tuclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.tuclient.TuClient;
import dev.tuclient.module.ClientModule;
import dev.tuclient.module.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Guarda y carga los ajustes en .minecraft/config/tuclient.json */
public final class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ConfigManager() {
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("tuclient.json");
    }

    public static void load() {
        Path file = file();
        if (!Files.exists(file)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            for (ClientModule module : ModuleManager.all()) {
                if (root.has(module.getId())) {
                    module.load(root.getAsJsonObject(module.getId()));
                }
            }
        } catch (Exception e) {
            TuClient.LOGGER.error("No se pudo leer la configuración, se usan los valores por defecto", e);
        }
    }

    public static void save() {
        JsonObject root = new JsonObject();
        for (ClientModule module : ModuleManager.all()) {
            JsonObject json = new JsonObject();
            module.save(json);
            root.add(module.getId(), json);
        }
        try {
            Path file = file();
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(root, writer);
            }
        } catch (IOException e) {
            TuClient.LOGGER.error("No se pudo guardar la configuración", e);
        }
    }
}
