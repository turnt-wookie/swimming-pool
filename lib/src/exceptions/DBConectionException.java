package exceptions;

public class DBConectionException extends Exception{

    public DBConectionException( Throwable cause ) {

        super( "DataBaseAccesor Connections: "+cause.getMessage(), cause );

    }
}