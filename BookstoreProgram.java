import java.sql.*;

import sun.awt.www.content.audio.wav;

public class BookstoreProgram {
    //Database parameters
    private static final String DATABASE_NAME = "ebookstore";
    private static final String DATABASE_PROTOCOL = "jdbc";
    private static final String DATABASE_VENDOR = "mysql";
    private static final String DATABASE_HOST = "localhost";
    private static final String DATABASE_PORT = "3306";
    private static final String DATABASE_USER = "AppUser";
    private static final String DATABASE_PASSWORD = "AIPASS";
    
    public static void main(String[] args) {
        StringBuilder connectionURL = new StringBuilder();
        connectionURL.append(DATABASE_PROTOCOL).append(':')
                     .append(DATABASE_VENDOR).append("://")
                     .append(DATABASE_HOST).append(':')
                     .append(DATABASE_PORT).append('/')
                     .append(DATABASE_NAME).append("?useSSL=false");
        try {
            Connection connection = DriverManager.getConnection(
                    connectionURL.toString(),
                    DATABASE_USER, DATABASE_PASSWORD //TODO: Create a system user.
            );
            CliHandler consoleHandler = new CliHandler();
            ProgramState programState = MAIN_MENU;
            Book currentSelection = null;

            consoleHandler.printTitle();
            while (programState != EXIT) {
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
                        Book newBook = consoleHandler.getBookInfo();
                        BookTable.insertBook(connection,newBook);
                        currentSelection = newBook;
                        programState = MAIN_MENU;
                        break;
                    case DELETE;

                        
                        
                }
            }
        } catch (SQLException ex) {
            System.out.println("Database error encountered: " + ex.getMessage());
        }
    }

    private static ProgramState newStateFromMainMenu(int menuSelection){
        return switch(menuSelection) {
            case 1:
                yield ADDING_BOOK;
            case 2:
                yield UPDATE_OPTIONS;
            case 3:
                yield DELETE_MENU;
            case 4:
                yield SEARCHING_MENU;
            default:
                yield ERROR;
        }
    }

}
