package uk.co.haxyshideout.vpnbegone.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import uk.co.haxyshideout.vpnbegone.Permissions;
import uk.co.haxyshideout.vpnbegone.VPNBeGone;

public class CommandManager {

    public static void registerCommands() {
        CommandSpec lookupPlayerSpec = CommandSpec.builder()
                .description(Text.of("Lookup ip information for an online player"))
                .permission(Permissions.COMMAND_LOOKUP)
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("player")))
                )
                .executor(new LookupCommand.LookupPlayer())
                .build();

        CommandSpec lookupIPSpec = CommandSpec.builder()
                .description(Text.of("Lookup ip information"))
                .permission(Permissions.COMMAND_LOOKUP)
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.ip(Text.of("ip")))
                )
                .executor(new LookupCommand.LookupIP())
                .build();



        CommandSpec vpnbegoneSpec = CommandSpec.builder()
                .child(lookupPlayerSpec, "lookupplayer")
                .child(lookupIPSpec, "lookupip")
                .build();
        Sponge.getCommandManager().register(VPNBeGone.getInstance(), vpnbegoneSpec, "vpnbegone");
    }

}
