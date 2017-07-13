package org.metricssampler.extensions.oranosql;

import oracle.kv.impl.topo.ResourceId.ResourceType;
import oracle.kv.impl.util.ConfigurableService.ServiceStatus;
import org.metricssampler.reader.Metrics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Add aggregated count metrics:
 *  * the number of nodes of a certain type (e.g. replication nodes)
 *  * the number of nodes in certain status per type (e.g. number of running replication nodes)
 */
public class ResourceTypeServiceStatusMetrics {
    protected final static List<ResourceType> STATUS_RESOURCE_TYPES = Arrays.asList(ResourceType.REP_NODE, ResourceType.STORAGE_NODE, ResourceType.ADMIN);

    private final Map<ResourceType, Map<ServiceStatus, Integer>> statuses = initResourceTypeStatusCounts();
    private final long timestamp = System.currentTimeMillis();

    /**
     * Fill the statuses with count 0 for all relevant type and status combinations
     */
    protected Map<ResourceType, Map<ServiceStatus, Integer>> initResourceTypeStatusCounts() {
        final Map<ResourceType, Map<ServiceStatus, Integer>> result = new HashMap<>();
        for (ResourceType type : STATUS_RESOURCE_TYPES) {
            final HashMap<ServiceStatus, Integer> value = new HashMap<>();
            for (ServiceStatus status : ServiceStatus.values()) {
                value.put(status, 0);
            }
            result.put(type, value);
        }
        return result;
    }

    public void add(ResourceType type, ServiceStatus status) {
        statuses.get(type).computeIfPresent(status, (s, count) -> count + 1);
    }

    public void addMetrics(Metrics result) {
        for (Entry<ResourceType, Map<ServiceStatus, Integer>> entry : statuses.entrySet()) {
            final ResourceType type = entry.getKey();
            final int count = entry.getValue().values().stream().mapToInt(Integer::intValue).sum();
            result.add(type.name().toLowerCase() + ".count", "Number of " + type.name() + " nodes", timestamp, count);
            for (Entry<ServiceStatus, Integer> entry2 : entry.getValue().entrySet()) {
                final ServiceStatus status = entry2.getKey();
                result.add(type.name().toLowerCase() + ".status." + status.name().toLowerCase() + ".count", "Number of " + type.name() + " nodes in status " + status.name(), timestamp, entry2.getValue());
            }
        }
    }

}
