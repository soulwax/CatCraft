package com.soul.catcraft.config;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.support.ConnectionSource;
import com.soul.catcraft.CatCraft;
import com.soul.catcraft.repositories.PlayerRepository;
import com.soul.catcraft.repositories.PlayerRepositoryImpl;
import com.soul.catcraft.services.ChatService;
import com.soul.catcraft.services.ChatServiceImpl;
import com.soul.catcraft.services.PlayerService;
import com.soul.catcraft.services.PlayerServiceImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;


@Provides
@Singleton
public ConnectionSource provideConnectionSource(ConfigService configService) {
    try {
        PluginConfig.DatabaseConfig dbConfig = configService.getConfig().database;

        if ("sqlite".equals(dbConfig.type)) {
            // SQLite for development/small servers
            String dbPath = "plugins/CatCraft/catcraft.db";
            return new JdbcConnectionSource("jdbc:sqlite:" + dbPath);
        } else if ("mysql".equals(dbConfig.type)) {
            // MySQL with HikariCP connection pooling
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true",
                    dbConfig.host, dbConfig.port, dbConfig.database));
            hikariConfig.setUsername(dbConfig.username);
            hikariConfig.setPassword(dbConfig.password);
            hikariConfig.setMaximumPoolSize(dbConfig.maxPoolSize);
            hikariConfig.setConnectionTimeout(dbConfig.connectionTimeout);
            hikariConfig.setIdleTimeout(dbConfig.idleTimeout);
            hikariConfig.setLeakDetectionThreshold(dbConfig.enableConnectionLeakDetection ? 60000 : 0);
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            HikariDataSource dataSource = new HikariDataSource(hikariConfig);
            return new DataSourceConnectionSource(dataSource, DatabaseTypeUtils.createDatabaseType(hikariConfig.getJdbcUrl()));
        } else {
            throw new IllegalArgumentException("Unsupported database type: " + dbConfig.type);
        }
    } catch (SQLException e) {
        throw new RuntimeException("Failed to create database connection", e);
    }
}

@Provides
@Singleton
public ScheduledExecutorService provideScheduledExecutor(ConfigService configService) {
    int threadPoolSize = configService.getConfig().general.asyncThreadPoolSize;
    return Executors.newScheduledThreadPool(threadPoolSize,
            new ThreadFactoryBuilder()
                    .setNameFormat("CatCraft-Async-%d")
                    .setDaemon(true)
                    .build());
}
