import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class AppMain {

    public static void main(String[] args) {
        String filePath = new File("").getAbsolutePath();
        String configurationFilePath = filePath + "/main/src/config.json";

        try {
            DatabaseConnectionsPool pool = new DatabaseConnectionsPool(configurationFilePath);
            DatabaseConnection dbConnection = pool.acquireConnection();

            ResultSet rs = dbConnection.query("SELECT 1+1 as Suma FROM DUAL;");

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            System.out.println( "Column|Value");

            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = rs.getString(i);
                    System.out.print( rsmd.getColumnName(i) + "|" + columnValue);
                }
                System.out.println("");
            }

            pool.releaseConnection(dbConnection);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
