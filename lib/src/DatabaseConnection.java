import exceptions.DBQueryException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private int id;

    private Connection connection;

    private boolean acquired;

    private boolean deprecated;


    DatabaseConnection( int id, Connection connection ) {
        this.id = id;
        this.connection = connection;
    }

    public void testPrintId() {
        System.out.println( this.getId() );
    }

    public ResultSet query(String query) throws DBQueryException, SQLException {

        Statement statement = this.connection.createStatement();

        return statement.executeQuery(query);
    }

    int getId() {
        return id;
    }

    void setId( int id ) {
        this.id = id;
    }


    protected Connection getConnection() {
        return connection;
    }

    void setConnection(Connection connection) {

        this.connection = connection;
    }

    boolean isAcquired() {
        return acquired;
    }

    void setAcquired( boolean acquired ) {
        this.acquired = acquired;
    }

    boolean isDeprecated() {
        return deprecated;
    }

    void setDeprecated( boolean deprecated ) {
        this.deprecated = deprecated;
    }

    void disconnect(){
        try {
            this.connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

