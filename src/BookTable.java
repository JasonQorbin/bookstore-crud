import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;

public class BookTable {
    public static final String NAME = "books";

    // Columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_AUTHOR = "Author";
    public static final String COLUMN_QTY = "Qty";

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
     * Updates a record in the books table of the given ID number by changing the
     * title.
     * 
     * @param connection The database {@link Connection} object
     * @param idToChange The ID number of the record to update.
     * @param newTitle   The new book title.
     *
     * @returns {@code true} on success
     */
    public static boolean updateTitle(Connection connection, int idToChange, String newTitle) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(getUpdateQuery(COLUMN_TITLE));
        statement.setString(1, newTitle);
        statement.setInt(2, idToChange);
        return statement.executeUpdate() > 0;
    }

    /**
     * Updates a record in the books table of the given ID number by changing the
     * title.
     * 
     * @param connection The database {@link Connection} object
     * @param idToChange The ID number of the record to update.
     * @param newAuthor  The new book author.
     *
     * @returns {@code true} on success
     */
    public static boolean updateAuthor(Connection connection, int idToChange, String newAuthor) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(getUpdateQuery(COLUMN_AUTHOR));
        statement.setString(1, newAuthor);
        statement.setInt(2, idToChange);
        return statement.executeUpdate() > 0;
    }

    /**
     * Updates a record in the books table of the given ID number by changing the
     * title.
     * 
     * @param connection The database {@link Connection} object
     * @param idToChange The ID number of the record to update.
     * @param newQty     The new quantity.
     *
     * @returns {@code true} on success
     */
    public static boolean updateQty(Connection connection, int idToChange, int newQty) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(getUpdateQuery(COLUMN_QTY));
        statement.setInt(1, newQty);
        statement.setInt(2, idToChange);
        return statement.executeUpdate() > 0;
    }
}
