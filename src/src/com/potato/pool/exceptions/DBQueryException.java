package com.potato.pool.exceptions;

import java.sql.SQLException;

public class DBQueryException extends Exception {
    public DBQueryException(SQLException ex) {
    }
}
