package net.ramixin.dynamo.fabric.impl;

import net.fabricmc.loader.api.FabricLoader;
import net.ramixin.stator.Platform;

import java.nio.file.Path;

public class FabricPlatform implements Platform {

    @Override
    public String platformName() {
        return Platform.FABRIC;
    }

    @Override
    public Path gameDirectory() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public boolean isDevEnv() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
