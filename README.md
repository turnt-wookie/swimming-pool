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

* Description:
* Dependencies:
* Attributes:
* Functions:

### DatabaseConnectionsPool

* Description: Generates conections to the database and store it in blocks (the size of the blocks is defined in the config file) and delivers it trough the method `acquireConnection()`. The Pool can realease the connection trough the method `releaseConnection()`.
This class manage the pool size by itself and allways provides a specific number of avaliable connections, growing and reducing dynamically the size and number of the blocks.

* Dependencies: `Config`, `DatabaseConnection`.

* Attributes:
1. `blockSize`: De tipo entero con alcance privado sin valor por defecto. Almacena un número que representa el tamaño que deben tener los bloques del pool.
2. `maxPoolSize`: De tipo entero con alcance privado sin valor por defecto. Almacena un número que representa el tamaño máximo que puede tener el pool.
3. `amountBlocks`: De tipo entero con alcance privado cuyo valor por defecto es cero. Almacena un número que representa la cantidad de bloques con las que cuenta el pool en un momento específico.
4. `amountAcquiredConnections`: De tipo entero con alcance privado cuyo valor por defecto es cero. Almacena un número que representa la cantidad de conexiones que el pool ha entregado.
5. `pool`: De tipo `DatabaseConnection[]` con alcance privado cuyo valor es el array vacío. Almacena las conexiones del pool.

* Functions: 
1. `AcquireConnection`: Retorna una DatabaseConnection disponible. Visibilidad pública.
2. `ReleaseConnection`: Toma una DatabaseConnection y la libera. No tiene valor de retorno.
Visibilidad pública.
3. `UpdateConnections`: Sin valor de retorno. Visibilidad pública. Actualiza las conexiones no
utilizada y marca las utilizadas para actualizarse al momento de liberarse.
4. `SearchForNotAcquired`: Retorna una DatabaseConnection disponible. Visibilidad privada.
5. `IncreasePoolSize`: Sin valor de retorno. Visibilidad privada. Aumenta el tamaño de conexiones
activas del pool.
6. `DecreasePoolSize`: Sin valor de retorno. Visibilidad privada. Reduce el tamaño de conexiones activas del pool.
7. `InitializePool`: Sin valor de retorno. Visibilidad privada. Inicializa las primeras conexiones de pool.

### DatabaseInitialize

* Description: Es una conexion adquirible por el usuario. Contiene elementos de manejo empleados por el `DatabaseConnectionsPool` y proporciona el método query para ejecución en base de datos.

* Dependencies: `Connection`

* Attributes:
1. `ID`: De tipo entero privado sin valor por defecto. Número de identificación de la conexión.
2. `Connection`: De tipo `Connection` privado sin valor por defecto. Conexión proporcionada por
el `DatabaseAccessor`.
3. `Acquired`: De tipo boleano privado cuyo valor por defecto es falso. Define si la conexión cuenta
con un propietario o no.
4. `NeedsUpdate`: De tipo boleano privado cuyo valor por defecto es falso. Define si la conexión requiere de un update al ser liberado.

* Functions:
1. `query`: Publica. Toma como argumento un string que representa la query a ser ejecutada y retorna el ResultSet que es resultado de dicha ejecución.
2. `getId`: De paquete. Getter del atributo `ID`.
3. `setId`: De paquete. Setter del atributo `ID`.
4. `getConnection`: De paquete. Getter del atributo `Connection`.
5. `setConnection`: De paquete. Setter del atributo `Connection`.
6. `isAcquired`: De paquete. Getter del atributo `Acquired`.
7. `setAcquired`: De paquete. Setter del atributo `Acquired`.
8. `isNeedsUpdate`: De paquete. Getter del atributo `NeedsUpdate`.
9. `setNeedsUpdate`: De paquete. Setter del atributo `NeedsUpdate`.

### FileChangeListener

* Description:
* Dependencies:
* Attributes:
* Functions:

## Config

* Description: Reads the configurations in `config.json`.

* Dependencies: `JSONFileReader`.

* Attributes:
1. `ConfigurationFilePath`: De tipo string privado sin valor por defecto. Dirección del archivo de configuración a leer.
2. `JSONFileReader`: De tipo JSONFileReader privado sin valor por defecto. Lector de archivos JSON.
3. `ConfigurationObject`: De tipo JSONObject privado sin valor por defecto. Objeto JAVA-JSON que contiene la configuración.
 
* Functions:
1. `GetPoolSize`: Publico. Retorna entero. Obtiene el tamaño de los blocks del pool del objeto de configuración.
2. `GetMaxPoolSize`: Publico. Retorna entero. Obtiene el tamaño máximo del pool del objeto de configuración.

## JSONFileReader

* Description:
* Dependencies:
* Attributes:
* Functions:

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
