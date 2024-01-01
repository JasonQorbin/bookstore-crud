import java.util.function.Predicate;

class BookAuthorPredicate implements Predicate {
    
    public static final int AUTHOR_MAX_LENGTH = 80;
    
    private String message;
    
    public BookAuthorPredicate() {
        message = "OK";
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }

    public boolean test(String author) {
        boolean answer = author.length() > 0 && author.length() <= AUTHOR_MAX_LENGTH;
        if (answer == false) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("The maximum author length is ")
                .append(AUTHOR_MAX_LENGTH).append(" characters. The provided author contains ")
                .append(author.length()).append(" characters.");
            message = messageBuilder.toString();
        } else {
            message = "OK";
        }
        return answer;
    }
}
