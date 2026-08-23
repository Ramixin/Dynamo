package net.ramixin.dynamo.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.ramixin.dynamo.DynamoCommon;
import net.ramixin.dynamo.EntryProvider;
import net.ramixin.dynamo.LoadData;
import net.ramixin.dynamo.fabric.impl.FabricPlatform;
import net.ramixin.dynamo.fabric.impl.networking.FabricClientNetworking;
import net.ramixin.dynamo.fabric.impl.networking.FabricNetworking;
import net.ramixin.dynamo.fabric.impl.registration.FabricClientRegistration;
import net.ramixin.dynamo.fabric.impl.registration.FabricRegistration;
import net.ramixin.stator.Platform;
import net.ramixin.stator.entrypoints.Phase;
import net.ramixin.stator.entrypoints.Side;
import net.ramixin.stator.networking.ClientNetworking;
import net.ramixin.stator.networking.Networking;
import net.ramixin.stator.registration.ClientRegistration;
import net.ramixin.stator.registration.Registration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Dynamo implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer {

    private static final LoadData loadData = getLoadData();
    private static final Registration registration = new FabricRegistration();
    private static final ClientRegistration clientRegistration = new FabricClientRegistration();
    private static final Networking networking = new FabricNetworking();
    private static final ClientNetworking clientNetworking = new FabricClientNetworking();
    private static final Platform platform = new FabricPlatform();
    
    @Override
    public void onInitialize() {
        loadInit(Side.COMMON, Dynamo::useServerParameters);
        loadSetup(Side.COMMON, Dynamo::useServerParameters);
        loadComplete(Side.COMMON, Dynamo::useServerParameters);

    }

    @Override
    public void onInitializeClient() {
        loadInit(Side.CLIENT, Dynamo::useClientAndServerParameters);
        loadSetup(Side.CLIENT, Dynamo::useClientAndServerParameters);
        loadComplete(Side.CLIENT, Dynamo::useClientAndServerParameters);
    }

    @Override
    public void onInitializeServer() {
        loadInit(Side.SERVER, Dynamo::useServerParameters);
        loadSetup(Side.SERVER, Dynamo::useServerParameters);
        loadComplete(Side.SERVER, Dynamo::useServerParameters);
    }

    private static LoadData getLoadData() {
        List<Path> dispatcherPaths = new ArrayList<>();
        List<Path> eventPaths = new ArrayList<>();
        List<Path> entrypointPaths = new ArrayList<>();
        for(ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            addToList(mod, DynamoCommon.DISPATCHER_FILE_PATH, dispatcherPaths);
            addToList(mod, DynamoCommon.EVENT_FILE_PATH, eventPaths);
            addToList(mod, DynamoCommon.INITIALIZER_FILE_PATH, entrypointPaths);
        }
        return new LoadData(dispatcherPaths, eventPaths, entrypointPaths);
    }
    
    private static void addToList(ModContainer mod, String path, List<Path> list) {
        Optional<Path> maybeFile = mod.findPath(path);
        if(maybeFile.isEmpty()) return;
        list.add(maybeFile.get());
    }

    private static void loadInit(Side side, Consumer<EntryProvider> providerBuilder) {
        DynamoCommon.loadDispatchersFromFile(loadData.dispatchers(), Platform.FABRIC);

        EntryProvider entryProvider = DynamoCommon.loadInitializersFromFile(loadData.entrypoints(), side, Phase.INIT);
        providerBuilder.accept(entryProvider);
        entryProvider.provide();
    }

    private static void loadSetup(Side side, Consumer<EntryProvider> providerBuilder) {
        EntryProvider entryProvider = DynamoCommon.loadInitializersFromFile(loadData.entrypoints(), side, Phase.SETUP);
        providerBuilder.accept(entryProvider);
        entryProvider.provide();

        DynamoCommon.loadEventsFromFile(loadData.events());
    }

    private static void loadComplete(Side side, Consumer<EntryProvider> providerBuilder) {
        EntryProvider entryProvider = DynamoCommon.loadInitializersFromFile(loadData.entrypoints(), side, Phase.COMPLETE);
        providerBuilder.accept(entryProvider);
        entryProvider.provide();
    }

    private static void useServerParameters(EntryProvider provider) {
        provider.setRegistration(registration);
        provider.setNetworking(networking);
        provider.setPlatform(platform);
    }

    private static void useClientAndServerParameters(EntryProvider provider) {
        useServerParameters(provider);
        useClientParameters(provider);
    }

    private static void useClientParameters(EntryProvider provider) {
        provider.setClientRegistration(clientRegistration);
        provider.setClientNetworking(clientNetworking);
    }
}
