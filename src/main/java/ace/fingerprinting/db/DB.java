package ace.fingerprinting.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ace.fingerprinting.model.FpInfo;


public class DB {
    private Connection connection;

    private static final String tableName = "fp";

    static {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void open() throws SQLException {
        connection = DriverManager.getConnection("jdbc:derby://localhost:1527/fp");

        // We want to control transactions manually. Autocommit is on by
        // default in JDBC.
        connection.setAutoCommit(false);
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public Optional<FpInfo> select(String id) throws SQLException {
        String sql = MessageFormat.format(
                "SELECT id, time, ip_address, campaign_id, template_id, message_id, destination_url, browser_fp FROM {0} WHERE id = ''{1}''",
                tableName,
                id);
        System.out.println("Executing query: " + sql);
        Statement statement = connection.createStatement();
        try (ResultSet resultSet = statement.executeQuery(sql)) {
            if (!resultSet.next()) {
                return Optional.empty();
            } else {
                return Optional.of(getFpInfo(resultSet));
            }
        }
    }

    public List<FpInfo> selectAll() throws SQLException {
        List<FpInfo> fpInfos = new ArrayList<>();

        String sql = MessageFormat.format(
                "SELECT id, time, ip_address, campaign_id, template_id, message_id, destination_url, browser_fp FROM {0}",
                tableName);

        Statement statement = connection.createStatement();
        try (ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                fpInfos.add(getFpInfo(resultSet));
            }
        }

        return fpInfos;
    }

    private FpInfo getFpInfo(ResultSet resultSet) throws SQLException {
        FpInfo fpInfo = new FpInfo();
        fpInfo.setId(resultSet.getString(1));
        long time = resultSet.getLong(2);
        fpInfo.setTime(new Date(time));
        fpInfo.setIpAddress(resultSet.getString(3));
        fpInfo.setCampaignId(resultSet.getString(4));
        fpInfo.setTemplateId(resultSet.getString(5));
        fpInfo.setMessageId(resultSet.getString(6));
        fpInfo.setDestinationUrl(resultSet.getString(7));
        fpInfo.setBrowserFp(resultSet.getString(8));
        return fpInfo;
    }

    public void create(FpInfo fpInfo) throws SQLException {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}) VALUES ({2})",
                tableName,
                getColumnNames(getDbValues(fpInfo)),
                getQuotedColumnValues(getDbValues(fpInfo)));
        System.out.println("Executing query: " + sql);
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    public void update(FpInfo fpInfo) throws SQLException {
        String sql = MessageFormat.format("UPDATE {0} SET {1} WHERE id = ''{2}''",
                tableName,
                getUpdateSetString(getDbValues(fpInfo)),
                fpInfo.getId());
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    public void delete(FpInfo fpInfo) throws SQLException {
        String sql = MessageFormat.format("DELETE FROM {0} WHERE id = ''{1}''",
                tableName,
                fpInfo.getId());
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    static class DbValue {
        String columnName;
        Object columnValue;

        DbValue(String columnName, Object columnValue) {
            this.columnName = columnName;
            this.columnValue = columnValue;
        }
    }

    static String getUpdateSetString(List<DbValue> dbValues) {
        return dbValues.stream().map(DB::mapToUpdateString).flatMap(Optional::stream).collect(Collectors.joining(","));
    }

    static Optional<String> mapToUpdateString(DbValue dbValue) {
        if (dbValue.columnValue == null) {
            return Optional.empty();
        } else {
            return Optional.of(MessageFormat.format("{0} = {1}", dbValue.columnName, valueInQuotes(dbValue.columnValue)));
        }
    }

    static Optional<String> mapToColumnName(DbValue dbValue) {
        if (dbValue.columnValue == null) {
            return Optional.empty();
        } else {
            return Optional.of(dbValue.columnName);
        }
    }

    static Optional<Object> mapToColumnValue(DbValue dbValue) {
        if (dbValue.columnValue == null) {
            return Optional.empty();
        } else {
            return Optional.of(dbValue.columnValue);
        }
    }

    static List<DbValue> getDbValues(FpInfo fpInfo) {
        List<DbValue> dbValues = new ArrayList<>();

        dbValues.add(new DbValue("id", fpInfo.getId()));
        dbValues.add(new DbValue("time", fpInfo.getTime() == null ? null : fpInfo.getTime().getTime()));
        dbValues.add(new DbValue("ip_address", fpInfo.getIpAddress()));
        dbValues.add(new DbValue("campaign_id", fpInfo.getCampaignId()));
        dbValues.add(new DbValue("template_id", fpInfo.getTemplateId()));
        dbValues.add(new DbValue("message_id", fpInfo.getMessageId()));
        dbValues.add(new DbValue("destination_url", fpInfo.getDestinationUrl()));
        dbValues.add(new DbValue("browser_fp", fpInfo.getBrowserFp()));

        return dbValues;
    }

    static String getColumnNames(List<DbValue> dbValues) {
        return dbValues.stream().map(DB::mapToColumnName).flatMap(Optional::stream).collect(Collectors.joining(","));
    }

    static String getQuotedColumnValues(List<DbValue> dbValues) {
        return dbValues.stream().map(DB::mapToColumnValue).flatMap(Optional::stream).map(DB::valueInQuotes).collect(Collectors.joining(","));
    }

    static String inQuotes(String val) {
        return MessageFormat.format("''{0}''", val);
    }

    static String valueInQuotes(Object value) {
        if (value instanceof String) {
            return inQuotes(String.class.cast(value));
        } else if (value instanceof Long) {
            return "" + value;
        } else {
            throw new IllegalStateException();
        }
    }
}
