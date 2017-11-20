package exceptions;

public class DBFileException extends Exception{

    public DBFileException( Throwable cause ) {

        super( "DataBaseAccesor Files: " + cause.getMessage(), cause );

    }

}