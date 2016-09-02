package org.pesc.cds.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created by sallen on 8/15/16.
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
    public static final String PREFIX = "NetworkServer_";
    @Value("${cache.manager.expire.time.minutes:120}")
    private Integer expireTimeMinutes;
    @Value("${cache.manager.maximum.size:1000}")
    private Integer maximumSize;

    @Bean
    public CacheManager cacheManager(){
        GuavaCacheManager guavaCacheManager =  new GuavaCacheManager();
        guavaCacheManager.setCacheBuilder(CacheBuilder.newBuilder().expireAfterAccess(expireTimeMinutes, TimeUnit.MINUTES).maximumSize(maximumSize));
        return guavaCacheManager;
    }
}
