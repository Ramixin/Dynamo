package net.ramixin.dynamo.neoforge;

import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.ramixin.stator.Platform;

import java.nio.file.Path;

public class NeoForgePlatform implements Platform {
    @Override
    public String platformName() {
        return Platform.NEOFORGE;
    }

    @Override
    public Path gameDirectory() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public boolean isDevEnv() {
        return !FMLLoader.getCurrent().isProduction();
    }
}
