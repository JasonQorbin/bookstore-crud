import java.sql.*;


class DataSource {
    
    //Default database parameters
    private static final String DATABASE_NAME = "ebookstore";
    private static final String DATABASE_PROTOCOL = "jdbc";
    private static final String DATABASE_VENDOR = "mysql";
    private static final String DATABASE_HOST = "localhost";
    private static final String DATABASE_PORT = "3306";
    private static final String DATABASE_USER = "Jason";
    private static final String DATABASE_PASSWORD = "KochiraDozo";
    
    private Connection connection;
    
    public static String getDatabaseName() {
        return DATABASE_NAME;
    }

    public Connection getConnection() {
        return connection;
    }

    private String getConnectionURL() {
        StringBuilder connectionURL = new StringBuilder();
        connectionURL.append(DATABASE_PROTOCOL).append(':')
            .append(DATABASE_VENDOR).append("://")
            .append(DATABASE_HOST).append(':')
            .append(DATABASE_PORT).append('/')
            .append(DATABASE_NAME).append("?useSSL=false");
        return connectionURL.toString();
    }

    public DataSource() throws SQLException {
        connection = DriverManager.getConnection(
            getConnectionURL(),
            DATABASE_USER, DATABASE_PASSWORD //TODO: Create a system user.
        );
        initialiseDatabase();
    }

    private boolean databaseExists() {
        Statement statement = connection.createStatement;
        StringBuilder query = new StringBuilder();
        query.append("select schema_name from information_schema.schemata where schema_name = '")
            .append(DATABASE_NAME).append("';");
        ResultSet result = statement.executeQuery(query.toString());

        return result.next();
    }

    private void createDatabase() {
        Statement statement = connection.createStatement;
        StringBuilder query = new StringBuilder();
        query.append("CREATE DATABASE ")
            .append(DATABASE_NAME).append(";");
        statement.executeUpdate(query.toString());
    }

    public void initialiseDatabase() {
        if (!databaseExists()) {
            createDatabase();   
            setDatabaseAsDefault();
            BookTable.initialiseBookTable(connection);
        } else {
            setDatabaseAsDefault();
            if (!BookTable.tableExists(connection)) {
                BookTable.initialiseBookTable(connection);
            }
        }
    }

    public void deleteBook (Book book) throws SQLException {
        BookTable.deleteBook(connection, book.id);
    }
}
