package net.ramixin.dynamo.fabric;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.ramixin.dynamo.fabric.contexts.FabricBlockBrokenContext;
import net.ramixin.dynamo.fabric.contexts.FabricCommandRegistrationContext;
import net.ramixin.dynamo.fabric.contexts.FabricPlayerJoinedServerContext;
import net.ramixin.stator.Platform;
import net.ramixin.stator.events.Event;
import net.ramixin.stator.events.annotations.BlockBrokenEvent;
import net.ramixin.stator.events.annotations.CommandRegistrationEvent;
import net.ramixin.stator.events.annotations.PlayerJoinedServerEvent;
import net.ramixin.stator.events.contexts.BlockBrokenContext;
import net.ramixin.stator.events.contexts.CommandRegistrationContext;
import net.ramixin.stator.events.contexts.PlayerJoinedServerContext;
import net.ramixin.stator.events.dispatching.Dispatcher;

public class FabricDispatchers {

    @Dispatcher(event = BlockBrokenEvent.class, loader = Platform.FABRIC)
    private static void blockBrokenDispatcher(Event<BlockBrokenContext, Void> event) {
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            event.call(new FabricBlockBrokenContext(level, player, pos, state, blockEntity));
            return true;
        });
    }

    @Dispatcher(event = PlayerJoinedServerEvent.class, loader = Platform.FABRIC)
    private static void playerJoinedServerDispatcher(Event<PlayerJoinedServerContext, Void> event) {
        ServerPlayerEvents.JOIN.register((player) -> event.call(new FabricPlayerJoinedServerContext(player)));
    }

    @Dispatcher(event = CommandRegistrationEvent.class, loader = Platform.FABRIC)
    private static void commandRegistrationDispatcher(Event<CommandRegistrationContext, Void> event) {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> event.call(new FabricCommandRegistrationContext(dispatcher, buildContext, selection)));
    }

}
