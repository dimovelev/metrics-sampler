package org.metricssampler.extensions.elasticsearch;

public class ElasticSearchUtil {
    private ElasticSearchUtil() {
        // private constructor to prevent instantiation of utility class
    }

    public static int mapClusterHealthStatus(String value) {
        switch(value) {
            case "green":
                return 0;
            case "yellow":
                return 1;
            case "red":
                return 2;
            default:
                return -1;
        }
    }

}
