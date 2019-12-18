package com.library.mbooks.dao;

import com.library.mbooks.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookDao extends JpaRepository<Book, Integer> {
    Book findById(int id);

    @Query("from Book b where b.title LIKE %?1% or b.author LIKE %?1% or b.isbn LIKE %?1% or b.publisher LIKE %?1%")
    List<Book> findAllByTitle(String search);
}
