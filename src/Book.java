class Book {
    public int id;
    public String title;
    public String author;
    public int qty;

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(title).append(" by ").append(author).append(" (")
            .append(qty).append(')');
        return output.toString();
    }
}
