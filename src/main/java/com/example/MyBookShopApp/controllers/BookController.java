package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.ControllerParamsCatch;
import com.example.MyBookShopApp.aspect.annotations.ControllerResponseCatch;
import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.InteractionWithBooksDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.services.BookReviewService;
import com.example.MyBookShopApp.services.RatingService;
import com.example.MyBookShopApp.services.BookFileService;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final RatingService ratingService;
    private final BookReviewService bookReviewService;
    private final BookFileService storage;

    @ModelAttribute("recentBooks")
    public List<BookDto> recentBooksModel() {
        return bookService.getPageOfRecentBooks(
                DefaultController.DEFAULT_OFFSET,
                DefaultController.DEFAULT_SIZE).getBooks();
    }

    @ModelAttribute("popularBooks")
    public List<BookDto> popularBooksModel() {
        return bookService.getPageOfPopularBooks(
                DefaultController.DEFAULT_OFFSET,
                DefaultController.DEFAULT_SIZE).getBooks();
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping({"/recommended/page", "/recommended/slider"})
    @ResponseBody
    public BooksPageDto recommendedBooksPage(@RequestParam("offset") int offset,
                                             @RequestParam("limit") int size) {
        return bookService.getPageOfRecommendedBooks(offset, size);
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping("/recent/slider")
    @ResponseBody
    public BooksPageDto recentBooksSlider(@RequestParam("offset") int offset,
                                          @RequestParam("limit") int size) {
        return bookService.getPageOfRecentBooks(offset, size);
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping("/recent/page")
    @ResponseBody
    public BooksPageDto recentBooksFromToPage(@RequestParam("from") String from,
                                              @RequestParam("to") String to,
                                              @RequestParam("offset") int offset,
                                              @RequestParam("limit") int size) {
        return bookService.getPageOfRecentBooks(from, to, offset, size);
    }

    @GetMapping("/recent")
    public String recentBooks() {
        return "/books/recent";
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping({"/popular/page", "/popular/slider"})
    @ResponseBody
    public BooksPageDto popularBooksPage(@RequestParam("offset") int offset,
                                         @RequestParam("limit") int size) {
        return bookService.getPageOfPopularBooks(offset, size);
    }

    @GetMapping("/popular")
    public String popularBooks() {
        return "/books/popular";
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping("/{slug}")
    public String bookPage(@PathVariable(value = "slug", required = false) String slug, Model model) {
        model.addAttribute("book", bookService.getBookDtoBySlug(slug));
        model.addAttribute("bookReviewList", bookReviewService.getBookReviewList(slug));
        return "/books/slug";
    }

    @ControllerParamsCatch
    @PostMapping("/{slug}/img/save")
    public String saveNewBookImage(@RequestParam("file") MultipartFile file,
                                   @PathVariable(value = "slug", required = false) String slug) throws Exception {
        storage.saveNewBookImage(file, slug);
        return "redirect:/books/" + slug;
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @PreAuthorize("hasRole('USER')")
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

    @ControllerParamsCatch
    @ControllerResponseCatch
    @PostMapping("/rateBook")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResultDto rateBook(@RequestBody InteractionWithBooksDto interaction, Principal principal) {
        ratingService.saveBookRating(interaction.getBookId(), interaction.getValue(), principal);
        return new ResultDto();
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/bookReview")
    @ResponseBody
    public ResultDto bookReview(@RequestBody InteractionWithBooksDto interaction, Principal principal) {
        bookReviewService.saveBookReview(interaction.getBookId(), interaction.getText(), principal);
        return new ResultDto();
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/rateBookReview")
    @ResponseBody
    public ResultDto rateBookReview() {
        return new ResultDto();
    }
}
