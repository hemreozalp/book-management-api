package com.hemreozalp.book_management_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hemreozalp.book_management_api.model.Book;
import com.hemreozalp.book_management_api.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBook_success() {
        Book book = new Book();
        book.setTitle("Title");

        when(bookRepository.save(book)).thenReturn(book);

        Book savedBook = bookService.createBook(book);

        assertEquals(book, savedBook);
        verify(bookRepository).save(book);
    }

    @Test
    void getAllBooks_returnsList() {
        List<Book> books = List.of(new Book(), new Book());
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
    }

    @Test
    void getBookById_found() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void getBookById_notFound() {
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.getBookById(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateBook_success() {
        Book existing = new Book();
        existing.setId(1L);
        existing.setTitle("Old Title");

        Book updated = new Book();
        updated.setTitle("New Title");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);

        Book result = bookService.updateBook(1L, updated);

        assertEquals("New Title", result.getTitle());
        verify(bookRepository).save(existing);
    }

    @Test
    void updateBook_notFound_throwsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            bookService.updateBook(1L, new Book());
        });

        assertTrue(ex.getMessage().contains("Book not found"));
    }

    @Test
    void deleteBook_success() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_notFound_throwsException() {
        when(bookRepository.existsById(2L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            bookService.deleteBook(2L);
        });

        assertTrue(ex.getMessage().contains("Book not found"));
    }
}

