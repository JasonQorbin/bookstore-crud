import java.util.function.Predicate;

class BookTitlePredicate implements Predicate<String> {
    
    public static final int TITLE_MAX_LENGTH = 80;
    
    private String message;
    
    public BookTitlePredicate() {
        message = "OK";
    }

    public String getMessage() {
        return message;
    }
    
    public String toString() {
        return message;
    }

    public boolean test(String title) {
        boolean answer = title.length() > 0 && title.length() <= TITLE_MAX_LENGTH;
        if (answer == false) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("The maximum title length is ")
                .append(TITLE_MAX_LENGTH).append(" characters. The provided title contains ")
                .append(title.length()).append(" characters.");
            message = messageBuilder.toString();
        } else {
            message = "OK";
        }
        return answer;
    }
}
