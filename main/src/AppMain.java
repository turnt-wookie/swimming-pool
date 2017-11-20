import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class AppMain {

    public static void main(String[] args) {
        String filePath = new File("").getAbsolutePath();
        String configurationFilePath = filePath + "/main/src/config.json";

        try {
            DatabaseConnectionsPool pool = new DatabaseConnectionsPool(configurationFilePath);
            DatabaseConnection connection = pool.acquireConnection();

            connection.getConnection().query("CREATE database IF NOT EXISTS `ugly-duck`;");
            ResultSet rs = connection.getConnection().query("SELECT 1+1 as Suma FROM DUAL;");
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

            pool.releaseConnection(connection);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
