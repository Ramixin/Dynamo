package net.ramixin.dynamo.neoforge.impl.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.ramixin.stator.networking.ClientNetworking;

public class NeoForgeClientNetworking implements ClientNetworking {

    @Override
    public void sendServerbound(CustomPacketPayload customPacketPayload) {
        ClientPacketDistributor.sendToServer(customPacketPayload);
    }
}
