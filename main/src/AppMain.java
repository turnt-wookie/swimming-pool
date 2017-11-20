import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.concurrent.TimeUnit;

public class AppMain {

    public static void main(String[] args) {
        String filePath = new File("").getAbsolutePath();
        String configurationFilePath = filePath + "/main/src/config.json";

        while (true) {
            try {
                DatabaseConnectionsPool pool = new DatabaseConnectionsPool(configurationFilePath);
                DatabaseConnection connection = pool.acquireConnection();

                ResultSet rs = connection.getConnection().query("SELECT * FROM duck;");
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
                TimeUnit.SECONDS.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("--------------");
        }
    }
}
