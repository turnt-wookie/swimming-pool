import exceptions.DBQueryException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection{

    private final Connection JDBCConnection;

    public DBConnection(Connection connection) {
        this.JDBCConnection = connection;

    }

    public ResultSet query(String query) throws DBQueryException {

        ResultSet result;

        try {

            Statement stmt = null;
            stmt = (Statement) JDBCConnection.createStatement();
            result = stmt.executeQuery(query);


        } catch (SQLException ex) {

            throw new DBQueryException(ex);

        }

        return result;
    }

    public void disconnect(){

        try {
            JDBCConnection.close();
        } catch (SQLException ex) {

        }

    }

}

