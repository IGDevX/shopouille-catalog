package org.shopouille.config;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import java.time.Duration;

@ApplicationScoped
public class MicrometerConfig {

    @Produces
    @Singleton
    public MeterFilter configureHistogramBuckets() {
        return new MeterFilter() {
            @Override
            public DistributionStatisticConfig configure(
                    Meter.Id id, DistributionStatisticConfig config) {
                if (id.getName().startsWith("http.server.requests")
                        || id.getName().startsWith("http.server.request.duration")) {
                    return DistributionStatisticConfig.builder()
                            .percentilesHistogram(true)
                            .serviceLevelObjectives(
                                    Duration.ofMillis(1).toNanos(),
                                    Duration.ofMillis(5).toNanos(),
                                    Duration.ofMillis(10).toNanos(),
                                    Duration.ofMillis(25).toNanos(),
                                    Duration.ofMillis(50).toNanos(),
                                    Duration.ofMillis(100).toNanos(),
                                    Duration.ofMillis(250).toNanos(),
                                    Duration.ofMillis(500).toNanos(),
                                    Duration.ofMillis(1000).toNanos(),
                                    Duration.ofMillis(2500).toNanos(),
                                    Duration.ofMillis(5000).toNanos(),
                                    Duration.ofMillis(10000).toNanos())
                            .build()
                            .merge(config);
                }
                return config;
            }
        };
    }
}
