package de.muenchen.refarch.configuration;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    public Config localConfig(@Value("${spring.session.timeout:1000}") final int timeout) {
        final Config hazelcastConfig = new Config();
        hazelcastConfig.setClusterName(hazelcastProperties.getClusterName());
        hazelcastConfig.setInstanceName(hazelcastProperties.getInstanceName());

        addSessionTimeoutToHazelcastConfig(hazelcastConfig, timeout);

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
    public Config config(@Value("${spring.session.timeout:1000}") final int timeout) {
        final Config hazelcastConfig = new Config();
        hazelcastConfig.setClusterName(hazelcastProperties.getClusterName());
        hazelcastConfig.setInstanceName(hazelcastProperties.getInstanceName());

        addSessionTimeoutToHazelcastConfig(hazelcastConfig, timeout);

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
     * @param sessionTimeout for security session.
     */
    private void addSessionTimeoutToHazelcastConfig(final Config hazelcastConfig, final int sessionTimeout) {
        final MapConfig sessionConfig = new MapConfig();
        sessionConfig.setName("SOURCE_ADDRESSES");
        sessionConfig.setTimeToLiveSeconds(sessionTimeout);
        sessionConfig.getEvictionConfig().setEvictionPolicy(EvictionPolicy.LRU);

        hazelcastConfig.addMapConfig(sessionConfig);
    }

}
