package net.ramixin.dynamo.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.ramixin.dynamo.DynamoCommon;
import net.ramixin.dynamo.EntryProvider;
import net.ramixin.dynamo.fabric.impl.FabricPlatform;
import net.ramixin.dynamo.fabric.impl.networking.FabricClientNetworking;
import net.ramixin.dynamo.fabric.impl.networking.FabricNetworking;
import net.ramixin.dynamo.fabric.impl.registration.FabricClientRegistration;
import net.ramixin.dynamo.fabric.impl.registration.FabricRegistration;
import net.ramixin.stator.Platform;
import net.ramixin.stator.entrypoints.Entrypoint;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Dynamo implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer {

    @Override
    public void onInitialize() {
        load(Entrypoint.Side.COMMON, Dynamo::useServerParameters);
    }

    @Override
    public void onInitializeClient() {
        load(Entrypoint.Side.CLIENT, Dynamo::useClientAndServerParameters);
    }

    @Override
    public void onInitializeServer() {
        load(Entrypoint.Side.SERVER, Dynamo::useServerParameters);
    }

    private static void addToList(ModContainer mod, String path, List<Path> list) {
        Optional<Path> maybeFile = mod.findPath(path);
        if(maybeFile.isEmpty()) return;
        list.add(maybeFile.get());
    }

    private static void load(Entrypoint.Side side, Consumer<EntryProvider> providerBuilder) {
        List<Path> dispatcherPaths = new ArrayList<>();
        List<Path> eventPaths = new ArrayList<>();
        List<Path> entrypointPaths = new ArrayList<>();
        for(ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            addToList(mod, DynamoCommon.DISPATCHER_FILE_PATH, dispatcherPaths);
            addToList(mod, DynamoCommon.EVENT_FILE_PATH, eventPaths);
            addToList(mod, DynamoCommon.INITIALIZER_FILE_PATH, entrypointPaths);
        }
        DynamoCommon.loadDispatchersFromFile(dispatcherPaths, Platform.FABRIC);

        EntryProvider entryProvider = DynamoCommon.loadInitializersFromFile(entrypointPaths, side);
        providerBuilder.accept(entryProvider);
        entryProvider.provide();

        DynamoCommon.loadEventsFromFile(eventPaths);
    }

    private static void useServerParameters(EntryProvider provider) {
        provider.setRegistration(new FabricRegistration());
        provider.setNetworking(new FabricNetworking());
        provider.setPlatform(new FabricPlatform());
    }

    private static void useClientAndServerParameters(EntryProvider provider) {
        useServerParameters(provider);
        useClientParameters(provider);
    }

    private static void useClientParameters(EntryProvider provider) {
        provider.setClientRegistration(new FabricClientRegistration());
        provider.setClientNetworking(new FabricClientNetworking());
    }
}
