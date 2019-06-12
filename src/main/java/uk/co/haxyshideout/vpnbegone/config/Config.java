package uk.co.haxyshideout.vpnbegone.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

@ConfigSerializable
public class Config {

    @Setting
    public General general = new General();
    @Setting
    public Providers providers = new Providers();

    @ConfigSerializable
    public static class General {

        @Setting(comment = "Prefix for any chat messages from this plugin")
        public Text prefix = Text.of(TextColors.GOLD, "[", TextColors.DARK_RED, "VPNBeGone", TextColors.GOLD, "] ");

        @Setting(comment = "Kick message to use when player connects from an anonymizer")
        public Text kickMessage = Text.of("You seem to be using a VPN or Proxy service");

    }

    @ConfigSerializable
    public static class Providers {

        @Setting(comment = "Supports 500 free calls per day, paid plans provided")
        public VpnBlockerConfig vpnBlockerConfig = new VpnBlockerConfig();
        @Setting(comment = "This api only allows 500 calls per day, paid options are not supported currently")
        public GetIPIntelConfig getIPIntelConfig = new GetIPIntelConfig();
        @Setting(comment = "Timeout for api queries in milliseconds")
        public int timeoutMS = 10000;
        @Setting(comment = "Provider to use vpnblocker/getipintel")
        public String provider = "vpnblocker";

        @ConfigSerializable
        public static class VpnBlockerConfig {
            @Setting(comment = "Api key for https://vpnblocker.net/")
            public String apiKey = "";
        }

        @ConfigSerializable
        public static class GetIPIntelConfig {
            @Setting(comment = "Api key for https://getipintel.net/")
            public String apiKey = "";
        }


    }

}
