package me.junny.dutytoggle.repository;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcsqlmigrations.helpers.QueryBuilderFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that is responsible for calling the database. Storing/retrieving session data.
 */
@IocBean
public class LuckPermsRepository {

    private final QueryBuilderFactory query;

    public LuckPermsRepository(QueryBuilderFactory query) {
        this.query = query;
    }

    public List<String> getAllNamesWithPrimaryGroup(List<String> groups) {
        List<String> questionMarks = groups.stream().map(p -> "?").collect(Collectors.toList());
        String queryString = String.format("SELECT * FROM luckperms_user_permissions lup inner join luckperms_players lp on lup.uuid = lp.uuid WHERE lup.permission in (%s)", String.join(", ", questionMarks));

        return query.create()
            .find(queryString,
                ps -> {
                    int index = 1;
                    for (String group : groups) {
                        ps.setString(index, group);
                        index++;
                    }
                },
                rs -> rs.getString("username"));
    }
}
