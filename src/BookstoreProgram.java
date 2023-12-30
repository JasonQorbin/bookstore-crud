import java.sql.*;
             
public class BookstoreProgram {
    //Database parameters
    private static final String DATABASE_NAME = "ebookstore";
    private static final String DATABASE_PROTOCOL = "jdbc";
    private static final String DATABASE_VENDOR = "mysql";
    private static final String DATABASE_HOST = "localhost";
    private static final String DATABASE_PORT = "3306";
    private static final String DATABASE_USER = "Jason";
    private static final String DATABASE_PASSWORD = "KochiraDozo";
    
    private enum ProgramState {
        ERROR,
        MAIN_MENU,
        ADDING_BOOK,
        SEARCHING_MENU,
        SEARCH_RESULTS,
        UPDATE_OPTIONS,
        DELETE_MENU,
        EXIT
    }

    public static void main(String[] args) {
        StringBuilder connectionURL = new StringBuilder();
        connectionURL.append(DATABASE_PROTOCOL).append(':')
                     .append(DATABASE_VENDOR).append("://")
                     .append(DATABASE_HOST).append(':')
                     .append(DATABASE_PORT).append('/')
                     .append(DATABASE_NAME).append("?useSSL=false");
        CliHandler consoleHandler = new CliHandler();
        try {
            Connection connection = DriverManager.getConnection(
                    connectionURL.toString(),
                    DATABASE_USER, DATABASE_PASSWORD //TODO: Create a system user.
            );
            ProgramState programState = ProgramState.MAIN_MENU;
            Book currentSelection = null;

            consoleHandler.printTitle();
            while (programState != ProgramState.EXIT) {
                switch (programState) {
                    case MAIN_MENU:
                        int menuSelection = -1;
                        if (currentSelection == null) {
                            menuSelection = consoleHandler.printMainMenu();
                        } else {
                            menuSelection = consoleHandler.printMainMenu(currentSelection);
                        }
                        programState = newStateFromMainMenu(menuSelection);
                        break;
                    case ADDING_BOOK:
                        Book newBook = consoleHandler.getBookInfoFromUser();
                        BookTable.insertBook(connection,newBook);
                        currentSelection = newBook;
                        programState = ProgramState.MAIN_MENU;
                        break;
                    case DELETE_MENU:
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Database error encountered: " + ex.getMessage());
        }

        consoleHandler.cleanup();
    }

    private static ProgramState newStateFromMainMenu(int menuSelection){
        return switch(menuSelection) {
            case 1:
                yield ProgramState.ADDING_BOOK;
            case 2:
                yield ProgramState.UPDATE_OPTIONS;
            case 3:
                yield ProgramState.DELETE_MENU;
            case 4:
                yield ProgramState.SEARCHING_MENU;
            default:
                yield ProgramState.ERROR;
        };
    }

}
