package net.ramixin.dynamo.neoforge;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.ramixin.dynamo.neoforge.contexts.NeoForgeBlockBrokenContext;
import net.ramixin.stator.Platform;
import net.ramixin.stator.events.Event;
import net.ramixin.stator.events.annotations.BlockBrokenEvent;
import net.ramixin.stator.events.contexts.BlockBrokenContext;
import net.ramixin.stator.events.dispatching.Dispatcher;

public class NeoForgeDispatchers {

    @Dispatcher(event = BlockBrokenEvent.class, loader = Platform.NEOFORGE)
    private static void blockBrokenDispatcher(Event<BlockBrokenContext, Void> event) {
        NeoForge.EVENT_BUS.addListener((BlockEvent.BreakEvent neoEvent) ->
                event.call(new NeoForgeBlockBrokenContext(neoEvent.getLevel(), neoEvent.getPlayer(), neoEvent.getPos(), neoEvent.getState(), neoEvent.getLevel().getBlockEntity(neoEvent.getPos())))
        );
    }

}
