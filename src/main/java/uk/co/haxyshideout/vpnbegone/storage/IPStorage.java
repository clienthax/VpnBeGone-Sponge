package uk.co.haxyshideout.vpnbegone.storage;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;
import uk.co.haxyshideout.vpnbegone.VPNBeGone;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public class IPStorage {

    private SqlService sql;
    private String uri;

    public IPStorage() {
        uri = "jdbc:h2:"+VPNBeGone.getConfigFolder().resolve("ipcache.db").toString();

        try {
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DataSource getDataSource(String jdbcUrl) throws SQLException {
        if(sql == null) {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return sql.getDataSource(jdbcUrl);
    }

    public void createTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS IPCACHE (" +
                "ip VARCHAR(45) not NULL," +
                " providerName VARCHAR(30) not NULL," +
                " residential BOOLEAN not NULL," +
                " organization VARCHAR(255) not NULL," +
                " timeRecorded TIMESTAMP not NULL, " +//WITHOUT TIME ZONE
                " PRIMARY KEY (ip))";

        final Connection conn = getDataSource(uri).getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public void addIPEntryToDB(IPEntry ipEntry) throws SQLException {
        final String sql = "INSERT INTO IPCACHE (ip, providerName, residential, organization, timeRecorded) " +
                "VALUES(?,?,?,?,?)";

        final Connection conn = getDataSource(uri).getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, ipEntry.getIp());
        stmt.setString(2, ipEntry.getProviderName());
        stmt.setBoolean(3, ipEntry.isResidential());
        stmt.setString(4, ipEntry.getOrganization());
        stmt.setObject(5, ipEntry.getTimeRecorded());
        stmt.executeUpdate();
        stmt.close();
        conn.close();

    }

    public Optional<IPEntry> getIPEntryFromDB(String ip) throws SQLException {
        final String sql = "SELECT * FROM IPCACHE WHERE ip = '"+ip+"'";

        try(Connection conn = getDataSource(uri).getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet results = stmt.executeQuery()) {
            while (results.next()) {
                String ip1 = results.getString("ip");
                String providerName = results.getString("providerName");
                boolean residential = results.getBoolean("residential");
                String organization = results.getString("organization");
                LocalDateTime timeRecorded = results.getObject("timeRecorded", LocalDateTime.class);

                return Optional.of(new IPEntry(providerName, ip1, residential, organization, timeRecorded));
            }
        }

        //TODO
        return Optional.empty();
    }

    /**
     * Request the information for a specific ip.
     * This should check the local datasource before querying the api providers.
     * If the data has expired it should be ignored unless the api does not provide information when requested.
     * @param ip ip to get information for
     * @return IPEntry
     */
    public Optional<IPEntry> getIPEntry(String ip) {

        try {
            Optional<IPEntry> ipEntryFromDBOpt = getIPEntryFromDB(ip);
            if (ipEntryFromDBOpt.isPresent()) {
                //TODO data expiry
                return ipEntryFromDBOpt;
            }

            //Request data from provider
            Optional<IPEntry> ipEntryFromProviderOpt = VPNBeGone.getProvider().getIPEntry(ip);
            //Add to local DB
            if (ipEntryFromProviderOpt.isPresent()) {
                addIPEntryToDB(ipEntryFromProviderOpt.get());
            }

            return ipEntryFromProviderOpt;
        } catch (Exception e) {
            VPNBeGone.getLogger().error("Error getting ip details", e);
        }

        return Optional.empty();
    }



}
