package org.example.web.controllers;

import org.apache.log4j.Logger;
import org.example.app.services.BookService;
import org.example.web.dto.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/books")
public class BookShelfController {

    private final Logger logger = Logger.getLogger(BookShelfController.class);
    private final BookService bookService;

    @Autowired
    public BookShelfController(BookService bookService) {
        this.bookService = bookService;
    }


    @GetMapping("/shelf")
    public String books(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("bookList", bookService.getAllBooks());
        logger.info("got book shelf");
        return "book_shelf";
    }

    @PostMapping("/save")
    public String saveBook(Book book) {
        if (!book.getAuthor().isEmpty() ||
                !book.getTitle().isEmpty() ||
                book.getSize() != null) {
            bookService.saveBook(book);
            logger.info("current repository size: " + bookService.getAllBooks().size());
        } else {
            logger.info("book is empty");
        }
        return "redirect:/books/shelf";
    }

    @PostMapping("/remove")
    public String removeBookById(@RequestParam(value = "bookIdToRemove") String bookIdToRemove) {
        logger.info("current id to remove: " + bookIdToRemove);
        if (!bookIdToRemove.isEmpty()) {
            try {
                bookService.removeBookById(Integer.parseInt(bookIdToRemove));
            }
            catch (Exception e) {
                logger.info("current id is not number!");
            }
        }
        return "redirect:/books/shelf";
    }

    @PostMapping("removeByRegex")
    public String removeBookByRegex(@RequestParam String bookAuthorToRemove,
                                    @RequestParam String bookTitleToRemove,
                                    @RequestParam String bookSizeToRemove) {
        try {
            bookService.removeBook(bookAuthorToRemove, bookTitleToRemove, Integer.parseInt(bookSizeToRemove));
        } catch (Exception e) {
            bookService.removeBook(bookAuthorToRemove, bookTitleToRemove, null);
        }
        logger.info("current repository size: " + bookService.getAllBooks().size());
        return "redirect:/books/shelf";
    }
}
