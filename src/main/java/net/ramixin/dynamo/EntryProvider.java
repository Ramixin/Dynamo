package net.ramixin.dynamo;

import net.ramixin.stator.Platform;
import net.ramixin.stator.entrypoints.EntrypointParameter;
import net.ramixin.stator.metadata.EntrypointsMetaFile;
import net.ramixin.stator.networking.ClientNetworking;
import net.ramixin.stator.networking.Networking;
import net.ramixin.stator.registration.ClientRegistration;
import net.ramixin.stator.registration.Registration;

import java.util.List;

public class EntryProvider {

    private final List<EntrypointsMetaFile.EntrypointData> entryData;

    private Registration registration;
    private ClientRegistration clientRegistration;
    private Platform platform;
    private Networking networking;
    private ClientNetworking clientNetworking;

    public EntryProvider(List<EntrypointsMetaFile.EntrypointData> entryData) {
        this.entryData = entryData;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public void setClientRegistration(ClientRegistration clientRegistration) {
        this.clientRegistration = clientRegistration;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public void setNetworking(Networking networking) {
        this.networking = networking;
    }

    public void setClientNetworking(ClientNetworking clientNetworking) {
        this.clientNetworking = clientNetworking;
    }

    public void provide() {
        for(EntrypointsMetaFile.EntrypointData data : entryData) {
            EntrypointParameter[] expected = data.parameters();
            Object[] args = new Object[expected.length];
            for(int i = 0; i < expected.length; i++) {
                EntrypointParameter parameter = expected[i];
                Object arg = getArg(parameter);
                args[i] = arg;
            }

            try {
                data.method().invoke(null, args);
            } catch(Exception e) {
                throw new RuntimeException(String.format("Entrypoint %s#%s threw during initialization", data.method().getDeclaringClass(), data.method().getName()), e);
            }

        }
    }

    private Object getArg(EntrypointParameter parameter) {
        Object arg = switch(parameter) {
            case REGISTRATION -> registration;
            case CLIENT_REGISTRATION -> clientRegistration;
            case PLATFORM -> platform;
            case NETWORKING -> networking;
            case CLIENT_NETWORKING -> clientNetworking;
            default -> throw new IllegalArgumentException("Unknown parameter: " + parameter);
        };
        if(arg == null)
            throw new IllegalArgumentException("parameter " + parameter + " is missing from Dynamo EntryProvider");
        return arg;
    }

}
