package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.InteractionWithBookDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.services.BookReviewService;
import com.example.MyBookShopApp.services.RatingService;
import com.example.MyBookShopApp.services.ResourceStorage;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.services.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final RatingService ratingService;
    private final BookReviewService bookReviewService;
    private final ResourceStorage storage;

    @GetMapping({"/recommended/page", "/books/recommended/slider"})
    @ResponseBody
    public BooksPageDto recommendedBooks(@RequestParam("offset") Integer offset,
                                         @RequestParam("limit") Integer size) {
        return new BooksPageDto(bookService.getPageOfRecommendedBooks(offset, size));
    }

    @GetMapping("/recent/slider")
    @ResponseBody
    public BooksPageDto recentBooks(@RequestParam("offset") Integer offset,
                                    @RequestParam("limit") Integer size) {
        return new BooksPageDto(bookService.getPageOfRecentBooks(offset, size));
    }

    @GetMapping("/recent/page")
    @ResponseBody
    public BooksPageDto recentBooks(@RequestParam("from") String from,
                                    @RequestParam("to") String to,
                                    @RequestParam("offset") Integer offset,
                                    @RequestParam("limit") Integer size) {
        return new BooksPageDto(bookService.getPageOfRecentBooks(from, to, offset, size));
    }

    @GetMapping("/recent")
    public String recentBooks() {
        return "/books/recent";
    }

    @GetMapping({"/popular/page", "/books/popular/slider"})
    @ResponseBody
    public BooksPageDto popularBooks(@RequestParam("offset") Integer offset,
                                     @RequestParam("limit") Integer size) {
        return new BooksPageDto(bookService.getPageOfPopularBooks(offset, size));
    }

    @GetMapping("/popular")
    public String popularBooks() {
        return "/books/popular";
    }

    @GetMapping("/{slug}")
    public String bookPage(@PathVariable(value = "slug", required = false) String slug, Model model) throws Exception {
        model.addAttribute("bookSlug", bookService.getBookDtoBySlug(slug));
        model.addAttribute("bookReviewList", bookReviewService.getBookReviewList(slug));
        return "/books/slug";
    }

    @PostMapping("/{slug}/img/save")
    public String saveNewBookImage(@RequestParam("file") MultipartFile file,
                                   @PathVariable(value = "slug", required = false) String slug) throws Exception {
        storage.saveNewBookImage(file, slug);
        return "redirect:/books/" + slug;
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> bookFile(@PathVariable(value = "hash", required = false) String hash) throws IOException {
        Path path = storage.getBookFilePath(hash);
        MediaType mediaType = storage.getBookFileMime(hash);
        byte[] data = storage.getBookFileByteArray(hash);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

    @PostMapping("/rateBook")
    @ResponseBody
    public ResultDto rateBook(@RequestBody InteractionWithBookDto interaction, Principal principal) {
        ratingService.saveBookRating(interaction.getBookId(), interaction.getValue());
        return new ResultDto(true);
    }

    @PostMapping("/bookReview")
    @ResponseBody
    public ResultDto bookReview(@RequestBody InteractionWithBookDto interaction, Principal principal) {
        bookReviewService.saveBookReview(interaction.getBookId(), interaction.getText(), principal);
        return new ResultDto(true);
    }

    @PostMapping("/rateBookReview")
    @ResponseBody
    public ResultDto rateBookReview() {
        return new ResultDto(true);
    }
}
