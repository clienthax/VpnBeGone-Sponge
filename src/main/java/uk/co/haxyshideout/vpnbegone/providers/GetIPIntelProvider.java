package uk.co.haxyshideout.vpnbegone.providers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import uk.co.haxyshideout.vpnbegone.VPNBeGone;
import uk.co.haxyshideout.vpnbegone.storage.IPEntry;
import uk.co.haxyshideout.vpnbegone.utils.HttpUtils;

import java.util.Optional;

public class GetIPIntelProvider implements IPInfoProvider {

    private boolean exceededFree = false;

    @Override
    public Optional<IPEntry> getIPEntry(String ip) {
        //Make sure we don't spam the api if we run out of requests
        if(exceededFree) {
            VPNBeGone.getLogger().error("Out of free requests for provider "+apiName()+" paid keys not currently supported");
            return Optional.empty();
        }

        String jsonString = HttpUtils.getStringFromSite(getApiEndpointForIP(ip));
        if(jsonString.isEmpty()) {
            VPNBeGone.getLogger().error("Failed to pull information from "+apiName());
            return Optional.empty();
        }

        if(jsonString.equals("429")) {//TODO nicer way to handle this
            VPNBeGone.getLogger().error("Out of free requests for provider "+apiName());
            exceededFree = true;
            return Optional.empty();
        }

        //Parse api response
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        if(!json.get("status").getAsString().equals("success")) {
            VPNBeGone.getLogger().error(apiName()+" api returned error");
            return Optional.empty();
        }

        boolean isHost = json.get("result").getAsBoolean();
        String organization = json.get("ASN Name").getAsString();

        IPEntry ipEntry = new IPEntry(apiName(), ip, !isHost, organization);

        return Optional.of(ipEntry);
    }

    @Override
    public String apiName() {
        return "GetIPIntelProvider";
    }

    @Override
    public String apiProviderWebsite() {
        return "https://getipintel.net/";
    }

    @Override
    public String getApiEndpointForIP(String ip) {
        String apiKey = VPNBeGone.getConfig().providers.getIPIntelConfig.apiKey;

        //Free
        if(apiKey.isEmpty()) {
            //The oflags n is undocumented on the api, it returns the ASN name
            return "http://check.getipintel.net/check.php?format=json&contact=clienthax@gmail.com&ip="+ip+"&flags=m&oflags=n";
        }

        VPNBeGone.getLogger().error("Paid keys not supported for "+apiName());

        //Paid
        return "http://api.vpnblocker.net/v2/json/"+ip+"/";
    }

}
