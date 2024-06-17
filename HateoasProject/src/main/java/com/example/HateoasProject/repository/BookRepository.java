package com.example.HateoasProject.repository;

import com.example.HateoasProject.model.Book;

import java.util.List;

public interface BookRepository {

    List<Book> findAll();

    Book findById(Long id);

    Book save(Book book);

    void delete(Long book);
}

