package me.junny.dutytoggle.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.junny.dutytoggle.util.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class MySQLConnectionProvider {

    private static MySQLConnectionProvider INSTANCE;

    private final String host;
    private final String user;
    private final String database;
    private final String password;
    private final int port;

    private HikariDataSource datasource;

    private MySQLConnectionProvider() {
        YamlConfiguration yamlConfiguration = FileManager.instance().getConfig("config.yml").get();
        port = yamlConfiguration.getInt("database.port");
        host = yamlConfiguration.getString("database.host");
        user = yamlConfiguration.getString("database.user");
        database = yamlConfiguration.getString("database.database");
        password = yamlConfiguration.getString("database.password");
    }

    public static MySQLConnectionProvider instance() {
        if(INSTANCE == null) {
            INSTANCE = new MySQLConnectionProvider();
        }
        return INSTANCE;
    }

    public DataSource getDatasource() {
        if(datasource == null){
            getDataSource();
        }
        return datasource;
    }

    public Connection getConnection() {
        try {
            // retrieve one connection from the connection pool
            // This opens a connection to the database
            Class.forName("com.mysql.jdbc.Driver");
            return getDatasource().getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            throw new DatabaseException("Failed to connect to the database", e);
        }
    }

    private void getDataSource() {
        // Setup the hikari datasource.
        // A datasource is a class from the JDBC API which is responsible for opening and maintaining the connections to the database
        // Hikari is a library that implements the JDBC API and allows for connection pooling, increasing the speed by which we can open connections to the database.
        if(datasource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false&allowMultiQueries=true");
            config.setUsername(user);
            config.setPassword(password);
            config.setMaximumPoolSize(5);
            config.setLeakDetectionThreshold(5000);
            config.setAutoCommit(true);
            config.setDriverClassName("com.mysql.jdbc.Driver");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            datasource = new HikariDataSource(config);
        }
    }
}
