package exceptions;

public class DBInvalidSettingsException extends Exception{

    public DBInvalidSettingsException() {
        super();
    }

    public DBInvalidSettingsException(String message) {
        super(message);
    }

    public DBInvalidSettingsException(Throwable cause) {
        super("DataBaseAccesor Settings: "+cause.getMessage(), cause);
    }


}