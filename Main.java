import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Book class to represent book data
class Book {
    private int id;
    private String title;
    private String author;
    private String category;

    // Constructors
    public Book(String title, String author, String category) {
        this.title = title;
        this.author = author;
        this.category = category;
    }

    public Book(int id, String title, String author, String category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }

    @Override
    public String toString() {
        return "ID: " + id + ", Title: " + title + ", Author: " + author + ", Category: " + category;
    }
}

// DAO (Data Access Object) for database operations
class LibraryDAO {
    private Connection connection;

    // Establish the database connection
    public LibraryDAO() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db", "root", "");
        } catch (SQLException e) {
            System.err.println("Error connecting to the database. Please check your database settings.");
            e.printStackTrace();
        }
    }

    // Load all books from the database
    public List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                books.add(new Book(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Failed to load books from the database.");
            e.printStackTrace();
        }
        return books;
    }

    // Add a book to the database
    public boolean addBook(Book book) {
        String query = "INSERT INTO books (title, author, category) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Failed to add the book to the database.");
            e.printStackTrace();
            return false;
        }
    }

    // Update a book in the database
    public boolean updateBook(Book book) {
        String query = "UPDATE books SET title = ?, author = ?, category = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());
            statement.setInt(4, book.getId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Failed to update the book in the database.");
            e.printStackTrace();
            return false;
        }
    }

    // Delete a book from the database
    public boolean deleteBook(int bookId) {
        String query = "DELETE FROM books WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bookId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Failed to delete the book from the database.");
            e.printStackTrace();
            return false;
        }
    }
}

// CLI interface for library operations
class LibraryUI {
    private LibraryDAO libraryDAO = new LibraryDAO();

    // Display all books
    public void displayBooks() {
        List<Book> books = libraryDAO.loadBooks();
        if (books.isEmpty()) {
            System.out.println("No books available or failed to load books from the database.");
        } else {
            System.out.println("Books available in the library:");
            for (Book book : books) {
                System.out.println(book);
            }
        }
    }

    // Add a new book
    public void addBook(String title, String author, String category) {
        Book newBook = new Book(title, author, category);
        if (libraryDAO.addBook(newBook)) {
            System.out.println("Book added successfully.");
        } else {
            System.out.println("Failed to add book.");
        }
    }

    // Update a book
    public void updateBook(int id, String title, String author, String category) {
        Book updatedBook = new Book(id, title, author, category);
        if (libraryDAO.updateBook(updatedBook)) {
            System.out.println("Book updated successfully.");
        } else {
            System.out.println("Failed to update book.");
        }
    }

    // Delete a book
    public void deleteBook(int id) {
        if (libraryDAO.deleteBook(id)) {
            System.out.println("Book deleted successfully.");
        } else {
            System.out.println("Failed to delete book.");
        }
    }
}

// Main class for CLI operations
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final LibraryUI libraryUI = new LibraryUI();

    public static void main(String[] args) {
        System.out.println("Welcome to the E-Library CLI!");
        
        while (true) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    libraryUI.displayBooks();
                    break;
                case "2":
                    addBook();
                    break;
                case "3":
                    updateBook();
                    break;
                case "4":
                    deleteBook();
                    break;
                case "5":
                    System.out.println("Exiting the E-Library. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please choose a valid number from the menu.");
            }
        }
    }

    // Display the main menu
    private static void displayMenu() {
        System.out.println("\nE-Library Menu:");
        System.out.println("1. View all books");
        System.out.println("2. Add a new book");
        System.out.println("3. Update a book");
        System.out.println("4. Delete a book");
        System.out.println("5. Exit");
        System.out.print("Please enter your choice: ");
    }

    // Helper method to add a book
    private static void addBook() {
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        System.out.print("Enter category: ");
        String category = scanner.nextLine();

        libraryUI.addBook(title, author, category);
    }

    // Helper method to update a book
    private static void updateBook() {
        System.out.print("Enter the ID of the book to update: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter new title: ");
        String title = scanner.nextLine();
        System.out.print("Enter new author: ");
        String author = scanner.nextLine();
        System.out.print("Enter new category: ");
        String category = scanner.nextLine();

        libraryUI.updateBook(id, title, author, category);
    }

    // Helper method to delete a book
    private static void deleteBook() {
        System.out.print("Enter the ID of the book to delete: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        libraryUI.deleteBook(id);
    }
}
