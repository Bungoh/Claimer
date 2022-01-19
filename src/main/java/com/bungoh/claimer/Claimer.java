package com.bungoh.claimer;

import cloud.commandframework.CommandTree;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.bungoh.claimer.claims.Claim;
import com.bungoh.claimer.claims.ClaimManager;
import com.bungoh.claimer.claims.WorldClaimManager;
import com.bungoh.claimer.commands.Commands;
import com.bungoh.claimer.listeners.BlockBreakListener;
import com.bungoh.claimer.listeners.BlockPlaceListener;
import com.bungoh.claimer.text.Message;
import com.bungoh.claimer.text.Messages;
import com.bungoh.claimer.text.TextColors;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

public final class Claimer extends JavaPlugin {

    private BukkitCommandManager<CommandSender> manager;

    @Override
    public void onEnable() {
        // Detect and Setup Hex Support
        TextColors.setup(this);
        // Setup Main Config File
        setupConfig();
        // Setup Messages
        Messages.setup();
        // Setup Claim Manager
        ClaimManager.getInstance();
        // Setup Commands
        setupCommands();
        // Register Listeners
        registerListeners();
    }

    @Override
    public void onDisable() {
        //Update Claims in the Data File
        ClaimManager.getInstance().updateClaims();
    }

    private void setupCommands() {
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                CommandExecutionCoordinator.simpleCoordinator();
        final Function<CommandSender, CommandSender> mapperFunction = Function.identity();
        try {
            this.manager = new PaperCommandManager<>(
                    this,
                    executionCoordinatorFunction,
                    mapperFunction,
                    mapperFunction
            );
        } catch (final Exception e) {
            this.getLogger().severe("Failed to initialize the command manager.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new Commands(manager).initCommands();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
    }

    private void setupConfig() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        MainConfig.config = getConfig();
    }

    public static class MainConfig {

        private static FileConfiguration config;

        public static String getPrefix() {
            return TextColors.translate(config.getString("prefix"));
        }

    }
}
