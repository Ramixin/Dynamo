package net.ramixin.dynamo.fabric.impl.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.ramixin.stator.networking.Networking;

public class FabricNetworking implements Networking {

    @Override
    public void sendClientbound(ServerPlayer serverPlayer, CustomPacketPayload customPacketPayload) {
        ServerPlayNetworking.send(serverPlayer, customPacketPayload);
    }
}
