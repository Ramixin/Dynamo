package net.ramixin.dynamo;

import net.ramixin.stator.entrypoints.Entrypoint;
import net.ramixin.stator.events.StatorEventRegistry;
import net.ramixin.stator.metadata.EntrypointsMetaFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public interface DynamoCommon {

    String MOD_NAME = "Dynamo";
    Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    String DISPATCHER_FILE_PATH = "META-INF/stator/dispatchers.json";
    String EVENT_FILE_PATH = "META-INF/stator/events.json";
    String INITIALIZER_FILE_PATH = "META-INF/stator/initializers.json";

    static void loadDispatchersFromFile(List<Path> dispatchers, String loader) {
        for(Path path : dispatchers) {
            try {
                StatorEventRegistry.registerDispatchersMetafile(loader, path);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Failed to process dispatcher file %s", path), e);
            }
        }
    }

    static void loadEventsFromFile(List<Path> events) {
        for(Path path : events) {
            try {
                StatorEventRegistry.registerEventsMetaFile(path);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Failed to process event file: %s", path), e);
            }
        }
    }

    static EntryProvider loadInitializersFromFile(List<Path> initializers, Entrypoint.Side side) {
        List<EntrypointsMetaFile.EntrypointData> entryData = new ArrayList<>();
        for(Path path : initializers) {
            try {
                EntrypointsMetaFile file = EntrypointsMetaFile.read(path, LOGGER);
                entryData.addAll(file.getSideList(side));
            } catch (Exception e) {
                throw new RuntimeException(String.format("Failed to process initializer file: %s", path), e);
            }
        }
        return new EntryProvider(entryData);
    }
}
