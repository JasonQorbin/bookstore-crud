import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.io.IOException;

class CliHandler {
    Scanner consoleReader;

    public CliHandler() {
        consoleReader = new Scanner(System.in);
    }
    
    public void printTitle() {
        System.out.println(
            """
                Bookstore Stock Register
                ========================
            """
        );
        System.out.println();
    }
    
    public int printMainMenu(Book selectedBook) {
        System.out.println(
            """
            1. Add a new book
            2. Search/select books
            3. Update selected book
            4. Delete selected book
            0. Exit
            """
        );
        System.out.println();
        System.out.println("Selected book: "
            + selectedBook.title + " by " + selectedBook.author + " (" + selectedBook.qty + ")");
        boolean haveValidInput = false;
        int input = -1;
        while (!haveValidInput) {
            try {
                System.out.print("Menu choice: ");
                input = consoleReader.nextInt();
            } catch (InputMismatchException exc) {
                //The input received isn't an integer
                continue;
            } catch (NoSuchElementException exc) {
                //No input found.
                continue;
            }

            if (input > 4 || input < 0 ) {
                //Input was valid but out of range.
                //Try again
                continue;
            } else {
                haveValidInput = true;
            }
        }    
        return input;
    }


    public int printMainMenu() {
        System.out.println(
            """
            1. Add a new book
            2. Search/select books
            0. Exit
            """
        );
        System.out.println();
        boolean haveValidInput = false;
        int input = -1;
        while (!haveValidInput) {
            try {
                System.out.print("Menu choice: ");
                input = consoleReader.nextInt();
            } catch (InputMismatchException exc) {
                //The input received doesn't appear to be an integer
                continue;
            } catch (NoSuchElementException exc) {
                //No input received
                continue;
            }

            if (input > 2 || input < 0 ) {
                //Input was valid but out of range.
                //Try again
                continue;
            } else {
                haveValidInput = true;
            }
        }
        return input;
    }

    public Book getBookInfoFromUser() {
        Book book = new Book();
        
        book.title =  getString("Book title: ", new BookTitlePredicate());
        book.author = getString("Book author: ", new BookAuthorPredicate());
        book.qty = getInt("Starting quantity: ", new BookQuantityPredicate());
        
        return book;
    }

    private String getString(String prompt, Predicate predicate) {
        while(true) {
            System.out.print(prompt);
            String input = consoleReader.next();
            
            if (predicate.test(input)) {
                return input;
            } else {
                System.out.println(predicate.toString());
                continue;
            }
        }
    }

    private int getInt(String prompt, Predicate predicate) { 
        int number = 0;
        while (true) {
            System.out.print(prompt);
            try {    
                number = consoleReader.nextInt();
            } catch (InputMismatchException inputEx) {
                System.out.println("The amount entered should be an integer.");
                continue;
            }
            if (predicate.test(number)) {
                break;
            }
        }
        return number;
    }

    public boolean confirmDeletion(Book book) {
        String input = " ";
        while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")) {
            System.out.print("Are you sure you want to delete the selected record? [y/n] ");
            input = consoleReader.next();
        }
        return input.equalsIgnoreCase("y");
    }

    public Book searchDialog() {
        System.out.println("""
            How would you like to search:

            1. By book title
            2. By author
            0. Back to Main menu
        """);
        
        //TODO: Repeating code block used to get and validate input. Put in method.
        System.out.println();
        boolean haveValidInput = false;
        int input = -1;
        while (!haveValidInput) {
            try {
                System.out.print("Menu choice: ");
                input = consoleReader.nextInt();
            } catch (InputMismatchException exc) {
                //The input received doesn't appear to be an integer
                continue;
            } catch (NoSuchElementException exc) {
                //No input received
                continue;
            }

            if (input > 2 || input < 0 ) {
                //Input was valid but out of range.
                //Request input again.
                continue;
            } else {
                haveValidInput = true;
            }
        }
        ArrayList<Book> searchResults;
        System.out.println();
        if (input == 0) { //Operation aborted. No new selection.
            return null;
        } else if (input == 1) {
            searchResults = new ArrayList<Book>(searchByTitleDialog());
        } else if (input == 2) {
            searchResults = new ArrayList<Book>(searchByAuthorDialog());
        } else {
            throw new AssertionError("Unhandled menu choice" + input + " encountered in search dialog");
        }

        if ( searchResults.size() == 0 ) {
            System.out.println(" -- No search results -- ");
            return null;
        } else {
            Book selection = printAndPickResult(searchResults);
            return selection;
        }
    }

    private Book printAndPickResult(ArrayList<Book> searchResults) {
        System.out.println(" -- Search results -- ");
        System.out.println();
        for (int index = 0; index < searchResults.size(); ++index) {
            StringBuilder result = new StringBuilder();
            if ( index < 9 ) {
                result.append(' ');
            }
            result.append(index + 1);
            result.append(" - ");
            result.append(searchResults.get(index).toString());
        }

        int choice = 0;
        boolean haveValidInput = false;
        while (!haveValidInput) {
            choice = 0;
            System.out.println();
            System.out.print("Select a result [0 to cancel]: ");
            try {
                choice = consoleReader.nextInt();
                haveValidInput = choice >= 0 && choice < (searchResults.size() + 1);
            } catch (InputMismatchException exc) {
                System.out.println("Please enter a number above or 0 to cancel");
            } catch (NoSuchElementException exc) {
                System.out.println("Please enter a number above or 0 to cancel");
            }
        }

        if (choice == 0) {
            return null;
        } else {
            return searchResults.get(choice -1);
        }
    }
    
    public static enum SearchCriteria {
        ByTitle,
        ByAuthor
    }

    private List<Book> searchByTitleDialog() {
        return searchbyCriteria("Book title to search for (Leave blank to show all records) : ", SearchCriteria.ByTitle);
    }

    private List<Book> searchByAuthorDialog() {
        return searchbyCriteria("Book author to search for (Leave blank to show all records) : ", SearchCriteria.ByAuthor);
    }

    private List<Book> searchbyCriteria(String prompt, SearchCriteria criteria) {
        System.out.print(prompt);
        String searchString = consoleReader.nextLine();

        return DataSource.getInstance().searchBooks(criteria, searchString);
    }

    public void cleanup() {
        consoleReader.close();
    }
}
