import java.util.Scanner;

import javax.sql.rowset.Predicate;

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
        while (!haveValidInput) {
            try {
                System.out.print("Menu choice: ");
                int input = consoleReader.nextInt();
            } catch (InputMismatchException | NoSuchElementException exc) {
                //Either nothing was typed in or the input was not an integer.
                //Go back to the start of the loop and try getting input again.
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
        System.out.println("Selected book: "
            + selectedBook.title + " by " + selectedBook.author + " (" + selectedBook.qty + ")");
        boolean haveValidInput = false;
        while (!haveValidInput) {
            try {
                System.out.print("Menu choice: ");
                int input = consoleReader.nextInt();
            } catch (InputMismatchException | NoSuchElementException exc) {
                //Either nothing was typed in or the input was not an integer.
                //Go back to the start of the loop and try getting input again.
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
        Book book;
        String title;
        String author;
        int qty;
        
        book.title =  getString("Book title: ", new BookTitlePredicate());
        book.author = getString("Book author: ", new BookAuthorPredicate());
        book.qty = getInt("Starting quantity: ", new BookQuantityPredicate());
        
        return book;
    }

    private String getString(String prompt, Predicate<String> predicate) {
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

    private int getInt(String prompt, Predicate<Integer> predicate) { 
        while (true) {
            System.out.print(prompt);
            try {    
                qty = consoleReader.nextInt();
            } catch (InputMismatchException inputEx) {
                System.out.println("The amount entered should be an integer.");
                continue;
            }

        }
    }

    @Override
    protected void finalize() {
        try {
            consoleReader.close();
        } catch (IOException exc) {
            System.out.println("Problem closing terminal scanner.");
        }
    }
}
