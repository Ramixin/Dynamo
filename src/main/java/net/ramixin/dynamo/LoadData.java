package net.ramixin.dynamo;

import java.nio.file.Path;
import java.util.List;

public record LoadData(List<Path> dispatchers, List<Path> events, List<Path> entrypoints) {

}
