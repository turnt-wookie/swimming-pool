import exceptions.ConfigKeyNotFoundException;
import exceptions.PoolBadConfigurationException;
import exceptions.PoolBadConnectionException;
import exceptions.PoolNoConnectionsAvailableException;
import resources.Config;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class DatabaseConnectionsPool {

    private DatabaseInitialize databaseInitialize;

    private int blockSize;

    private int maxPoolSize;

    private int amountBlocks;

    private int amountAcquiredConnections;

    private DatabaseConnection[] pool;

    private Config config;


    public DatabaseConnectionsPool(String configFile) throws PoolBadConfigurationException, PoolBadConnectionException, ConfigKeyNotFoundException {

        try {

            this.config = new Config(configFile);
            FileChangeListener listener = new FileChangeListener(configFile);


            this.databaseInitialize = new DatabaseInitialize( this, config );

            this.blockSize = config.getBlockSize();
            this.maxPoolSize = config.getMaxPoolSize();

            this.amountBlocks = 0;
            this.amountAcquiredConnections = 0;

        } catch ( IOException | ParseException exception ) {

            throw new PoolBadConfigurationException( "Cannot read configuration file.", exception );

        }

        this.initializePool();

    }

    public DatabaseConnection acquireConnection() throws PoolNoConnectionsAvailableException, PoolBadConnectionException {

        DatabaseConnection databaseConnection = this.searchForNotAcquiredConnection();
        databaseConnection.setAcquired( true );
        this.amountAcquiredConnections++;

        /* Lets check if poolSize increase is required: */
        int totalConnections = this.blockSize * this.amountBlocks;
        int maxBlocksAmount = this.maxPoolSize / this.blockSize;

        boolean isPoolSizeIncreaseNecessary = totalConnections - this.amountAcquiredConnections > 1;
        boolean canIncreasePoolSize = this.amountBlocks < maxBlocksAmount;

        if ( isPoolSizeIncreaseNecessary && canIncreasePoolSize  ) {
            this.increasePoolSize();
        }
        /* --- */

        return databaseConnection;

    }

    public void releaseConnection( DatabaseConnection connection ) throws Exception {

        if ( connection.isDeprecated() ) {

            connection.setConnection( this.databaseInitialize.getConnection() );
            connection.setDeprecated( false );

        }

        connection.setAcquired( false );
        this.amountAcquiredConnections--;

        /* Lets check if poolSize reduction is possible: */
        int totalConnections = this.blockSize * this.amountBlocks;
        boolean shouldReducePoolSize = ( totalConnections - this.amountAcquiredConnections ) > this.blockSize;

        if ( shouldReducePoolSize ) {
            this.decreasePoolSize();
        }
        /* --- */

    }


    public void updateConnections() {

        for ( int i = 0; i < this.maxPoolSize; i++ ) {

            if ( this.pool[ i ] != null ) {

                DatabaseConnection actualIterationConnection = this.pool[i];

                if ( ! actualIterationConnection.isAcquired() ) {

                    try {

                        DBConnection connection = this.databaseInitialize.getConnection();
                        actualIterationConnection.setConnection( connection );

                    } catch ( Exception exception ) {

                        new PoolBadConnectionException( "Bad connection provided from DatabaseAccessor.", exception )
                                .printStackTrace();

                    }

                } else {

                    actualIterationConnection.setDeprecated( true );

                }

            }

        }

    }

    private DatabaseConnection searchForNotAcquiredConnection() throws PoolNoConnectionsAvailableException {

        int i = 0;
        int poolSize = this.pool.length;

        while ( true ) {

            if ( i == poolSize ) {

                throw new PoolNoConnectionsAvailableException( "No pool database connections available." );

            } else {

                DatabaseConnection actualIterationConnection;
                actualIterationConnection = this.pool[ i ];

                if ( actualIterationConnection.isAcquired() ) {

                    i++;
                    continue;

                } else {

                    return actualIterationConnection;

                }

            }

        }

    }

    private void increasePoolSize() throws PoolBadConnectionException {

        int maxBlocksAmount = this.maxPoolSize / this.blockSize;
        boolean canIncreasePoolSize = this.amountBlocks < maxBlocksAmount;

        if ( canIncreasePoolSize ) {

            int indexFirstNotInitializedConnection = ( this.blockSize * this.amountBlocks );

            for ( int i = 0; i < this.blockSize; i++ ) {

                try {

                    this.pool[indexFirstNotInitializedConnection] =
                            new DatabaseConnection(indexFirstNotInitializedConnection, this.databaseInitialize.getConnection());

                    indexFirstNotInitializedConnection++;

                } catch ( Exception exception ) {

                    throw new PoolBadConnectionException( "Bad connection provided from DatabaseAccessor.", exception);

                }

            }

            this.amountBlocks++;

        } else {

            new Exception( "Can't increase pool maximum size" ).printStackTrace();

        }

    }

    private void decreasePoolSize() throws Exception {

        int totalConnections = this.blockSize * this.amountBlocks;
        boolean canReducePoolSize =
                ( totalConnections - this.amountAcquiredConnections ) > this.blockSize;

        if ( canReducePoolSize ) {

            DatabaseConnection[] newPool = new DatabaseConnection[ this.maxPoolSize ];
            int firstNotInitializedPoolIndex = 0;

            for ( int i = 0; i < this.maxPoolSize; i++ ) {
                if ( this.pool[ i ] != null ) {

                    DatabaseConnection actualIterationConnection = this.pool[ i ];
                    actualIterationConnection.setId( firstNotInitializedPoolIndex );
                    newPool[ firstNotInitializedPoolIndex ] = actualIterationConnection;
                    firstNotInitializedPoolIndex++;

                }
            }

            if ( ( firstNotInitializedPoolIndex % this.blockSize ) != 0 ) {

                int stopAt = this.blockSize - ( firstNotInitializedPoolIndex % this.blockSize );

                for ( int i = 0; i < stopAt; i++ ) {

                    newPool[ firstNotInitializedPoolIndex ] =
                            new DatabaseConnection( firstNotInitializedPoolIndex, this.databaseInitialize.getConnection() );

                }

            }

            this.pool = newPool;
            this.amountBlocks--;

        } else {

            new Exception( "Cannot reduce poolSize" ).printStackTrace();

        }

    }

    private void initializePool() throws PoolBadConnectionException {

        this.pool = new DatabaseConnection[ this.maxPoolSize ];
        for ( int i = 0; i < this.blockSize; i++ ) {

            try {

                DBConnection connection = this.databaseInitialize.getConnection();
                this.pool[ i ] = new DatabaseConnection( i, connection );

            } catch ( Exception exception ) {

                throw new PoolBadConnectionException( "Bad connection provided from DatabaseAccessor.", exception );

            }

        }

        this.amountBlocks++;

    }

}