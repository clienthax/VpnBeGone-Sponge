package uk.co.haxyshideout.vpnbegone.providers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import uk.co.haxyshideout.vpnbegone.VPNBeGone;
import uk.co.haxyshideout.vpnbegone.storage.IPEntry;
import uk.co.haxyshideout.vpnbegone.utils.HttpUtils;

import java.util.Optional;

public class VpnBlockerProvider implements IPInfoProvider {

    private int remainingRequests = -1;

    @Override
    public Optional<IPEntry> getIPEntry(String ip) {
        //Make sure we don't spam the api if we run out of requests
        if(remainingRequests == 0) {
            VPNBeGone.getLogger().error("Out of free requests for provider "+apiName()+" please purchase a api key from "+apiProviderWebsite());
            return Optional.empty();
        }

        String jsonString = HttpUtils.getStringFromSite(getApiEndpointForIP(ip));
        if(jsonString.isEmpty()) {
            VPNBeGone.getLogger().error("Failed to pull information from "+apiName());
            return Optional.empty();
        }

        //Parse api response
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        if(!json.get("status").getAsString().equals("success")) {
            VPNBeGone.getLogger().error(apiName()+" api returned error");
            return Optional.empty();
        }

        String apiPackage = json.get("package").getAsString();
        boolean isHost = json.get("host-ip").getAsBoolean();
        String organization = json.get("org").getAsString();

        //Remaining requests only listed on free package
        if(apiPackage.equals("Free")) {
            //Update so we don't spam the api if we run out of requests
            remainingRequests = json.get("remaining_requests").getAsInt();
        }

        IPEntry ipEntry = new IPEntry(apiName(), ip, !isHost, organization);
        //TODO professional api vars (if anyone ever needs them)

        return Optional.of(ipEntry);
    }

    @Override
    public String apiName() {
        return "VpnBlockerProvider";
    }

    @Override
    public String apiProviderWebsite() {
        return "https://vpnblocker.net/";
    }

    @Override
    public String getApiEndpointForIP(String ip) {
        String apiKey = VPNBeGone.getConfig().providers.vpnBlockerConfig.apiKey;

        //Free
        if(apiKey.isEmpty()) {
            return "http://api.vpnblocker.net/v2/json/"+ip;
        }

        //Paid
        return "http://api.vpnblocker.net/v2/json/"+ip+"/"+VPNBeGone.getConfig().providers.vpnBlockerConfig.apiKey;
    }

    //https://vpnblocker.net/usage

    /**
     * Free
     * ~$ curl "http://api.vpnblocker.net/v2/json/192.184.93.53"
     * {
     *     "status": "success",
     *     "package": "Free",
     *     "remaining_requests": 499,
     *     "ipaddress": "192.184.93.53",
     *     "host-ip": true,
     *     "org": "RamNode LLC"
     * }
     *
     * Basic
     * ~$ curl "http://api.vpnblocker.net/v2/json/192.184.93.53/API KEY"
     * {
     *     "status": "success",
     *     "package": "Basic",
     *     "ipaddress": "192.184.93.53",
     *     "host-ip": true,
     *     "org": "RamNode LLC"
     * }
     *
     * Professional
     * ~$ curl "http://api.vpnblocker.net/v2/json/192.184.93.53/API KEY"
     * {
     *     "status": "success",
     *     "package": "Professional",
     *     "ipaddress": "192.184.93.53",
     *     "host-ip": true,
     *     "org": "RamNode LLC",
     *     "country": {
     *         "name": "United States",
     *         "code": "US"
     *     },
     *     "subdivision": {
     *         "name": "Georgia",
     *         "code": "GA"
     *     },
     *     "city": "Macon",
     *     "postal": "31205",
     *     "location": {
     *         "lat": 32.8407,
     *         "long": -83.6324
     *     }
     * }
     */



}
