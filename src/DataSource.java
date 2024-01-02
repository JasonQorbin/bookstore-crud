import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class DataSource {
    
    //Default database parameters
    private static final String DATABASE_NAME = "ebookstore";
    private static final String DATABASE_PROTOCOL = "jdbc";
    private static final String DATABASE_VENDOR = "mysql";
    private static final String DATABASE_HOST = "localhost";
    private static final String DATABASE_PORT = "3306";
    private static final String DATABASE_USER = "Jason"; //TODO: Replace username and password
    private static final String DATABASE_PASSWORD = "KochiraDozo";
    
    private Connection connection;

    // Singleton pattern (instance, inctance-getter & private constructor)
    //-----
    public static final DataSource instance = new DataSource();
    
    public static DataSource getInstance() { return instance; }

    private DataSource() throws SQLException {
        connection = DriverManager.getConnection(
            getConnectionURL(),
            DATABASE_USER, DATABASE_PASSWORD
        );
        initialiseDatabase();
    }
    //------
    
    private String getConnectionURL() {
        StringBuilder connectionURL = new StringBuilder();
        connectionURL.append(DATABASE_PROTOCOL).append(':')
            .append(DATABASE_VENDOR).append("://")
            .append(DATABASE_HOST).append(':')
            .append(DATABASE_PORT).append('/')
            .append(DATABASE_NAME).append("?useSSL=false");
        return connectionURL.toString();
    }
    
    private boolean databaseExists() {
        Statement statement = connection.createStatement();
        StringBuilder query = new StringBuilder();
        query.append("select schema_name from information_schema.schemata where schema_name = '")
            .append(DATABASE_NAME).append("';");
        ResultSet result = statement.executeQuery(query.toString());
        boolean answer = result.next();
        statement.close();
        return answer;
    }

    private void createDatabase() {
        Statement statement = connection.createStatement();
        StringBuilder query = new StringBuilder();
        query.append("CREATE DATABASE ")
            .append(DATABASE_NAME).append(";");
        statement.executeUpdate(query.toString());
        statement.close();
    }

    public void initialiseDatabase() {
        if (!databaseExists()) {
            createDatabase();   
        }

        setDatabaseAsDefault();

        if (!bookTableExists()) {
            initialiseBookTable();
        }
    }

    private void setDatabaseAsDefault() {
        Statement statement = connection.createStatement();
        StringBuilder query = new StringBuilder();
        query.append("USE ").append(DATABASE_NAME).append(';');
        statement.executeUpdate(query.toString());
        statement.close();
    }

    //Book table paramaters
    //======================
    private static final String BOOK_TABLE_NAME = "books";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_AUTHOR = "Author";
    public static final String COLUMN_QTY = "Qty";

    /**
     * Executes a query using the given Statement object to cleanly (re)create the table in the database.
     * Can safely be call even if the table already exists but existing data will be destroyed.
     *
     * This method assumes that the correct default database has been set with "USE database_name;".
     * No prepared statements are used here because there is no user input to sanitise and this is a query
     * that should run very infrequently.
     *
     */
    private void createBookTable() throws SQLException {
        Statement statement = connection.createStatement();
        StringBuilder queryBuilder = new StringBuilder();

        //Remove the table first if it exists to prevent a SQL error if it's already there.
        queryBuilder.append("DROP TABLE IF EXISTS ").append(BOOK_TABLE_NAME).append(";");
        statement.executeUpdate(queryBuilder.toString());
        queryBuilder.setLength(0);

        queryBuilder.append("CREATE TABLE ").append(BOOK_TABLE_NAME).append("(\n")
                    .append("\t").append(COLUMN_ID)    .append(" INT NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY,\n")
                    .append("\t").append(COLUMN_TITLE) .append(" VARCHAR(80) NOT NULL,\n")
                    .append("\t").append(COLUMN_AUTHOR).append(" VARCHAR(40) NOT NULL,\n")
                    .append("\t").append(COLUMN_QTY)   .append(" INT DEFAULT 0\n")
                    .append(");");
        statement.addBatch(queryBuilder.toString());
        queryBuilder.setLength(0);

        //The task stipulates that the ID numbers should start from 3001;
        queryBuilder.append("ALTER TABLE ").append(BOOK_TABLE_NAME).append(" AUTO_INCREMENT = 3001;");
        statement.addBatch(queryBuilder.toString());

        statement.executeBatch();
        statement.close();
    }

    /**
     * Convenience method to be used after the database is created initially.
     * Creates the
     * books table and inserts the initial data.
     *
     * @returns {@code true} on success.
     */
    public boolean initialiseBookTable() throws SQLException {
        createBookTable();
        return insertInitialData();
    }
    
    /**
     * Executes a query using the given Statement object to add a new book to the
     * database with the given
     * parameters
     *
     * This method assumes that the correct default database has been set with "USE
     * database_name;" and that the book
     * table already exists. This method uses prepared statements to handle
     * sanitising of the user input to prevent
     * SQL injection attacks.
     *
     * The return value is the ID of the new record, which is an auto incrementing
     * column so the new value is found
     * as the max of the ID column.
     *
     * @param connection The database connection object.
     * @param title      The title of the new book. (Does not need to be unique in the database)
     * @param author     The author of the book
     * @param qty        The initial quantity of the book in stock.
     *
     * @returns The ID of the new record or -1 on failure
     *
     */
    public int insertBook(String title, String author, int qty) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ").append(BOOK_TABLE_NAME).append(" (")
                .append(COLUMN_TITLE).append(", ")
                .append(COLUMN_AUTHOR).append(", ")
                .append(COLUMN_QTY).append(") VALUES (?, ?, ?);");
        PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());
        statement.setString(1, title);
        statement.setString(1, author);
        statement.setInt(1, qty);
        int rowsAffected = statement.executeUpdate();
        int newID = -1;
        if (rowsAffected > 0) {
            ResultSet keys = statement.getGeneratedKeys();
            keys.next();
            newID = keys.getInt(1);
            keys.close();
        }
        statement.close();
        return newID;
    }

    /**
     * Inserts a new record into the books table using the data provided.
     *
     * @param newBook    A {@link Book} object that will be inserted.
     *
     * @returns {@code true on success}
     */
    public int insertBook( Book newBook) throws SQLException {
        return insertBook(newBook.title, newBook.author, newBook.qty);
    }

    /**
     * Checks to see if the Books table exists. This method assumes the database
     * already exists.
     * A SQL Exception will be thrown if the database doesn't exist yet.
     *
     * @returns {@code true} if the database table exists.
     */
    public boolean bookTableExists() throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM information_schema.tables WHERE table_schema = '")
                .append(DATABASE_NAME).append("' AND table_name = '")
                .append(BOOK_TABLE_NAME).append("';");
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(queryBuilder.toString());
        boolean success = result.next();
        statement.close();
        return success;
    }

    /**
     * Deletes a record in the books table of the given ID number.
     * 
     * @param idToDelete The ID number of the record to delete.
     *
     * @returns {@code true} on success
     */
    public boolean deleteBook(int idToDelete) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DELETE FROM ").append(BOOK_TABLE_NAME)
                .append(" WHERE ").append(COLUMN_ID).append(" = ")
                .append(idToDelete).append(';');
        Statement statement = connection.createStatement();
        boolean success = statement.executeUpdate(queryBuilder.toString()) > 0;
        statement.close();
        return success;
    }

    public boolean deleteBook(Book bookToDelete) throws SQLException {
        return deleteBook(bookToDelete.id);
    }

    //Initital Data
    private final String STARTING_TITLES [] = {
        "A Tale of Two Cities",
        "Harry Potter and the Philosophers Stone",
        "The Lion, the With & the Wardrobe",
        "The Lord of the Rings",
        "Alice in Wonderland",
        "Queen of Shadows",
        "Iron Flame",
        "Throne of Glass",
        "Heir of Fire",
        "The Lion: Son of the Forest",
        "Courage and Honour",
        "The Queen of Nothing",
        "The Wicked King",
        "Everless",
        "Evermore",
        "The Cruel Prince",
        "Empire of Storms",
        "Harry Potter and the Prisoner of Azkaban",
        "Harry Potter and the Chamber of Secrets",
        "Harry Potter and the Goblet of Fire"
    };

    private final String STARTING_AUTHORS [] = {
        "Charles Dickens",
        "J.K. Rowling",
        "C.S. Lewis",
        "J.R.R Tolkien",
        "Lewis Carrol",
        "Sarah J Maas",
        "Rebecca Yarros",
        "Sarah J Maas",
        "Sarah J Maas",
        "Mike Brooks",
        "Graham McNeill",
        "Holly Black",
        "Holly Black",
        "Sara Holland",
        "Sara Holland",
        "Holly Black",
        "Sarah J Maas",
        "J.K. Rowling",
        "J.K. Rowling",
        "J.K. Rowling"
    };

    private final int STARTING_QTY [] = {
        30,
        40,
        25,
        37,
        12,
        154,
        122,
        87,
        164,
        11,
        11,
        39,
        29,
        56,
        57,
        30,
        734,
        41,
        61,
        91
    };

    /**
     * Inserts the initial data into the books table.
     *
     * @returns {@code true} on success.
     */
    public boolean insertInitialData() throws SQLException {
        boolean result = true;

        for (int index = 0; index < STARTING_TITLES.length; ++index) {
            boolean success = insertBook (
                STARTING_TITLES[index],
                STARTING_AUTHORS[index],
                STARTING_QTY[index]
            ) > 0;
            result = result && success;
        }
        return result;
    }

    private Book getBookFromResultSet (ResultSet resultSet) {
        Book answer = new Book();
        answer.id = resultSet.getInt(COLUMN_ID);
        answer.title = resultSet.getString(COLUMN_TITLE);
        answer.author = resultSet.getString(COLUMN_AUTHOR);
        answer.qty = resultSet.getInt(COLUMN_QTY);
        return answer;
    }

    public List<Book> searchBooks(CliHandler.SearchCriteria criteria, String searchTerm) throws SQLException {
        StringBuilder queryPrefix = new StringBuilder();
        queryPrefix.append("SELECT ")
            .append(COLUMN_ID).append(", ")
            .append(COLUMN_TITLE).append(", ")
            .append(COLUMN_AUTHOR).append(", ")
            .append(COLUMN_QTY).append(" FROM ").append(BOOK_TABLE_NAME)
            .append(" WHERE ");
        switch (criteria) {
            case ByTitle:
                queryPrefix.append(COLUMN_TITLE);
                break;
            case ByAuthor:
                queryPrefix.append(COLUMN_AUTHOR);
                break;
            default:
                throw new AssertionError("Invalid search criteria in searchBooks method");
        }

        StringBuilder query = new StringBuilder(); 
        Statement statement = connection.createStatement();
        
        //Exact search
        query.append(queryPrefix)
            .append(" = '").append(searchTerm).append('\'');
        statement.addBatch(query.toString());
        query.setLength(0);

        //Search term as a prefix/suffix
        query.append(queryPrefix)
            .append(" LIKE '%_").append(searchTerm).append("_%\'");
        statement.addBatch(query.toString());
        
        statement.executeBatch();

        ArrayList<Book> answer = new ArrayList<>();
        
        boolean haveResultSet = statement.getMoreResults();
        while (haveResultSet) {
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                Book newBook = getBookFromResultSet(resultSet);
                answer.add(newBook);
            }
        }
        
        statement.close();

        return answer;
    }

    public void close() {
        connection.close();
    }

}
