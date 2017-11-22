# swimming-pool

**swimming-pool** is a library that helps you manage a conection pool for MySQL with Java.

## How to use?

To use the Java Database Conection Pool you need to do a couple of things:
1. Make a `config.json` file with the configuration for the pool.
2. Intanciate a `DatabaseConnectionsPool` object.
3. Now you can get a `DatabaseConnection` from the pool and use it's methods to make queries and manage the database.
*You may have to import modules from `java.sql` like `ResultSet` to manage the data.

In the next example we configure a pool of conections with 5 blocks of 25 connections each:
``` json
{
  "blockSize": 5,
  "maxPoolSize": 25,
  "DB_HOST" : "localhost",
  "DB_PORT" : 3306,
  "DB_DATABASE" : "ugly-duck",
  "DB_USERNAME" : "root",
  "DB_PASSWORD" : ""
}
```
Once we have the configuration file, we can use the Pool methods to manage the connections.

Here we have two options, one querying with the Library methods and Interfaces, and other (more flexible) using `java.sql.*` methods and interfaces.

Example 1 (Quick but less flexible):
```java
String filePath = new File("").getAbsolutePath();
String configurationFilePath = filePath + "/main/src/config.json"; // Or wherever your config file is.

try {
  // First we instanciate the pool
  DatabaseConnectionsPool pool = new DatabaseConnectionsPool(configurationFilePath);
  // Then we thell the pool to give us one connection
  DatabaseConnection dbConnection = pool.acquireConnection();

  // After that we can make querys to the database like this and place the
  // results in a ResultSet object (You may have to import java.sql.ResultSet)
  ResultSet rs = dbConnection.query("SELECT 1+1 as Suma FROM DUAL;");

  pool.releaseConnection(dbConnection);

} catch(Trowable e) {
  e.printStackTrace();
}
```

Example 2 (More steps but super flexible):
```java
String filePath = new File("").getAbsolutePath();
String configurationFilePath = filePath + "/main/src/config.json"; // Or wherever your config file is.

try {
  // First we instanciate the pool
  DatabaseConnectionsPool pool = new DatabaseConnectionsPool(configurationFilePath);
  // Then we thell the pool to give us one connection
  DatabaseConnection dbConnection = pool.acquireConnection();

  // After that we can make querys to the database like this and place the
  // results in a ResultSet object (You may have to import java.sql.*)
  Connection connection = dbConnection.getConnection();
  Statement statement = connection.createStatement();
  ResultSet rs = statement.executeQuery("SELECT 1+1 as Suma FROM DUAL;");

  pool.releaseConnection(dbConnection);

} catch(Trowable e) {
  e.printStackTrace();
}
```

Full Example:
``` java
String filePath = new File("").getAbsolutePath();
String configurationFilePath = filePath + "/main/src/config.json";

try {
  DatabaseConnectionsPool pool = new DatabaseConnectionsPool(configurationFilePath);
  DatabaseConnection dbConnection = pool.acquireConnection();
  
  ResultSet rs = dbConnection.query("SELECT 1+1 as Suma FROM DUAL;");

  // This step is not neccesary, but we demonstrate how to get the metadata
  // of the tables in the database
  ResultSetMetaData rsmd = rs.getMetaData();
  int columnsNumber = rsmd.getColumnCount();
  System.out.println( "Column|Value");

  // With the ResultSet methods we can navigate trough the results
  // Refer to: https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html
  while (rs.next()) {
    for (int i = 1; i <= columnsNumber; i++) {
      if (i > 1) System.out.print(",  ");
      String columnValue = rs.getString(i);
      System.out.print( rsmd.getColumnName(i) + "|" + columnValue);
    }
    System.out.println("");
  }

  // And finally we can release the connection we use from the pool
  pool.releaseConnection(connection);
} catch (Throwable e) {
  e.printStackTrace();
}
```

## Features

1. Provide connections with a Connection Pool.
2. Increase the pool size.
3. Decrease the pool size.
4. Reconnect all the unused connections (Refresh).
5. Configure pool segments, with number of blocks and size of each block.
6. The pool is configurable trough a JSON file.

## Components
<!--
### Component 1
* Description:
* Dependencies:
* Input Interfaces:
* Output Interfaces:
* Artifacts: Archivos que deben utilizarse, así como librerías (ejemplo archivo de configuración, librería de MySQL).
-->

### Pool

* Description: Pool is a storage system for connections that manage that connectios to the database in threads to make them efficient.

* Dependencies:
  1. `PoolConfigurationReader`
  2. `DatabaseAccessor`

* Input Interfaces:
  1. `ReleaseConnection` take a `DatabaseConnection` and stablish it as free and triggers an actualization.
  2. `UpdateConnections` updates the process of actualization of the free and adquired connections and mark it propperly.

* Output Interfaces:
  1. `AcquireConnection` returns a `DatabaseConnection` avaliable.

* Artifacts:
  1. `JSONFileReader`


### Component Diagram

