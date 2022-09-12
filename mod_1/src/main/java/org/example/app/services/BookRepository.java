package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepository implements ProjectRepository<Book> {

    private final Logger logger = Logger.getLogger(BookRepository.class);
    private final List<Book> repo = new ArrayList<>();

    @Override
    public List<Book> retrieveAll() {
        return new ArrayList<>(repo);
    }

    @Override
    public void store(Book book) {
        book.setId(book.hashCode());
        logger.info("store new book: " + book);
        repo.add(book);
    }

    @Override
    public void removeItemById(Integer bookIdToRemove) {
        for (Book book : retrieveAll()) {
            if (book.getId().equals(bookIdToRemove)) {
                logger.info("remove book completed: " + book);
                repo.remove(book);
            }
        }
    }

    @Override
    public void removeItem(String author, String title, Integer size) {
        List<Book> books = retrieveAll();
        if (!author.isEmpty()) {
            books = filterByAuthor(books, author);
        }
        if (!title.isEmpty()) {
            books = filterByTitle(books, title);
        }
        if (size != null) {
            books = filterBySize(books, size);
        }
        logger.info("remove books completed: " + books);
        repo.removeAll(books);
    }

    private List<Book> filterByAuthor(List<Book> books, String author) {
        List<Book> filteredBooks = new ArrayList<>();
        books.forEach(b -> {
            if (b.getAuthor().contains(author)) {
                filteredBooks.add(b);
            }
        });
        return filteredBooks;
    }

    private List<Book> filterByTitle(List<Book> books, String title) {
        List<Book> filteredBooks = new ArrayList<>();
        books.forEach(b -> {
            if (b.getTitle().contains(title)) {
                filteredBooks.add(b);
            }
        });
        return filteredBooks;
    }

    private List<Book> filterBySize(List<Book> books, Integer size) {
        List<Book> filteredBooks = new ArrayList<>();
        books.forEach(b -> {
            if (b.getSize().equals(size)) {
                filteredBooks.add(b);
            }
        });
        return filteredBooks;
    }
}
