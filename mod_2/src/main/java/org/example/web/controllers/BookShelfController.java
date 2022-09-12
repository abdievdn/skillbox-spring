package org.example.web.controllers;

import org.apache.log4j.Logger;
import org.example.app.services.BookService;
import org.example.web.dto.Book;
import org.example.web.dto.BookToRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Controller
@Scope("singleton")
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
        logger.info(this.toString());
        model.addAttribute("book", new Book());
        model.addAttribute("bookToRemove", new BookToRemove());
        model.addAttribute("bookList", bookService.getAllBooks());
        return "book_shelf";
    }

    @PostMapping("/save")
    public String saveBook(@Valid Book book, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            logger.info("wrong input in some field");
            model.addAttribute("book", book);
            model.addAttribute("bookToRemove", new BookToRemove());
            model.addAttribute("bookList", bookService.getAllBooks());
            return "book_shelf";
        } else {
            bookService.saveBook(book);
            logger.info("current repository size: " + bookService.getAllBooks().size());
            return "redirect:/books/shelf";
        }
    }

    @PostMapping("/remove")
    public String removeBook(@Valid BookToRemove bookToRemove, BindingResult bindingResult, Model model) {
        logger.info("current book to remove: " + bookToRemove.toString());
        if (!bindingResult.hasErrors() && isBookToRemoveHasAnyValue(bookToRemove)) {
            bookService.removeBook(bookToRemove);
            return "redirect:/books/shelf";
        }
        if (!isBookToRemoveHasAnyValue(bookToRemove)) {
            model.addAttribute("emptyFields", "at least one field must be filled");
        }
        logger.info("wrong input for remove");
        model.addAttribute("book", new Book());
        model.addAttribute("bookList", bookService.getAllBooks());
        return "book_shelf";
    }

    private boolean isBookToRemoveHasAnyValue(BookToRemove bookToRemove) {
        return bookToRemove.getId() != null ||
                bookToRemove.getSize() != null ||
                !bookToRemove.getAuthor().isEmpty() ||
                !bookToRemove.getTitle().isEmpty();
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) throws Exception {
        String name = file.getOriginalFilename();
        byte[] bytes = file.getBytes();
        String rootPath = System.getProperty("catalina.home");
        File dir = new File(rootPath + File.separator + "external_uploads");
        if (!dir.exists() && !dir.mkdirs()) {
            throw new FileNotFoundException();
        }
        File serverFile = new File(dir.getAbsolutePath() + File.separator + name);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(bytes);
        stream.close();
        logger.info("new file saved at: " + serverFile.getAbsolutePath());
        books(model);
        model.addAttribute("uploadSuccess", "file was successfully uploaded");
        return "book_shelf";
    }

    @ExceptionHandler(FileNotFoundException.class)
    public String handleFileError(Model model) {
        logger.info("no file selected");
        books(model);
        model.addAttribute("uploadError", "no file selected or server is busy");
        return "book_shelf";
    }
}
