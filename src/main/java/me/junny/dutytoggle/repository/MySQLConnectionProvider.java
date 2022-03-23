package me.junny.dutytoggle.repository;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigProperty;
import be.garagepoort.mcsqlmigrations.DatabaseType;
import be.garagepoort.mcsqlmigrations.SqlConnectionProvider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@IocBean
public class MySQLConnectionProvider implements SqlConnectionProvider {

    @ConfigProperty("database.host")
    private String host;
    @ConfigProperty("database.user")
    private String user;
    @ConfigProperty("database.database")
    private String database;
    @ConfigProperty("database.password")
    private String password;
    @ConfigProperty("database.port")
    private int port;

    private HikariDataSource datasource;

    public MySQLConnectionProvider() {}

    public DataSource getDatasource() {
        if(datasource == null){
            getDataSource();
        }
        return datasource;
    }

    @Override
    public List<String> getMigrationPackages() {
        return Collections.emptyList();
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.MYSQL;
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
