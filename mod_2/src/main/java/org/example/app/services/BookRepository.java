package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.Book;
import org.example.web.dto.BookToRemove;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class BookRepository implements ProjectRepository<Book>, ApplicationContextAware {

    private final Logger logger = Logger.getLogger(BookRepository.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public BookRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Book> retrieveAll() {
        return jdbcTemplate.query("SELECT * FROM books", (ResultSet rs, int rowNum) -> {
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setAuthor(rs.getString("author"));
            book.setTitle(rs.getString("title"));
            book.setSize(rs.getInt("size"));
            return book;
        });
    }

    @Override
    public void store(Book book) {
        //book.setId(context.getBean(IdProvider.class).provideId(book));
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("author", book.getAuthor());
        parameterSource.addValue("title", book.getTitle());
        parameterSource.addValue("size", book.getSize());
        jdbcTemplate.update("INSERT INTO books(author,title,size) VALUES(:author,:title,:size)", parameterSource);
        logger.info("store new book: " + book);
//        repo.add(book);
    }

    @Override
    public boolean removeItem(BookToRemove bookToRemove) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        if (bookToRemove.getId() != null) {
            parameterSource.addValue("id", bookToRemove.getId());
        } else {
            parameterSource.addValue("id", "%");
        }
        if (bookToRemove.getSize() != null) {
            parameterSource.addValue("size", bookToRemove.getSize());
        } else {
            parameterSource.addValue("size", "%");
        }
        if (!bookToRemove.getAuthor().isEmpty()) {
            parameterSource.addValue("author", bookToRemove.getAuthor());
        } else {
            parameterSource.addValue("author", "%");
        }
        if (!bookToRemove.getTitle().isEmpty()) {
            parameterSource.addValue("title", bookToRemove.getTitle());
        } else {
            parameterSource.addValue("title", "%");
        }
        jdbcTemplate.update("DELETE FROM books WHERE id LIKE :id AND author LIKE :author AND title LIKE :title AND size LIKE :size", parameterSource);
        logger.info("remove book completed");
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    }

    private void defaultInit() {
        logger.info("default INIT in provider in repo");
    }

    private void defaultDestroy() {
        logger.info("default DESTROY in provider in repo");
    }
}
