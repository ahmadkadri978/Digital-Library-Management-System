package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    BookRepository bookRepository;
    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Page<Book> getAllBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return bookRepository.findAll(pageable);
    }

    @Override
    public Page<Book> searchBooksByTitle(String title,int page, int size) {
        Pageable pageable = PageRequest.of(page , size);
        return bookRepository.findByTitleContainingIgnoreCase(title , pageable);
    }

    @Override
    public Page<Book> searchBooksByAuthor(String author, int page, int size) {
        Pageable pageable  = PageRequest.of(page , size);
        return bookRepository.findByAuthorContainingIgnoreCase(author , pageable);
    }

    @Override
    public Page<Book> searchBooksByIsbn(String isbn, int page, int size) {
        Pageable pageable = PageRequest.of(page , size );
        return bookRepository.findByIsbn(isbn , pageable);
    }

    @Override
    public Book getBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if(book.isPresent())
        return book.get();
        else return null;
    }

    @Override
    public void updateBook(Long id, Book book) {
        Optional<Book> updateBook = bookRepository.findById(id);
        if(updateBook.isPresent())
        {
            updateBook.get().setTitle(book.getTitle());
            updateBook.get().setAuthor(book.getAuthor());
            updateBook.get().setDescription(book.getDescription());
            bookRepository.save(updateBook.get());

        }
    }

    @Override
    public void deleteBook(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) bookRepository.deleteById(id);
    }


}
