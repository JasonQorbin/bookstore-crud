import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;

public class BookTable {
    public static final String NAME = "books";

    //Columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_AUTHOR = "Author";
    public static final String COLUMN_QTY = "Qty";
    

    //Initital Data
    public static final String STARTING_TITLES [] = {
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

    public static final String STARTING_AUTHORS [] = {
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

    public static final int STARTING_QTY [] = {
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
     * Executes a query using the given Statement object to cleanly (re)create the table in the database.
     * Can safely be call even if the table already exists but existing data will be destroyed.
     *
     * This method assumes that the correct default database has been set with "USE database_name;".
     * No prepared statements are used here because there is no user input to sanitise and this is a query
     * that should run very infrequently.
     *
     * @param connection A Connection object from the database connection.
     */
    public static void createTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        StringBuilder queryBuilder = new StringBuilder();

        //Remove the table first if it exists to prevent a SQL error if it's already there.
        queryBuilder.append("DROP TABLE IF EXISTS ").append(NAME).append(";");
        statement.executeUpdate(queryBuilder.toString());
        queryBuilder.setLength(0);

        queryBuilder.append("CREATE TABLE ").append(NAME).append("(\n")
                    .append("\t").append(COLUMN_ID)    .append(" INT NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY,\n")
                    .append("\t").append(COLUMN_TITLE) .append(" VARCHAR(80) NOT NULL,\n")
                    .append("\t").append(COLUMN_AUTHOR).append(" VARCHAR(40) NOT NULL,\n")
                    .append("\t").append(COLUMN_QTY)   .append(" INT DEFAULT 0\n")
                    .append(");");
        statement.executeUpdate(queryBuilder.toString());
        queryBuilder.setLength(0);

        //The task stipulates that the ID numbers should start from 3001;
        queryBuilder.append("ALTER TABLE ").append(NAME).append(" AUTO_INCREMENT = 3001;");
        statement.executeUpdate(queryBuilder.toString());
        queryBuilder.setLength(0);
    }

    /**
     * Executes a query using the given Statement object to add a new book to the database with the given
     * parameters
     *
     * This method assumes that the correct default database has been set with "USE database_name;" and that the book
     * table already exists. This method uses prepared statements to handle sanitising of the user input to prevent
     * SQL injection attacks.
     *
     * The return value is the ID of the new record, which is an auto incrementing column so the new value is found
     *  as the max of the ID column.
     *
     * @param connection The database connection object.
     * @param title The title of the new book. (Does not need to be unique in the database)
     * @param author The author of the book
     * @param qty The  initial quantity of the book in stock.
     *
     * @returns The ID of the new record or -1 on failure
     *
     */
    public static int insertBook(Connection connection, String title, String author, int qty) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ").append(NAME).append(" (")
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
            newID = getMaxID(connection);
        }
        return newID;
    }
    
    /**
     * Inserts a new record into the books table using the data provided.
     *
     * @param connection The database {@link Connection} object
     * @param newBook A {@link Book} object that will be inserted.
     *
     * @returns {@code true on success}
     */
    public static int insertBook(Connection connection, Book newBook) throws SQLException{
        return insertBook(connection, newBook.title, newBook.author, newBook.qty);
    }

    public static int getMaxID(Connection connection) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT MAX(").append(COLUMN_ID).append(") FROM ").append(NAME).append(';');
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(queryBuilder.toString());
        if (result.next()) {
            return result.getInt(1);
        } else {
            return -1;
        }
    }
    
    /**
     * Inserts the initial data into the books table.
     *
     * @param connection The database {@link Connection} object.
     * @returns {@code true} on success.
     */
    public static boolean insertInitialData(Connection connection) throws SQLException {
        boolean result = true;

        for (int index = 0; index < STARTING_TITLES.length; ++index) {
            boolean success = insertBook (
                connection, STARTING_TITLES[index],
                STARTING_AUTHORS[index],
                STARTING_QTY[index]
            ) > 0;
            result = result && success;
        }
        return result;
    }
    
    /**
     * Convenience method to be used after the database is created initially. Creates the 
     * books table and inserts the initial data.
     *
     * @returns {@code true} on success.
     */
    public static boolean initialiseBookTable(Connection connection) throws SQLException {
        createTable(connection);
        return insertInitialData(connection);
    }
    
    /**
     * Checks to see if the Books table exists. This method assumes the database already exists.
     * A SQL Exception will be thrown if the database doesn't exist yet.
     *
     * @param connection The database {@link Connection} object
     * @returns {@code true} if the database table exists.
     */
    public static boolean tableExists(Connection connection) throws SQLException{
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM information_schema.tables WHERE table_schema = '")
            .append(DataSource.getDatabaseName()).append("' AND table_name = '")
            .append(NAME).append("';");
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(queryBuilder.toString());

        return result.next();
    }

    /**
     * Deletes a record in the books table of the given ID number.
     * 
     * @param connection The database {@link Connection} object
     * @param idToDelete The ID number of the record to delete.
     *
     * @returns {@code true} on success
     */
    public static boolean deleteBook(Connection connection, int idToDelete) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DELETE FROM ").append(NAME)
            .append(" WHERE ").append(COLUMN_ID).append(" = ")
            .append(idToDelete).append(';');
        Statement statement = connection.createStatement();
        return statement.executeUpdate(queryBuilder.toString()) > 0;
    }
    
    /**
     * Helper method to create a template for UPDATE query Statements.
     *
     * @param column The name of the column being updated
     * @returns A parameterized update statement.
     */
    private static String getUpdateQuery(String column) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE ").append(NAME).append(" SET ").append(column)
                    .append(" = ? WHERE ").append(COLUMN_ID).append(" = ?;");
        return queryBuilder.toString();
    }
    
    /**
     * Updates a record in the books table of the given ID number by changing the title.
     * 
     * @param connection The database {@link Connection} object
     * @param idToChange The ID number of the record to update.
     * @param newTitle   The new book title.
     *
     * @returns {@code true} on success
     */
    public static boolean updateTitle(Connection connection, int idToChange,String newTitle) throws SQLException{
        PreparedStatement statement = connection.prepareStatement(getUpdateQuery(COLUMN_TITLE));
        statement.setString(1, newTitle);        
        statement.setInt(2, idToChange);
        return statement.executeUpdate() >0;
    }

    /**
     * Updates a record in the books table of the given ID number by changing the title.
     * 
     * @param connection The database {@link Connection} object
     * @param idToChange The ID number of the record to update.
     * @param newAuthor   The new book author.
     *
     * @returns {@code true} on success
     */
    public static boolean updateAuthor(Connection connection, int idToChange,String newAuthor) throws SQLException{
        PreparedStatement statement = connection.prepareStatement(getUpdateQuery(COLUMN_AUTHOR));
        statement.setString(1, newAuthor);        
        statement.setInt(2, idToChange);
        return statement.executeUpdate() >0;
    }

    /**
     * Updates a record in the books table of the given ID number by changing the title.
     * 
     * @param connection The database {@link Connection} object
     * @param idToChange The ID number of the record to update.
     * @param newQty   The new quantity.
     *
     * @returns {@code true} on success
     */
    public static boolean updateQty(Connection connection, int idToChange, int newQty) throws SQLException{
        PreparedStatement statement = connection.prepareStatement(getUpdateQuery(COLUMN_QTY));
        statement.setInt(1, newQty);        
        statement.setInt(2, idToChange);
        return statement.executeUpdate() >0;
    }
}
