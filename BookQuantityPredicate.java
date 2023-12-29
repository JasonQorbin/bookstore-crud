import java.util.function.Predicate;

class BookQuantityPredicate implements Predicate<Integer> {
    
    public boolean test(Integer quantity) {
        return quantity > 0;
    }
}
