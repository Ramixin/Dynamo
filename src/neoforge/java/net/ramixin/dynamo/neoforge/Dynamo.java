package net.ramixin.dynamo.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.jarcontents.JarContents;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.ramixin.dynamo.DynamoCommon;
import net.ramixin.dynamo.EntryProvider;
import net.ramixin.dynamo.LoadData;
import net.ramixin.dynamo.neoforge.impl.networking.NeoForgeClientNetworking;
import net.ramixin.dynamo.neoforge.impl.networking.NeoforgeNetworking;
import net.ramixin.dynamo.neoforge.impl.registration.NeoForgeClientRegistration;
import net.ramixin.dynamo.neoforge.impl.registration.NeoForgeRegistration;
import net.ramixin.stator.Latent;
import net.ramixin.stator.Platform;
import net.ramixin.stator.entrypoints.Phase;
import net.ramixin.stator.entrypoints.Side;
import net.ramixin.stator.networking.ClientNetworking;
import net.ramixin.stator.networking.Networking;
import net.ramixin.stator.registration.ClientRegistration;
import net.ramixin.stator.registration.Registration;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Mod("dynamo")
public class Dynamo {

    private static final LoadData loadData = getLoadData();
    private static final Latent<Registration> registration = new Latent<>();
    private static final Latent<ClientRegistration> clientRegistration = new Latent<>();
    private static final Networking networking = new NeoforgeNetworking();
    private static final ClientNetworking clientNetworking = new NeoForgeClientNetworking();
    private static final Platform platform = new NeoForgePlatform();

    public Dynamo(IEventBus modEventBus) {
        registration.supply(new NeoForgeRegistration(modEventBus));
        loadInit(Side.COMMON, Dynamo::useServerParameters);
        modEventBus.addListener(Dynamo::commonSetup);
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        loadSetup(Side.COMMON, Dynamo::useServerParameters);
        loadComplete(Side.COMMON, Dynamo::useServerParameters);
    }

    @Mod(value = "dynamo", dist = Dist.CLIENT)
    public static class DynamoClient {

        public DynamoClient(IEventBus modEventBus) {
            clientRegistration.supply(new NeoForgeClientRegistration(modEventBus));
            loadInit(Side.CLIENT, Dynamo::useClientAndServerParameters);
            modEventBus.addListener(DynamoClient::clientSetup);
        }

        public static void clientSetup(FMLClientSetupEvent event) {
            loadSetup(Side.CLIENT, Dynamo::useClientAndServerParameters);
            loadComplete(Side.CLIENT, Dynamo::useClientAndServerParameters);
        }

    }

    @Mod(value = "dynamo", dist = Dist.DEDICATED_SERVER)
    public static class DynamoServer {

        public DynamoServer(IEventBus modEventBus) {
            loadInit(Side.SERVER, Dynamo::useServerParameters);
            modEventBus.addListener(DynamoServer::serverSetup);
        }

        public static void serverSetup(FMLDedicatedServerSetupEvent event) {
            loadSetup(Side.SERVER, Dynamo::useServerParameters);
            loadComplete(Side.SERVER, Dynamo::useServerParameters);
        }

    }

    public static LoadData getLoadData() {
        List<Path> dispatcherPaths = new ArrayList<>();
        List<Path> eventPaths = new ArrayList<>();
        List<Path> entrypointPaths = new ArrayList<>();
        for(IModFileInfo info : ModList.get().getModFiles()) {
            JarContents mod = info.getFile().getContents();
            addToList(mod, DynamoCommon.DISPATCHER_FILE_PATH, dispatcherPaths);
            addToList(mod, DynamoCommon.EVENT_FILE_PATH, eventPaths);
            addToList(mod, DynamoCommon.INITIALIZER_FILE_PATH, entrypointPaths);
        }
        return new LoadData(dispatcherPaths, eventPaths, entrypointPaths);
    }

    private static void addToList(JarContents mod, String path, List<Path> list) {
        Optional<URI> maybeFile = mod.findFile(path);
        if(maybeFile.isEmpty()) return;
        list.add(Paths.get(maybeFile.get()));
    }

    private static void loadInit(Side side, Consumer<EntryProvider> providerBuilder) {
        DynamoCommon.loadDispatchersFromFile(loadData.dispatchers(), Platform.NEOFORGE);

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
        provider.setRegistration(registration.get());
        provider.setNetworking(networking);
        provider.setPlatform(platform);
    }

    private static void useClientAndServerParameters(EntryProvider provider) {
        useServerParameters(provider);
        useClientParameters(provider);
    }

    private static void useClientParameters(EntryProvider provider) {
        provider.setClientRegistration(clientRegistration.get());
        provider.setClientNetworking(clientNetworking);
    }

}
