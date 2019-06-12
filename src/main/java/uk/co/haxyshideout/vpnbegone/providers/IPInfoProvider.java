package uk.co.haxyshideout.vpnbegone.providers;

import uk.co.haxyshideout.vpnbegone.storage.IPEntry;

import java.util.Optional;

public interface IPInfoProvider {

    /**
     * Gets the IPEntry for the specified LookupIP from the provider api,
     * This will be stored to the local cache after.
     *
     * @param ip ip to lookup
     * @return IPEntry for the specified LookupIP
     */
    Optional<IPEntry> getIPEntry(String ip);

    /**
     * Gets the name of the API
     *
     * @return name of the api
     */
    String apiName();

    /**
     * Gets the main website for the api provider
     * @return website for provider
     */
    String apiProviderWebsite();

    /**
     * Gets the endpoint for an LookupIP,
     * This will return a paid link if the api key is present in the config for the provider
     * @param ip ip to generate link for
     * @return URL containing endpoint with ip embedded
     */
    String getApiEndpointForIP(String ip);

}
