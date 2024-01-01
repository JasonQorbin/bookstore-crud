import java.sql.*;

import sun.awt.www.content.audio.wav;
             
public class BookstoreProgram {
    //Default database parameters
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
        CliHandler consoleHandler = new CliHandler();
        try {
            DataSource dataSource = new DataSource();
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
                        int newID = BookTable.insertBook(connection,newBook);
                        newBook.id = newID;
                        currentSelection = newBook;
                        programState = ProgramState.MAIN_MENU;
                        break;
                    case DELETE_MENU:
                        if (consoleHandler.confirmDeletion(currentSelection)) {
                            dataSource.deleteBook(currentSelection);
                            currentSelection = null;
                        }
                        programState = ProgramState.MAIN_MENU;
                        break;
                    case SEARCHING_MENU:
                        consoleHandler.searchDialog();
                        
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
            case 0:
                yield ProgramState.EXIT;
            default:
                yield ProgramState.ERROR;
        };
    }

}
