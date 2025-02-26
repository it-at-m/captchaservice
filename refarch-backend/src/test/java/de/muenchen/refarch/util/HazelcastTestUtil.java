package de.muenchen.refarch.util;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HazelcastTestUtil {

    private final HazelcastInstance hazelcastInstance;

    public HazelcastTestUtil(final HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public void resetHazelcastInstance() {
        final List<String> mapNames = hazelcastInstance.getDistributedObjects()
                .stream()
                .map(DistributedObject::getName)
                .toList();
        for (final String mapName : mapNames) {
            hazelcastInstance.getMap(mapName).clear();
        }
    }

}
