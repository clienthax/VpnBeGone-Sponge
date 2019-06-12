package uk.co.haxyshideout.vpnbegone;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import uk.co.haxyshideout.vpnbegone.commands.CommandManager;
import uk.co.haxyshideout.vpnbegone.config.Config;
import uk.co.haxyshideout.vpnbegone.listeners.LoginListener;
import uk.co.haxyshideout.vpnbegone.providers.GetIPIntelProvider;
import uk.co.haxyshideout.vpnbegone.providers.IPInfoProvider;
import uk.co.haxyshideout.vpnbegone.providers.VpnBlockerProvider;
import uk.co.haxyshideout.vpnbegone.storage.IPStorage;

import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "vpnbegone",
        name = "VpnBeGone",
        authors = "Clienthax"
)
public class VPNBeGone {

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private Config config;
    private CommentedConfigurationNode node;
    @SuppressWarnings("UnstableApiUsage")
    private TypeToken<Config> configTypeToken = TypeToken.of(Config.class);

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;

    private static VPNBeGone INSTANCE;
    private IPInfoProvider provider;
    private IPStorage ipStorage;

    public static Path getConfigFolder() {
        return INSTANCE.privateConfigDir;
    }

    @Listener
    public void onGameConstruct(GameConstructionEvent event) throws IOException, ObjectMappingException {
        INSTANCE = this;
        loadConfig();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("VpnBeGone Loading");
        logger.info("\n" +
                " ██▒   █▓ ██▓███   ███▄    █  ▄▄▄▄   ▓█████   ▄████  ▒█████   ███▄    █ ▓█████ \n" +
                "▓██░   █▒▓██░  ██▒ ██ ▀█   █ ▓█████▄ ▓█   ▀  ██▒ ▀█▒▒██▒  ██▒ ██ ▀█   █ ▓█   ▀ \n" +
                " ▓██  █▒░▓██░ ██▓▒▓██  ▀█ ██▒▒██▒ ▄██▒███   ▒██░▄▄▄░▒██░  ██▒▓██  ▀█ ██▒▒███   \n" +
                "  ▒██ █░░▒██▄█▓▒ ▒▓██▒  ▐▌██▒▒██░█▀  ▒▓█  ▄ ░▓█  ██▓▒██   ██░▓██▒  ▐▌██▒▒▓█  ▄ \n" +
                "   ▒▀█░  ▒██▒ ░  ░▒██░   ▓██░░▓█  ▀█▓░▒████▒░▒▓███▀▒░ ████▓▒░▒██░   ▓██░░▒████▒\n" +
                "   ░ ▐░  ▒▓▒░ ░  ░░ ▒░   ▒ ▒ ░▒▓███▀▒░░ ▒░ ░ ░▒   ▒ ░ ▒░▒░▒░ ░ ▒░   ▒ ▒ ░░ ▒░ ░\n" +
                "   ░ ░░  ░▒ ░     ░ ░░   ░ ▒░▒░▒   ░  ░ ░  ░  ░   ░   ░ ▒ ▒░ ░ ░░   ░ ▒░ ░ ░  ░\n" +
                "     ░░  ░░          ░   ░ ░  ░    ░    ░   ░ ░   ░ ░ ░ ░ ▒     ░   ░ ░    ░   \n" +
                "      ░                    ░  ░         ░  ░      ░     ░ ░           ░    ░  ░\n" +
                "     ░                             ░                                           \n");

        if(registerAPIProviders()) {
            registerStorage();
            registerListeners();
            CommandManager.registerCommands();
            logger.info("VpnBeGone Loaded");
        } else {
            logger.error("VpnBeGone Failed to load, check provider config is correct");
        }
    }

    private void registerStorage() {
        ipStorage = new IPStorage();
    }

    private void registerListeners() {
        Sponge.getEventManager().registerListener(this, ClientConnectionEvent.Join.class, new LoginListener());
    }

    private boolean registerAPIProviders() {
        switch (config.providers.provider) {
            case "vpnblocker":
                provider = new VpnBlockerProvider();
                return true;
            case "getipintel":
                provider = new GetIPIntelProvider();
                return true;
        }

        return false;
    }

    private void loadConfig() throws IOException, ObjectMappingException {
        //Config
        this.node = this.configLoader.load();
        this.config = node.getValue(configTypeToken, new Config());
        saveConfig();
        //End config
    }

    private void saveConfig() {
        try {
            node.setValue(configTypeToken, this.config);
            this.configLoader.save(node);
        } catch (Exception e) {
            getLogger().error("Error saving config", e);
        }
    }

    public static Logger getLogger() {
        return INSTANCE.logger;
    }

    public static Config getConfig() {
        return INSTANCE.config;
    }

    public static IPInfoProvider getProvider() {
        return INSTANCE.provider;
    }

    public static IPStorage getIPStorage() {
        return INSTANCE.ipStorage;
    }

    public static VPNBeGone getInstance() {
        return INSTANCE;
    }

}
