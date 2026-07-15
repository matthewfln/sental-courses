package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.BookDto;
import enums.BookSortType;
import enums.BookStatus;
import exception.EntityNotFoundException;
import exception.GlobalExceptionHandler;
import model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import service.BookStore;
import java.time.LocalDate;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private BookStore bookStore;

    @InjectMocks
    private BookController bookController;

    private Book testBook;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        testBook = new Book(1, "Clean Architecture", BookStatus.IN_STOCK, 1800,
                LocalDate.of(2018, 1, 1), LocalDate.now(), "Uncle Bob");
    }

    @Test
    @DisplayName("getAllBooks - Позитивный: Возвращает список книг без сортировки и статус 200 OK")
    void getAllBooks_NoSort_ReturnsBooksList() throws Exception {
        when(bookStore.getBooks()).thenReturn(Collections.singletonList(testBook));

        mockMvc.perform(get("/api/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Clean Architecture"))
                .andExpect(jsonPath("$[0].price").value(1800));

        verify(bookStore, times(1)).getBooks();
    }

    @Test
    @DisplayName("getAllBooks - Позитивный: Возвращает отсортированный список при передаче параметра sort")
    void getAllBooks_WithSort_ReturnsSortedBooksList() throws Exception {
        when(bookStore.getSortedBooks(BookSortType.PRICE)).thenReturn(Collections.singletonList(testBook));

        mockMvc.perform(get("/api/books")
                        .param("sort", "PRICE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(bookStore, times(1)).getSortedBooks(BookSortType.PRICE);
    }

    @Test
    @DisplayName("getBookById - Позитивный: Книга найдена, возвращается DTO и статус 200 OK")
    void getBookById_BookExists_ReturnsBookDto() throws Exception {
        when(bookStore.findBookById(1)).thenReturn(testBook);

        mockMvc.perform(get("/api/books/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Architecture"));

        verify(bookStore, times(1)).findBookById(1);
    }

    @Test
    @DisplayName("getBookById - Негативный: Книга не найдена, выбрасывается 404 Not Found")
    void getBookById_BookNotFound_ReturnsNotFound() throws Exception {
        when(bookStore.findBookById(99)).thenThrow(new EntityNotFoundException("Книга с ID 99 не найдена."));

        mockMvc.perform(get("/api/books/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Книга с ID 99 не найдена."));

        verify(bookStore, times(1)).findBookById(99);
    }

    @Test
    @DisplayName("createBook - Позитивный: Создает новую книгу и возвращает статус 201 Created")
    void createBook_ValidDto_ReturnsCreatedBook() throws Exception {
        BookDto requestDto = new BookDto(0, "New Book", BookStatus.IN_STOCK, 1000, LocalDate.now(), "Desc");
        when(bookStore.createAndAddBook(any(BookDto.class))).thenReturn(testBook);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Architecture"));

        verify(bookStore, times(1)).createAndAddBook(any(BookDto.class));
    }

    @Test
    @DisplayName("writeOffBook - Позитивный: Списывает книгу и возвращает статус 200 OK")
    void writeOffBook_BookExists_ReturnsOk() throws Exception {
        mockMvc.perform(put("/api/books/1/write-off"))
                .andExpect(status().isOk());

        verify(bookStore, times(1)).writeOffBook(1);
    }

    @Test
    @DisplayName("writeOffBook - Негативный: Попытка списать несуществующую книгу вызывает 400 Bad Request")
    void writeOffBook_BookNotFound_ReturnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Книга с ID 99 не найдена.")).when(bookStore).writeOffBook(anyInt());

        mockMvc.perform(put("/api/books/99/write-off"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));

        verify(bookStore, times(1)).writeOffBook(99);
    }
}
