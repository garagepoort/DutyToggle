package me.junny.dutytoggle.repository;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcsqlmigrations.helpers.QueryBuilderFactory;
import me.junny.dutytoggle.DutySession;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Class that is responsible for calling the database. Storing/retrieving session data.
 */
@IocBean
public class SessionRepository {

    private final QueryBuilderFactory query;

    public SessionRepository(QueryBuilderFactory query) {
        this.query = query;
    }

    public void saveSession(DutySession dutySession) {
        query.create()
            .insertQuery("INSERT INTO duty_sessions(player_uuid, player_name, group_name, back) " +
                    "VALUES(?, ?, ?, ?);",
                insert -> {
                    insert.setString(1, dutySession.player.getUniqueId().toString());
                    insert.setString(2, dutySession.player.getName());
                    insert.setString(3, String.join(";", dutySession.groups));
                    insert.setLong(4, dutySession.back);
                });
    }

    public List<DutySession> getAllSessions() {
        return query.create()
            .find("SELECT * FROM duty_sessions WHERE back = 0 OR back > ?",
                ps -> ps.setLong(1, System.currentTimeMillis()),
                this::buildSession);
    }

    public List<DutySession> getExpiredSessions() {
        return query.create()
            .find("SELECT * FROM duty_sessions WHERE back != 0 AND back <= ?",
                ps -> ps.setLong(1, System.currentTimeMillis()),
                this::buildSession);
    }

    private DutySession buildSession(ResultSet rs) throws SQLException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("player_uuid")));
        return new DutySession(rs.getInt("ID"), player, Arrays.asList(rs.getString("group_name").split(";")), rs.getLong("back"));
    }

    public void deleteSession(UUID playerUuid) {
        query.create()
            .deleteQuery("DELETE FROM duty_sessions WHERE player_uuid = ?",
                insert -> insert.setString(1, playerUuid.toString()));
    }

    public Optional<DutySession> getSession(UUID uniqueId) {
        return query.create()
            .findOne("SELECT * FROM duty_sessions WHERE player_uuid = ?",
                ps -> ps.setString(1, uniqueId.toString()), this::buildSession);
    }
}
