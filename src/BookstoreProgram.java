import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.logging.ConsoleHandler;

import org.json.*;

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

    private static DatabaseCredentials credentials;

    public static void main(String[] args) {
        //Initialise resources
        CliHandler consoleHandler = new CliHandler();
        handleArgs(args, consoleHandler);
        DataSource dataSource;
        try {
            dataSource = DataSource.getInstance(credentials);
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
     * Parse commandline arguments. This primarily deals with deciding where to fetch the database connection info
     * from (file/console/default).
     *
     * @param args The commandline arguments from the main method.
     */
    private static void handleArgs(String[] args, CliHandler consoleHandler) {
        if (args.length == 0) {
            System.out.println("Using default database credentials. See the README file for details.");
            System.out.println("Usage (credentials from file): .\\run.bat -i database.ini");
            System.out.println("Usage (enter credentials manually): .\\run.bat -c");
            credentials = new DatabaseCredentials("localhost",
                    "3306",  "Librarian","Applecart", "ebookstore");
        }
        for (int i = 0; i < args.length; ++i)  {
            switch (args[i]) {
                case "-i":
                    credentials = readIniFile(args[++i]);
                    return;
                case "-c":
                    credentials =  getCredentialsFromUser(consoleHandler);
                    return;
            }
        }
    }

    private static DatabaseCredentials readIniFile(String filePath) {
        Path inputFile = Paths.get(filePath);
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
            String stringBuffer = reader.readLine();
            while (stringBuffer != null){
                builder.append(stringBuffer);
                stringBuffer = reader.readLine();
            }
            reader.close();
        } catch (IOException ex) {
            System.out.println("Could not read file " + inputFile);
            return null;
        }
        JSONObject json = new JSONObject(builder.toString());
        String protocol = json.getString("protocol");
        String vendor = json.getString("vendor");
        String host = json.getString("host");
        String port = json.getString("port");
        String user = json.getString("user");
        String password = json.getString("password");
        String database = json.getString("database");

        return new DatabaseCredentials(host, port, user, password, database);
    }

    public static DatabaseCredentials getCredentialsFromUser(CliHandler handler) {
        String host = handler.getStringFromUser("Server host name/ip [default = localhost]: ");
        if (host.isBlank()) {
            host = "localhost";
        }

        String port = handler.getStringFromUser("Server port [default = 3306]: ");
        if (port.isBlank()){
            port = "3306";
        }

        String user = handler.getStringFromUser("Database User: ");
        String password = handler.getStringFromUser("Database Password: ");
        String database = handler.getStringFromUser("Database name [default = ebookstore]: ");
        if (database.isBlank()){
            database = "ebookstore";
        }

        return new DatabaseCredentials(host, port, user, password, database);
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
