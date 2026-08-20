package net.ramixin.dynamo.neoforge.impl.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.ramixin.stator.networking.Networking;

public class NeoforgeNetworking implements Networking {

    @Override
    public void sendClientbound(ServerPlayer serverPlayer, CustomPacketPayload customPacketPayload) {
        PacketDistributor.sendToPlayer(serverPlayer, customPacketPayload);
    }
}
