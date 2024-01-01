import java.util.Scanner;
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

    public void cleanup() {
        consoleReader.close();
    }
}
