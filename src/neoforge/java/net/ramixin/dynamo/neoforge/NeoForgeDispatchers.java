package net.ramixin.dynamo.neoforge;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.ramixin.dynamo.neoforge.impl.contexts.NeoForgeBlockBrokenContext;
import net.ramixin.dynamo.neoforge.impl.contexts.NeoForgeCommandRegistrationContext;
import net.ramixin.dynamo.neoforge.impl.contexts.NeoForgePlayerJoinedServerContext;
import net.ramixin.stator.Platform;
import net.ramixin.stator.events.Event;
import net.ramixin.stator.events.annotations.BlockBrokenEvent;
import net.ramixin.stator.events.annotations.CommandRegistrationEvent;
import net.ramixin.stator.events.annotations.PlayerJoinedServerEvent;
import net.ramixin.stator.events.contexts.BlockBrokenContext;
import net.ramixin.stator.events.contexts.CommandRegistrationContext;
import net.ramixin.stator.events.contexts.PlayerJoinedServerContext;
import net.ramixin.stator.events.dispatching.Dispatcher;

public class NeoForgeDispatchers {

    @Dispatcher(event = BlockBrokenEvent.class, loader = Platform.NEOFORGE)
    private static void blockBrokenDispatcher(Event<BlockBrokenContext, Void> event) {
        NeoForge.EVENT_BUS.addListener((BlockEvent.BreakEvent neoEvent) ->
                event.call(new NeoForgeBlockBrokenContext(neoEvent))
        );
    }

    @Dispatcher(event = PlayerJoinedServerEvent.class, loader = Platform.NEOFORGE)
    private static void playerJoinedServerDispatcher(Event<PlayerJoinedServerContext, Void> event) {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent neoEvent) ->
                event.call(new NeoForgePlayerJoinedServerContext(neoEvent.getEntity()))
        );
    }

    @Dispatcher(event = CommandRegistrationEvent.class, loader = Platform.NEOFORGE)
    private static void commandRegistrationDispatcher(Event<CommandRegistrationContext, Void> event) {
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent neoEvent) ->
                event.call(new NeoForgeCommandRegistrationContext(neoEvent))
        );
    }

}
