package controller;

import dto.BookDto;
import model.Book;
import enums.BookStatus;
import enums.BookSortType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import service.BookStore;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookStore store;

    public BookController(BookStore store) {
        this.store = store;
    }

    @GetMapping
    public List<BookDto> getAllBooks(@RequestParam(required = false) BookSortType sort) {
        List<Book> books = (sort != null) ? store.getSortedBooks(sort) : store.getBooks();
        return books.stream().map(BookDto::fromEntity).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable int id) {
        Book book = store.findBookById(id);
        if (book == null) {
            throw new IllegalArgumentException("Книга с ID " + id + " не найдена.");
        }
        return BookDto.fromEntity(book);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@RequestBody BookDto dto) {
        int id = (new java.util.Random()).nextInt(1000000);
        Book book = new Book(id, dto.getTitle(), BookStatus.IN_STOCK, dto.getPrice(),
                dto.getPublicationDate() != null ? dto.getPublicationDate() : LocalDate.now(),
                LocalDate.now(), dto.getDescription());
        Book saved = store.addBook(book);
        return BookDto.fromEntity(saved);
    }

    @PutMapping("/{id}/write-off")
    public void writeOffBook(@PathVariable int id) {
        store.writeOffBook(id);
    }
}
