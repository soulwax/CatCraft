// File: src/main/java/com/soul/catcraft/services/ConfigServiceImpl.java

package com.soul.catcraft.services;

// services/ConfigServiceImpl.java
@Singleton
public class ConfigServiceImpl implements ConfigService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);

    private final Plugin plugin;
    private final EventBus eventBus;
    private PluginConfig config;
    private final WatchService watchService;
    private final Path configPath;

    @Inject
    public ConfigServiceImpl(Plugin plugin, EventBus eventBus) throws IOException {
        this.plugin = plugin;
        this.eventBus = eventBus;
        this.configPath = plugin.getDataFolder().toPath().resolve("config.yml");
        this.watchService = FileSystems.getDefault().newWatchService();

        loadConfiguration();
        setupFileWatcher();
    }

    private void loadConfiguration() {
        try {
            if (!Files.exists(configPath)) {
                plugin.saveDefaultConfig();
            }

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            this.config = mapper.readValue(configPath.toFile(), PluginConfig.class);

            // Validate configuration
            validateConfiguration();

            logger.info("Configuration loaded successfully");
            eventBus.post(new ConfigurationReloadedEvent(config));

        } catch (Exception e) {
            logger.error("Failed to load configuration", e);
            // Use default configuration
            this.config = new PluginConfig();
        }
    }

    private void setupFileWatcher() {
        try {
            plugin.getDataFolder().toPath().register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );

            // Start watching in a separate thread
            CompletableFuture.runAsync(() -> {
                while (true) {
                    try {
                        WatchKey key = watchService.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.context().toString().equals("config.yml")) {
                                // Debounce rapid file changes
                                Thread.sleep(500);

                                Bukkit.getScheduler().runTask(plugin, () -> {
                                    logger.info("Configuration file changed, reloading...");
                                    loadConfiguration();
                                });
                            }
                        }
                        key.reset();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });

        } catch (IOException e) {
            logger.warn("Could not setup configuration file watcher", e);
        }
    }

    private void validateConfiguration() throws ConfigurationException {
        // Validate ranges and required fields
        if (config.chat.localChatRange < 0) {
            throw new ConfigurationException("Local chat range cannot be negative");
        }

        if (config.database.maxPoolSize < 1 || config.database.maxPoolSize > 50) {
            throw new ConfigurationException("Database pool size must be between 1 and 50");
        }

        // Validate message templates
        if (config.chat.welcomeMessageTemplate == null || config.chat.welcomeMessageTemplate.trim().isEmpty()) {
            config.chat.welcomeMessageTemplate = "<green>Welcome <gold>{player}</gold> to the server!</green>";
        }
    }

    @Override
    public PluginConfig getConfig() {
        return config;
    }

    @Override
    public void reloadConfiguration() {
        loadConfiguration();
    }

    @Override
    public void saveConfiguration() {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.writeValue(configPath.toFile(), config);
            logger.info("Configuration saved successfully");
        } catch (IOException e) {
            logger.error("Failed to save configuration", e);
        }
    }
}