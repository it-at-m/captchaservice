package de.muenchen.captchaservice.configuration.hazelcast;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import de.muenchen.captchaservice.common.HazelcastConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
public class HazelcastConfiguration {
    private final HazelcastProperties hazelcastProperties;

    @Bean
    public HazelcastInstance hazelcastInstance(@Autowired final Config config) {
        return Hazelcast.getOrCreateHazelcastInstance(config);
    }

    @Bean
    @Profile({ "!hazelcast-k8s" })
    public Config localConfig() {
        final Config hazelcastConfig = new Config();
        hazelcastConfig.setClusterName(hazelcastProperties.getClusterName());
        hazelcastConfig.setInstanceName(hazelcastProperties.getInstanceName());

        addSessionTimeoutToHazelcastConfig(hazelcastConfig);

        final NetworkConfig networkConfig = hazelcastConfig.getNetworkConfig();

        final JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig()
                .setEnabled(true)
                .addMember("localhost");

        return hazelcastConfig;
    }

    @Bean
    @Profile({ "hazelcast-k8s" })
    public Config config() {
        final Config hazelcastConfig = new Config();
        hazelcastConfig.setClusterName(hazelcastProperties.getClusterName());
        hazelcastConfig.setInstanceName(hazelcastProperties.getInstanceName());

        addSessionTimeoutToHazelcastConfig(hazelcastConfig);

        hazelcastConfig.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        hazelcastConfig.getNetworkConfig().getJoin().getKubernetesConfig().setEnabled(true)
                //If we don't set a specific name, it would call -all- services within a namespace
                .setProperty("service-name", hazelcastProperties.getServiceName());

        return hazelcastConfig;
    }

    /**
     * Adds the session timeout in seconds to the hazelcast configuration.
     * <p>
     * Since we are creating the map it's important to evict sessions by setting a reasonable value for
     * time to live.
     *
     * @param hazelcastConfig to add the timeout.
     */
    private void addSessionTimeoutToHazelcastConfig(final Config hazelcastConfig) {
        final MapConfig invalidatedPayloadsConfig = new MapConfig();
        invalidatedPayloadsConfig.setName(HazelcastConstants.INVALIDATED_PAYLOADS);
        invalidatedPayloadsConfig.setTimeToLiveSeconds(hazelcastProperties.getTimeoutSeconds());
        invalidatedPayloadsConfig.getEvictionConfig().setEvictionPolicy(EvictionPolicy.LRU);
        hazelcastConfig.addMapConfig(invalidatedPayloadsConfig);

        final MapConfig sourceAddressesConfig = new MapConfig();
        sourceAddressesConfig.setName(HazelcastConstants.SOURCE_ADDRESSES);
        sourceAddressesConfig.setTimeToLiveSeconds(hazelcastProperties.getTimeoutSeconds());
        sourceAddressesConfig.getEvictionConfig().setEvictionPolicy(EvictionPolicy.LRU);
        hazelcastConfig.addMapConfig(sourceAddressesConfig);
    }

}
