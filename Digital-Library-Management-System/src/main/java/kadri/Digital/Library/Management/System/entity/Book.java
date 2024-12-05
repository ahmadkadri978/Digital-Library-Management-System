package kadri.Digital.Library.Management.System.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author must not exceed 255 characters")
    private String author;

    @Column
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "ISBN is required")
    @Size(max = 13, message = "ISBN must not exceed 13 characters")
    private String isbn;

    @Column
    @Min(value = 0, message = "Copies available must be 0 or more")
    private int copiesAvailable;
    // Constructors, Getters, and Setters
    public Book() {}

    public Book(String title, String author, String description, String isbn, int copiesAvailable) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.isbn = isbn;
        this.copiesAvailable = copiesAvailable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

}

