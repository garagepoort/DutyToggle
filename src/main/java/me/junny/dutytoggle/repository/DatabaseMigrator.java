package me.junny.dutytoggle.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMigrator {

    public static void executeMigrations() {
        try (Connection sql = MySQLConnectionProvider.instance().getConnection();
             PreparedStatement migrate = sql.prepareStatement("CREATE TABLE IF NOT EXISTS duty_sessions ( " +
                     "ID INT NOT NULL AUTO_INCREMENT, " +
                     "player_uuid VARCHAR(36) NOT NULL, " +
                     "player_name VARCHAR(36) NOT NULL, " +
                     "group_name VARCHAR(256) NOT NULL, " +
                     "back BIGINT NOT NULL, " +
                     "PRIMARY KEY (ID)) ENGINE = InnoDB;")) {
            migrate.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
