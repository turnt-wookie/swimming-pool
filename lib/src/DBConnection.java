import exceptions.DBQueryException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DBConnection implements Connection {

    public ResultSet query(String query) throws DBQueryException {

        ResultSet result;

        try {
            Statement stmt = this.createStatement();
            result = stmt.executeQuery(query);
        } catch (SQLException ex) {
            throw new DBQueryException(ex);
        }

        return result;
    }

    public void disconnect(){

        try {
            this.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

}

