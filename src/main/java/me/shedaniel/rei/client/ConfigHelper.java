package me.shedaniel.rei.client;

import me.shedaniel.rei.RoughlyEnoughItemsCore;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

public class ConfigHelper {
    
    private final File configFile;
    private REIConfig config;
    private boolean craftableOnly;
    
    public ConfigHelper() {
        this.configFile = new File(FMLPaths.CONFIGDIR.get().toFile(), "rei.json");
        this.craftableOnly = false;
        try {
            if (!configFile.getParentFile().exists() || !configFile.getParentFile().isDirectory())
                configFile.getParentFile().mkdirs();
            loadConfig();
            RoughlyEnoughItemsCore.LOGGER.info("Config is loaded.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static ConfigHelper getInstance() {
        return RoughlyEnoughItemsCore.getConfigHelper();
    }
    
    public void saveConfig() throws IOException {
        configFile.getParentFile().mkdirs();
        if (!configFile.exists() && !configFile.createNewFile()) {
            RoughlyEnoughItemsCore.LOGGER.error("Failed to save config! Overwriting with default config.");
            config = new REIConfig();
            return;
        }
        FileWriter writer = new FileWriter(configFile, false);
        try {
            REIConfig.GSON.toJson(config, writer);
        } finally {
            writer.close();
        }
    }
    
    public void loadConfig() throws IOException {
        if (!configFile.exists() || !configFile.canRead()) {
            config = new REIConfig();
            saveConfig();
            return;
        }
        boolean failed = false;
        try {
            config = REIConfig.GSON.fromJson(new InputStreamReader(Files.newInputStream(configFile.toPath())), REIConfig.class);
        } catch (Exception e) {
            failed = true;
        }
        if (failed || config == null) {
            RoughlyEnoughItemsCore.LOGGER.error("Failed to load config! Overwriting with default config.");
            config = new REIConfig();
        }
        saveConfig();
    }
    
    public String getGiveCommandPrefix() {
        return config.giveCommandPrefix;
    }
    
    public REIItemListOrdering getItemListOrdering() {
        return config.itemListOrdering;
    }
    
    public void setItemListOrdering(REIItemListOrdering ordering) {
        config.itemListOrdering = ordering;
    }
    
    public boolean isAscending() {
        return config.isAscending;
    }
    
    public void setAscending(boolean ascending) {
        config.isAscending = ascending;
    }
    
    public boolean craftableOnly() {
        return craftableOnly && config.enableCraftableOnlyButton;
    }
    
    public void toggleCraftableOnly() {
        craftableOnly = !craftableOnly;
    }
    
    public boolean showCraftableOnlyButton() {
        return config.enableCraftableOnlyButton;
    }
    
    public void setShowCraftableOnlyButton(boolean enableCraftableOnlyButton) {
        config.enableCraftableOnlyButton = enableCraftableOnlyButton;
    }
    
    public boolean sideSearchField() {
        return config.sideSearchField;
    }
    
    public void setSideSearchField(boolean sideSearchField) {
        config.sideSearchField = sideSearchField;
    }
    
    public boolean checkUpdates() {
        return config.checkUpdates;
    }
    
    public void setCheckUpdates(boolean checkUpdates) {
        config.checkUpdates = checkUpdates;
    }
    
    public boolean isMirrorItemPanel() {
        return config.mirrorItemPanel;
    }
    
    public void setMirrorItemPanel(boolean mirrorItemPanel) {
        config.mirrorItemPanel = mirrorItemPanel;
    }
    
    public boolean isLoadingDefaultPlugin() {
        return config.loadDefaultPlugin;
    }
    
    public void setLoadingDefaultPlugin(boolean loadDefaultPlugin) {
        config.loadDefaultPlugin = loadDefaultPlugin;
    }
    
}
