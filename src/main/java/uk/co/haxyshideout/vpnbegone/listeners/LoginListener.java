package uk.co.haxyshideout.vpnbegone.listeners;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.service.permission.PermissionService;
import uk.co.haxyshideout.vpnbegone.Permissions;
import uk.co.haxyshideout.vpnbegone.VPNBeGone;
import uk.co.haxyshideout.vpnbegone.storage.IPEntry;
import uk.co.haxyshideout.vpnbegone.storage.IPStorage;

import java.util.Optional;
import java.util.UUID;

public class LoginListener implements EventListener<ClientConnectionEvent.Join> {

    @Override
    public void handle(ClientConnectionEvent.Join event) {
        if(!event.getTargetEntity().hasPermission(Permissions.BYPASS)) {
            Sponge.getScheduler().createTaskBuilder().async().execute(() -> checkIP(event.getTargetEntity().getUniqueId(), event.getTargetEntity().getConnection().getAddress().getAddress().getHostAddress())).submit(VPNBeGone.getInstance());
        }//TODO logging for bypass / notify
    }

    //Runs async!!
    private void checkIP(UUID playerUUID, String ip) {
        IPStorage ipStorage = VPNBeGone.getIPStorage();
        Optional<IPEntry> ipEntryOpt = ipStorage.getIPEntry(ip);
        if(!ipEntryOpt.isPresent()) {
            VPNBeGone.getLogger().error("Failed to lookup details for ip "+ip);
            return;
        }

        IPEntry ipEntry = ipEntryOpt.get();
        if(!ipEntry.isResidential()) {
            //Kick the player if they are using a non residential ip if they are still online
            Sponge.getServer().getPlayer(playerUUID).ifPresent(player -> player.kick(VPNBeGone.getConfig().general.kickMessage));
        }


    }

}
