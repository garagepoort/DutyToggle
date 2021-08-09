package me.junny.dutytoggle.repository;

import me.junny.dutytoggle.DutySession;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Class that is responsible for calling the database. Storing/retrieving session data.
 */
public class SessionRepository {

    private static SessionRepository INSTANCE;
    private final MySQLConnectionProvider connectionProvider;

    private SessionRepository(MySQLConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public static SessionRepository instance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionRepository(MySQLConnectionProvider.instance());
        }
        return INSTANCE;
    }

    public Connection getConnection() {
        return connectionProvider.getConnection();
    }

    public void saveSession(DutySession dutySession) {
        // We use the JDBC API to execute an INSERT query against the database, saving the session
        try (Connection sql = getConnection();
             PreparedStatement insert = sql.prepareStatement("INSERT INTO duty_sessions(player_uuid, player_name, group_name, back) " +
                     "VALUES(?, ?, ?, ?);")) {
            insert.setString(1, dutySession.player.getUniqueId().toString());
            insert.setString(2, dutySession.player.getName());
            insert.setString(3, dutySession.groupName);
            insert.setLong(4, dutySession.back);
            insert.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public List<DutySession> getAllSessions() {
        List<DutySession> sessions = new ArrayList<>();
        try (Connection sql = getConnection();
             PreparedStatement ps = sql.prepareStatement("SELECT * FROM duty_sessions WHERE back = 0 OR back > ?")) {
            ps.setLong(1, System.currentTimeMillis());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sessions.add(buildSession(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return sessions;
    }

    public List<DutySession> getExpiredSessions() {
        List<DutySession> sessions = new ArrayList<>();
        try (Connection sql = getConnection();
             PreparedStatement ps = sql.prepareStatement("SELECT * FROM duty_sessions WHERE back != 0 AND back <= ?")) {
            ps.setLong(1, System.currentTimeMillis());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sessions.add(buildSession(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return sessions;
    }

    private DutySession buildSession(ResultSet rs) throws SQLException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("player_uuid")));
        return new DutySession(rs.getInt("ID"), player, rs.getString("group_name"), rs.getLong("back"));
    }

    public void deleteSession(UUID playerUuid) {
        try (Connection sql = getConnection();
             PreparedStatement insert = sql.prepareStatement("DELETE FROM duty_sessions WHERE player_uuid = ?");) {
            insert.setString(1, playerUuid.toString());
            insert.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public Optional<DutySession> getSession(UUID uniqueId) {
        try (Connection sql = getConnection();
             PreparedStatement ps = sql.prepareStatement("SELECT * FROM duty_sessions WHERE player_uuid = ?")) {
            ps.setString(1, uniqueId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = rs.next();
                if (first) {
                    return Optional.of(buildSession(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return Optional.empty();
    }
}
