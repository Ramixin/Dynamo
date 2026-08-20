package net.ramixin.dynamo.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.jarcontents.JarContents;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.ramixin.dynamo.DynamoCommon;
import net.ramixin.dynamo.EntryProvider;
import net.ramixin.dynamo.neoforge.impl.networking.NeoForgeClientNetworking;
import net.ramixin.dynamo.neoforge.impl.networking.NeoforgeNetworking;
import net.ramixin.dynamo.neoforge.impl.registration.NeoForgeClientRegistration;
import net.ramixin.dynamo.neoforge.impl.registration.NeoForgeRegistration;
import net.ramixin.stator.Platform;
import net.ramixin.stator.entrypoints.Entrypoint;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@Mod("dynamo")
public class Dynamo {

    public Dynamo(IEventBus modEventBus) {
        modEventBus.addListener(Dynamo::initEvent);
        modEventBus.addListener(Dynamo::initClientEvent);
        modEventBus.addListener(Dynamo::initDedicatedServerEvent);
        modEventBus.addListener(Dynamo::regEvent);
    }

    private static void regEvent(RegisterEvent event) {
        DynamoCommon.LOGGER.info("REG EVENT RAN");
    }

    public static void initEvent(FMLCommonSetupEvent event) {
        load(Entrypoint.Side.COMMON, event.getContainer().getEventBus(), Dynamo::useServerParameters);
    }

    public static void initClientEvent(FMLClientSetupEvent event) {
        load(Entrypoint.Side.CLIENT, event.getContainer().getEventBus(), Dynamo::useClientAndServerParameters);
    }

    public static void initDedicatedServerEvent(FMLDedicatedServerSetupEvent event) {
        load(Entrypoint.Side.SERVER, event.getContainer().getEventBus(), Dynamo::useServerParameters);
    }

    private static void addToList(JarContents mod, String path, List<Path> list) {
        Optional<URI> maybeFile = mod.findFile(path);
        if(maybeFile.isEmpty()) return;
        list.add(Paths.get(maybeFile.get()));
    }

    private static void load(Entrypoint.Side side, IEventBus bus, BiConsumer<EntryProvider, IEventBus> providerBuilder) {
        List<Path> dispatcherPaths = new ArrayList<>();
        List<Path> eventPaths = new ArrayList<>();
        List<Path> entrypointPaths = new ArrayList<>();
        for(IModFileInfo info : ModList.get().getModFiles()) {
            JarContents mod = info.getFile().getContents();
            addToList(mod, DynamoCommon.DISPATCHER_FILE_PATH, dispatcherPaths);
            addToList(mod, DynamoCommon.EVENT_FILE_PATH, eventPaths);
            addToList(mod, DynamoCommon.INITIALIZER_FILE_PATH, entrypointPaths);
        }
        DynamoCommon.loadDispatchersFromFile(dispatcherPaths, Platform.NEOFORGE);

        EntryProvider entryProvider = DynamoCommon.loadInitializersFromFile(entrypointPaths, side);
        providerBuilder.accept(entryProvider, bus);
        entryProvider.provide();

        DynamoCommon.loadEventsFromFile(eventPaths);
    }

    private static void useServerParameters(EntryProvider provider, IEventBus eventBus) {
        provider.setRegistration(new NeoForgeRegistration(eventBus));
        provider.setNetworking(new NeoforgeNetworking());
        provider.setPlatform(new NeoForgePlatform());
    }

    private static void useClientAndServerParameters(EntryProvider provider, IEventBus eventBus) {
        useServerParameters(provider, eventBus);
        useClientParameters(provider, eventBus);
    }

    private static void useClientParameters(EntryProvider provider, IEventBus eventBus) {
        provider.setClientRegistration(new NeoForgeClientRegistration(eventBus));
        provider.setClientNetworking(new NeoForgeClientNetworking());
    }

}
