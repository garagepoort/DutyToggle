package me.junny.dutytoggle.repository;

import java.sql.SQLException;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String s, Exception e) {
        super(s, e);
    }

    public DatabaseException(SQLException e) {
        super("Error executing database query",e);
    }
}
