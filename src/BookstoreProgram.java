import java.sql.*;

public class BookstoreProgram {
    /**
     * Represents all the possible program states.
     */
    private enum ProgramState {
        ERROR,
        MAIN_MENU,
        ADDING_BOOK,
        SEARCHING_MENU,
        UPDATE_MENU,
        DELETE_MENU,
        EXIT
    }

    public static void main(String[] args) {
        //Initialise resources
        CliHandler consoleHandler = new CliHandler();
        DataSource dataSource;
        try {
            dataSource = DataSource.getInstance();
        } catch (SQLException ex) {
            System.out.println("Fatal error: Could not establish database connection.\n" + ex.getMessage());
            return;
        }
            //The program is modelled as a state machine. Each state determines the current behaviour of the program.
            //The prgram loops continuously until the EXIT state is reached.
            ProgramState programState = ProgramState.MAIN_MENU;
            Book currentSelection = null;

            consoleHandler.printTitle();
            while (programState != ProgramState.EXIT) {
                switch (programState) {
                    case MAIN_MENU:
                        int menuSelection = -1;
                        menuSelection = consoleHandler.printMainMenu(currentSelection);
                        programState = newStateFromMainMenu(menuSelection);
                        break;
                    case ADDING_BOOK:
                        try {
                            currentSelection = consoleHandler.addBook();
                        } catch (DatabaseException ex) {
                            System.out.println(ex.getMessage());
                            System.out.println(ex.getCause().getMessage());
                        }
                        programState = ProgramState.MAIN_MENU;
                        break;
                    case DELETE_MENU:
                        try {
                            consoleHandler.deleteBook(currentSelection);
                        } catch (DatabaseException ex) {
                            System.out.println(ex.getMessage());
                            System.out.println(ex.getCause().getMessage());
                        }
                        currentSelection = null;
                        programState = ProgramState.MAIN_MENU;
                        break;
                    case SEARCHING_MENU:
                        try {
                            currentSelection = consoleHandler.searchDialog();
                        } catch (DatabaseException ex) {
                            System.out.println(ex.getMessage());
                            System.out.println(ex.getCause().getMessage());
                        }
                        programState = ProgramState.MAIN_MENU;
                        break;
                    case UPDATE_MENU:
                        try {
                            consoleHandler.updateMenu(currentSelection);
                        } catch (DatabaseException ex) {
                            System.out.println(ex.getMessage());
                            System.out.println(ex.getCause().getMessage());
                        }
                        programState = ProgramState.MAIN_MENU;
                        break;
                    default:
                        //The only state that should map to this branch is "ERROR".
                        System.out.println("Fatal error: The program is in an incorrect state. Attempting to close...");
                        programState = ProgramState.EXIT;
                        break;
                }
            }
        //Exiting program. Cleanup any open resources.
        try{
            dataSource.close();
        } catch (SQLException ex) {
            System.out.println("Error encountered while closing database connection.\n" + ex.getMessage());
        }
        consoleHandler.close();
    }

    /**
     * Processes the input received from the user on the main menu and return the new state of the program implied
     * from that.
     *
     * @param menuSelection The menu selection from the user.
     * @return The new program state value.
     */
    private static ProgramState newStateFromMainMenu(int menuSelection){
        return switch(menuSelection) {
            case 1:
                yield ProgramState.ADDING_BOOK;
            case 2:
                yield ProgramState.SEARCHING_MENU;
            case 3:
                yield ProgramState.UPDATE_MENU;
            case 4:
                yield ProgramState.DELETE_MENU;
            case 0:
                yield ProgramState.EXIT;
            default:
                yield ProgramState.ERROR;
        };
    }

}
