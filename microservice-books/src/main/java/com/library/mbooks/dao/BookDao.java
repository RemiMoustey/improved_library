package com.library.mbooks.dao;

import com.library.mbooks.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookDao extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {
    Book findById(int id);

    @Query("from Book b where b.title LIKE %?1% or b.author LIKE %?1% or b.isbn LIKE %?1% or b.publisher LIKE %?1%")
    List<Book> findAllByTitle(String search);

    List<Book> findAllByIdIn(List<Integer> bookIdList);
}
