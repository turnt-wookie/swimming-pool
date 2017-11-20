import exceptions.ConfigKeyNotFoundException;
import resources.Config;

import java.io.File;
import java.io.IOException;

public class AppMain {

    public static void main(String[] args) {
        String filePath = new File("").getAbsolutePath();
        String configurationFilePath = filePath + "/main/src/config.json";

        try {
            Config config = new Config(configurationFilePath);

            System.out.println(config.getDatabaseHost());

        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConfigKeyNotFoundException e) {
            e.printStackTrace();
        }

    }
}
