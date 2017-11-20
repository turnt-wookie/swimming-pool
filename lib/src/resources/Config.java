package resources;

import exceptions.ConfigKeyNotFoundException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Config {

    private JSONFileReader jsonFileReader = new JSONFileReader();

    private JSONObject configurationObject;

    public Config(String configurationFilePath) throws IOException, ParseException {
        this.configurationObject = this.jsonFileReader.getJSONObject( configurationFilePath );
    }

    public int getDatabasePort() throws ConfigKeyNotFoundException {
        return Math.toIntExact( (Long) this.getKey( "DB_PORT" ) );
    }

    public String getDatabaseHost() throws ConfigKeyNotFoundException {
        return (String) this.getKey("DB_HOST");

    }

    public String getDatabaseName() throws ConfigKeyNotFoundException {
        return (String) this.getKey("DB_DATABASE");


    }

    public String getDatabaseUsername() throws ConfigKeyNotFoundException {
        return (String) this.getKey("DB_USERNAME");


    }

    public String getDatabasePassword() throws ConfigKeyNotFoundException {
        return (String) this.getKey("DB_PASSWORD");

    }

    public int getBlockSize() {

        int blockSize = 0;
        try {
            blockSize = Math.toIntExact( (Long) this.getKey("blockSize") );
        } catch (ConfigKeyNotFoundException e) {
        }
        return blockSize;

    }

    public int getMaxPoolSize() {

        int blockSize = this.getBlockSize();
        int maxPoolSize = 0;
        try {
            maxPoolSize = Math.toIntExact((Long) this.getKey("maxPoolSize"));
        } catch (ConfigKeyNotFoundException e) {

        }

        boolean isMaxPoolSizeMultipleOfBlockSize = (maxPoolSize % blockSize) != 0;

        if (!isMaxPoolSizeMultipleOfBlockSize) {
            maxPoolSize = maxPoolSize - (maxPoolSize % blockSize);
        }

        return maxPoolSize;

    }

    private Object getKey(String key) throws ConfigKeyNotFoundException {
        Object result = this.configurationObject.get( key );

        if(result == null) {
            throw new ConfigKeyNotFoundException("The '" + key + "' was not set in the Configuration JSON");
        }

        return result;
    }

}
