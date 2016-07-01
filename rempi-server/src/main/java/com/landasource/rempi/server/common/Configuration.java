package com.landasource.rempi.server.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.provider.GenericTypeInterface;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.classpath.ClasspathConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.reload.strategy.PeriodicalReloadStrategy;

/**
 * Created by Zsolti on 2016.07.01..
 */
public class Configuration implements ConfigurationProvider {

    private ConfigurationProvider provider;

    @PostConstruct
    private void init() {

        final ConfigFilesProvider configFilesProvider = new ConfigFilesProvider() {
            @Override
            public Iterable<Path> getConfigFiles() {
                return Arrays.asList(Paths.get("application.properties"), Paths.get("application-dev.properties"));
            }
        };

        final ConfigurationSource source = new ClasspathConfigurationSource(configFilesProvider);
        provider = new ConfigurationProviderBuilder().withConfigurationSource(source).withReloadStrategy(new PeriodicalReloadStrategy(5, TimeUnit.SECONDS)).build();
    }

    @Override
    public Properties allConfigurationAsProperties() {
        return provider.allConfigurationAsProperties();
    }

    @Override
    public <T> T getProperty(final String key, final Class<T> type) {
        return provider.getProperty(key, type);
    }

    @Override
    public <T> T getProperty(final String key, final GenericTypeInterface genericType) {
        return provider.getProperty(key, genericType);
    }

    @Override
    public <T> T bind(final String prefix, final Class<T> type) {
        return bind(prefix, type);
    }

}
