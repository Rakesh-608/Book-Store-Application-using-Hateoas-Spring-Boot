package com.example.HateoasProject.controller;

import com.example.HateoasProject.exceptions.ResourceNotFoundException;
import com.example.HateoasProject.model.Book;
import com.example.HateoasProject.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    @Autowired
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<EntityModel<Book>> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        List<EntityModel<Book>> list = new ArrayList<>();
        for (Book book : books) {
            Object o = new EntityModel<>(book,
                    linkTo(methodOn(BookController.class).getBookById(book.getId())).withRel("self"),
                    linkTo(methodOn(BookController.class).updateBook(book.getId(), null)).withRel("update"),
                    linkTo(methodOn(BookController.class).deleteBook(book.getId())).withRel("delete"));
            list.add(o);
        }
        return list;
    }

    @GetMapping("/{id}")
    public EntityModel<Book> getBookById(@PathVariable Long id) {
        Book book = bookRepository.findById(id);
        if (book == null) {
            try {
                throw new ResourceNotFoundException("Book with id " + id + " not found");
            } catch (ResourceNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return new EntityModel<>(book,
                linkTo(methodOn(BookController.class).getBookById(id)).withRel("self"),
                linkTo(methodOn(BookController.class).updateBook(id, null)).withRel("update"),
                linkTo(methodOn(BookController.class).deleteBook(id)).withRel("delete"));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Book>> createBook(@RequestBody Book book) {
        Book savedBook = bookRepository.save(book);
        ResponseEntity<EntityModel<Book>> responseEntity = ResponseEntity.created(linkTo(methodOn(BookController.class).getBookById(savedBook.getId())).toUri())
                .body(new EntityModel<>(savedBook));
        return responseEntity;
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Book>> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) throws ResourceNotFoundException {
        Book existingBook = bookRepository.findById(id);
        if (existingBook == null) {
            throw new ResourceNotFoundException("Book with id " + id + " not found");
        }
        updatedBook.setId(id);
        bookRepository.save(updatedBook);
        ResponseEntity<EntityModel<Book>> responseEntity = ResponseEntity.ok(new EntityModel<>(updatedBook));
        return responseEntity;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        bookRepository.delete(id);
        return ResponseEntity.noContent().build();
    }
}
