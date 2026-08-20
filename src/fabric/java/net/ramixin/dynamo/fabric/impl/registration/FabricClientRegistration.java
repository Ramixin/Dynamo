package net.ramixin.dynamo.fabric.impl.registration;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.ramixin.dynamo.fabric.impl.networking.FabricClientPayloadContext;
import net.ramixin.stator.networking.ClientPayloadContext;
import net.ramixin.stator.registration.ClientRegistration;
import net.ramixin.stator.registration.DeferredClientRegistration;
import net.ramixin.stator.registration.Registrant;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Queue;
import java.util.function.Consumer;

public class FabricClientRegistration implements ClientRegistration {

    @Override
    public <T extends CustomPacketPayload> void clientboundHandler(CustomPacketPayload.Type<T> type, Consumer<ClientPayloadContext<T>> consumer) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, ctx) -> {
            FabricClientPayloadContext<T> context = new FabricClientPayloadContext<>(payload, ctx.player());
            consumer.accept(context);
        });
    }

    @Override
    public <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void screen(Registrant<MenuType<M>> menuType, TriFunction<M, Inventory, Component, S> triFunction) {
        MenuScreens.register(menuType.get(), triFunction::apply);
    }

    @Override
    public void loadDeferred(DeferredClientRegistration deferredClientRegistration) {
        Queue<Consumer<ClientRegistration>> deferrals = deferredClientRegistration.deferrals();
        while (!deferrals.isEmpty()) {
            Consumer<ClientRegistration> deferral = deferrals.poll();
            deferral.accept(this);
        }
    }
}
