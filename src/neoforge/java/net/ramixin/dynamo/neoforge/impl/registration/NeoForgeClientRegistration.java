package net.ramixin.dynamo.neoforge.impl.registration;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.ramixin.dynamo.neoforge.impl.networking.NeoForgeClientPayloadContext;
import net.ramixin.stator.networking.ClientPayloadContext;
import net.ramixin.stator.registration.ClientRegistration;
import net.ramixin.stator.registration.DeferredClientRegistration;
import net.ramixin.stator.registration.Registrant;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Queue;
import java.util.function.Consumer;

public class NeoForgeClientRegistration implements ClientRegistration {

    private final IEventBus bus;

    public NeoForgeClientRegistration(IEventBus bus) {
        this.bus = bus;
    }

    @Override
    public <T extends CustomPacketPayload> void clientboundHandler(CustomPacketPayload.Type<T> type, Consumer<ClientPayloadContext<T>> handler) {
        bus.addListener((Consumer<RegisterClientPayloadHandlersEvent>) event -> {
            event.register(type, (payload, ctx) -> handler.accept(new NeoForgeClientPayloadContext<>(payload, ctx.player())));
        });
    }

    @Override
    public <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void screen(Registrant<MenuType<M>> registrant, TriFunction<M, Inventory, Component, S> triFunction) {
        bus.addListener((Consumer<RegisterMenuScreensEvent>) event -> {
            event.register(registrant.get(), triFunction::apply);
        });
    }

    @Override
    public void loadDeferred(DeferredClientRegistration deferredClientRegistration) {
        Queue<Consumer<ClientRegistration>> deferrals = deferredClientRegistration.deferrals();
        while (!deferrals.isEmpty()) {
            Consumer<ClientRegistration> registrant = deferrals.remove();
            registrant.accept(this);
        }
    }


}