![component-diagram](http://rejonpardenilla.com/arqui/component_diagram.png)

## Classes
<!--
### Class 1
* Description:
* Dependencies: Dependencias con otras clases: <<Listar las asociaciones, nombre y descripción
* Attributes: Enumerarlas y adicionar el nombre, tipo, visibilidad, valor por omisión y descripción.
* Functions: Enumerarlas y adicionar el nombre, listado de argumentos con su tipo, valor de retorno, visibilidad, si es función pública mencionar el servicio que esta implementando (componente e interface de salida) y descripción.
-->

### DatabaseConnection

* Description: Is a connection that accessible for the user. Has elements used by `DatabaseConnectionsPool` and gives the `query()` method to execute database expressions without the need to prepare statements mannually.

* Dependencies: `Connection`

* Attributes:
1. `Id`: Integer. Private. Number that represents the connection id.
2. `Connection`: `Connection`. Private. Connection provided by `DatabaseAccessor`.
3. `Acquired`: Boolean. Private. Default: false. Defines if the connection is adquired or not.
4. `NeedsUpdate`: Boolean. Private. Default: false. Defines if the connection requires an update at the moment of release.

* Functions:
1. `query`: Public. Recieves a string that represents the query to be executed and returns the `ResultSet` from that operation.
2. `getId`: Protected. Getter of the attribute `ID`.
3. `setId`: Protected. Setter of the attribute `ID`.
4. `getConnection`: Protected. Getter of the attribute `Connection`.
5. `setConnection`: Protected. Setter of the attribute `Connection`.
6. `isAcquired`: Protected. Getter of the attribute `Acquired`.
7. `setAcquired`: Protected. Setter of the attribute `Acquired`.
8. `isNeedsUpdate`: Protected. Getter of the attribute `NeedsUpdate`.
9. `setNeedsUpdate`: Protected. Setter of the attribute `NeedsUpdate`.

### DatabaseConnectionsPool

* Description: Generates conections to the database and store it in blocks (the size of the blocks is defined in the config file) and delivers it trough the method `acquireConnection()`. The Pool can realease the connection trough the method `releaseConnection()`.
This class manage the pool size by itself and allways provides a specific number of avaliable connections, growing and reducing dynamically the size and number of the blocks.

* Dependencies: `Config`, `DatabaseConnection`.

* Attributes:
1. `blockSize`: Integer. Private. Stores a number that represents the size of the pool blocks.
2. `maxPoolSize`: Integer. Private. Stores a number that represents the maximum of blocks that a pool can have.
3. `amountBlocks`: Integer. Private. 0 as default. Stores a number that represents the number of blocks that have in that specific moment.
4. `amountAcquiredConnections`: Integer. Private. 0 as default. Stores a number that represents the amount of connections that the pool has already given.
5. `pool`: `DatabaseConnection[]`. Private. Stores the connections of the pool.

* Functions: 
1. `AcquireConnection`: Public. Returns a `DatabaseConnection` avaliable.
2. `ReleaseConnection`: Public. Takes a `DatabaseConnection` and frees it.
3. `UpdateConnections`: Public. Refresh the unused connections.
4. `SearchForNotAcquired`: Private. Returns a `DatabaseConnection` avaliable.
5. `IncreasePoolSize`: Private. Increase the size of the connection pool.
6. `DecreasePoolSize`: Private. Decrease the size of the connection pool.
7. `InitializePool`: Private. Initialize the first pool connections.

### DatabaseInitialize

* Description: Initialize a database with a MySQL driver, with a host, user and postgresql.

* Dependencies: None.

* Attributes:
1. `DB_HOST`: String. Private. Database Host name. 
2. `DB_PORT`: String. Private. Database Port name.
3. `DB_DATABASE`: String. Private. Databse name.
4. `DB_USERNAME`: String. Private. Database username.
5. `DB_PASSWORD`: String. Private. Database password.
6. `ConnectionPool`: `DatabaseConnectionsPool`. Private. Final. Reference to a connection pool.

* Functions:
1. `getConnection`: Public. Returns a database connection with the host, user and password set in the configuration file.
2. `notifyFileChange`: Public. Used to notify changes from the config json file.
3. `isConnectionWorking`: Private. Returns a boolean that represents if the connection obtained is up and running.

### FileChangeListener

* Description: Is a watcher for the `config.json` file to detect if the file is modified in execution time.

* Dependencies: `FileReader`, `Thread`

* Attributes:
1. `DB`: `DatabaseInitialize`. Private. A reference to notify the database initializer that connection host, user or password has changed.

2. `filePath`: String. Private. Represents the path of the `config.json` file.

* Functions:
1. `getFileContent`: Private. Manage the file buffer and returns the content as a String.

2. `run`: Public. Override from `Thread`, detects if the file has change.


### Config

* Description: Reads the configurations in `config.json`.

* Dependencies: `JSONFileReader`.

* Attributes:
1. `ConfigurationFilePath`: De tipo string privado sin valor por defecto. Dirección del archivo de configuración a leer.
2. `JSONFileReader`: De tipo JSONFileReader privado sin valor por defecto. Lector de archivos JSON.
3. `ConfigurationObject`: De tipo JSONObject privado sin valor por defecto. Objeto JAVA-JSON que contiene la configuración.
 
* Functions:
1. `GetPoolSize`: Publico. Retorna entero. Obtiene el tamaño de los blocks del pool del objeto de configuración.
2. `GetMaxPoolSize`: Publico. Retorna entero. Obtiene el tamaño máximo del pool del objeto de configuración.
3. `getDatabasePort`:

### JSONFileReader

* Description: Reads the json files and parses it as Objects.

* Dependencies: JSONParser.

* Attributes: None.

* Functions:
1. `getJSONObject`: Public. Recieves a string of the json url and returns a JSONObject.

### Class Diagram

![class-diagram](http://rejonpardenilla.com/arqui/class_diagram.png) 

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request

## License

**swimming-pool** is under the [MIT License](https://opensource.org/licenses/MIT).

## Authors

* [Juan Pablo Zamora Veraza](https://github.com/jupazave)
* [Daniel Alberto Rejón Pardenilla](https://github.com/rejonpardenilla)
