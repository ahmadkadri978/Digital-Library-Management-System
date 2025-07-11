package kadri.Digital.Library.Management.System.repository;

import kadri.Digital.Library.Management.System.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // search by Title
    Page<Book> findByTitleContainingIgnoreCase(String title , Pageable pageable);

    // search by Author
    Page<Book> findByAuthorContainingIgnoreCase(String author , Pageable pageable);

    // search by ISBN
    Page<Book> findByIsbn(String isbn , Pageable pageable);
}
