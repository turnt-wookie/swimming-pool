package exceptions;

public class DBQueryException extends Exception{

    public DBQueryException( Throwable cause ) {

        super( "DataBaseAccesor Querys: "+cause.getMessage(), cause );

    }

}