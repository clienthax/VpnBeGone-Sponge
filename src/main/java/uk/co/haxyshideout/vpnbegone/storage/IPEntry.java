package uk.co.haxyshideout.vpnbegone.storage;

import java.time.LocalDateTime;

public class IPEntry {

    final private String ip;
    final private boolean residential;
    final private String organization;
    //DateTime when the information was requested from the api
    final private LocalDateTime timeRecorded;
    //Name of the provider that provided the data for this LookupIP
    final private String providerName;

    public IPEntry(String providerName, String ip, boolean residential, String organization, LocalDateTime timeRecorded) {
        this.providerName = providerName;
        this.ip = ip;
        this.residential = residential;
        this.organization = organization;
        this.timeRecorded = timeRecorded;
    }

    public IPEntry(String providerName, String ip, boolean residential, String organization) {
        this(providerName, ip, residential, organization, LocalDateTime.now());
    }

    public String getIp() {
        return ip;
    }

    public boolean isResidential() {
        return residential;
    }

    public String getOrganization() {
        return organization;
    }

    public LocalDateTime getTimeRecorded() {
        return timeRecorded;
    }

    public String getProviderName() {
        return providerName;
    }
}
