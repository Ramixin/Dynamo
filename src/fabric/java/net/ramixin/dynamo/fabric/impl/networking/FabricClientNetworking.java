package net.ramixin.dynamo.fabric.impl.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.ramixin.stator.networking.ClientNetworking;

public class FabricClientNetworking implements ClientNetworking {

    @Override
    public void sendServerbound(CustomPacketPayload customPacketPayload) {
        ClientPlayNetworking.send(customPacketPayload);
    }
}
