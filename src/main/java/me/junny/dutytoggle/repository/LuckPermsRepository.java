package me.junny.dutytoggle.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that is responsible for calling the database. Storing/retrieving session data.
 */
public class LuckPermsRepository {

    private static LuckPermsRepository INSTANCE;
    private final MySQLConnectionProvider connectionProvider;

    private LuckPermsRepository(MySQLConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public static LuckPermsRepository instance() {
        if (INSTANCE == null) {
            INSTANCE = new LuckPermsRepository(MySQLConnectionProvider.instance());
        }
        return INSTANCE;
    }

    public Connection getConnection() {
        return connectionProvider.getConnection();
    }

    public List<String> getAllNamesWithPrimaryGroup(List<String> groups) {
        List<String> questionMarks = groups.stream().map(p -> "?").collect(Collectors.toList());
        String query = String.format("SELECT * FROM luckperms_user_permissions lup inner join luckperms_players lp on lup.uuid = lp.uuid WHERE lup.permission in (%s)", String.join(", ", questionMarks));

        List<String> sessions = new ArrayList<>();
        try (Connection sql = getConnection();
             PreparedStatement ps = sql.prepareStatement(query)) {
            int index = 1;
            for (String group : groups) {
                ps.setString(index, group);
                index++;
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sessions.add(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return sessions;
    }

}
