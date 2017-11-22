import exceptions.ConfigKeyNotFoundException;
import exceptions.DBFileException;
import exceptions.DBInvalidSettingsException;
import exceptions.DBQueryException;
import resources.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseInitialize {

    private String DB_CONNECTION;
    private String DB_HOST;
    private String DB_PORT;
    private String DB_DATABASE;
    private String DB_USERNAME;
    private String DB_PASSWORD;

    private FileChangeListener listener;

    private final DatabaseConnectionsPool connectionPool;

    public DatabaseInitialize(DatabaseConnectionsPool connectionPool, Config config) throws ConfigKeyNotFoundException {

        this.connectionPool = connectionPool;

        this.DB_DATABASE = config.getDatabaseName();
        this.DB_HOST = config.getDatabaseHost();
        this.DB_PORT = Integer.toString(config.getDatabasePort());
        this.DB_USERNAME = config.getDatabaseUsername();
        this.DB_PASSWORD = config.getDatabasePassword();

    }


    public DBConnection getConnection() throws DBQueryException,
            DBInvalidSettingsException, DBFileException {

        DBConnection connection = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", DB_USERNAME);
        connectionProps.put("password", DB_PASSWORD);

        try {

            String link = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_DATABASE;



            connection = (DBConnection) DriverManager.getConnection(link, connectionProps);


        } catch (SQLException ex) {

            ex.printStackTrace();

            throw new DBQueryException(ex);

        }

        return connection;

    }


    public void notifyFileChange() {

        if (isConnectionWorking()) {
            this.connectionPool.updateConnections();
        }
    }

    private boolean isConnectionWorking() {

        try {
            this.getConnection();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

}
