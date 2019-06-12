package uk.co.haxyshideout.vpnbegone.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import uk.co.haxyshideout.vpnbegone.VPNBeGone;
import uk.co.haxyshideout.vpnbegone.storage.IPEntry;

import java.net.InetAddress;
import java.util.Optional;

public class LookupCommand {

    public static class LookupPlayer implements CommandExecutor {
        @Override
        public CommandResult execute(CommandSource src, CommandContext args) {
            Player player = args.<Player>getOne("player").get();
            handleIP(src, player.getConnection().getAddress().getAddress().getHostAddress());
            return CommandResult.success();
        }
    }

    public static class LookupIP implements CommandExecutor {
        @Override
        public CommandResult execute(CommandSource src, CommandContext args) {
            InetAddress inetAddress = args.<InetAddress>getOne("ip").get();
            handleIP(src, inetAddress.getHostAddress());
            return CommandResult.success();
        }
    }

    private static void handleIP(CommandSource src, String ip) {
        Sponge.getScheduler().createTaskBuilder().async().execute(() -> {

            Optional<IPEntry> ipEntryOpt = VPNBeGone.getIPStorage().getIPEntry(ip);
            if(!ipEntryOpt.isPresent()) {
                src.sendMessage(Text.of(VPNBeGone.getConfig().general.prefix, TextColors.RED, "Unable to get information for ip ", ip));
                return;
            }

            IPEntry ipEntry = ipEntryOpt.get();
            if(!ipEntry.isResidential()) {
                src.sendMessage(Text.of(VPNBeGone.getConfig().general.prefix, TextColors.GREEN, ip, " belongs to a hosting organization."));
            } else {
                src.sendMessage(Text.of(VPNBeGone.getConfig().general.prefix, TextColors.GREEN, ip, " does not belong to a hosting organization."));
            }

        }).submit(VPNBeGone.getInstance());
    }

}
