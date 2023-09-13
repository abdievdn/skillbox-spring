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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(description = "Book Shop details", name = "BookShop")
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

    @Operation(summary = "Get page of recommended books for slider",
            parameters = {
                    @Parameter(name = "offset", description = "Get number of books page"),
                    @Parameter(name = "limit", description = "Get size of books page")})
    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping({"/recommended/slider"})
    @ResponseBody
    public BooksPageDto recommendedBooksPage(@RequestParam("offset") int offset,
                                             @RequestParam("limit") int size) {
        return bookService.getPageOfRecommendedBooks(offset, size);
    }

    @Operation(summary = "Get list of recent books for slider",
            parameters = {
                    @Parameter(name = "offset", description = "Get number of books page"),
                    @Parameter(name = "limit", description = "Get size of books page")})
    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping("/recent/slider")
    @ResponseBody
    public BooksPageDto recentBooksSlider(@RequestParam("offset") int offset,
                                          @RequestParam("limit") int size) {
        return bookService.getPageOfRecentBooks(offset, size);
    }

    @Operation(summary = "Get list of recommended books with additional date parameters",
            parameters = {
                    @Parameter(name = "from", description = "Get start Date for list of recent books"),
                    @Parameter(name = "to", description = "Get end Date for list of recent books"),
                    @Parameter(name = "offset", description = "Get number of books page"),
                    @Parameter(name = "limit", description = "Get size of books page")})
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

    @Operation(summary = "Get index page of recent books")
    @GetMapping("/recent")
    public String recentBooks() {
        return "/books/recent";
    }

    @Operation(summary = "Get list of recent books for slider and page",
            parameters = {
                    @Parameter(name = "offset", description = "Get number of books page"),
                    @Parameter(name = "limit", description = "Get size of books page")})
    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping({"/popular/page", "/popular/slider"})
    @ResponseBody
    public BooksPageDto popularBooksPage(@RequestParam("offset") int offset,
                                         @RequestParam("limit") int size) {
        return bookService.getPageOfPopularBooks(offset, size);
    }

    @Operation(summary = "Get index page of popular books")
    @GetMapping("/popular")
    public String popularBooks() {
        return "/books/popular";
    }

    @Operation(summary = "Get page of single book")
    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping("/{slug}")
    public String bookPage(
            @Parameter(name = "slug", description = "Identify book by slug value")
            @PathVariable(value = "slug", required = false) String slug, Model model, Principal principal) {
        model.addAttribute("book", bookService.getBookDtoBySlug(slug, principal));
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

    @Operation(summary = "Set rating for book by registered user")
    @ControllerParamsCatch
    @ControllerResponseCatch
    @PostMapping("/rateBook")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResultDto rateBook(@RequestBody InteractionWithBooksDto interaction, Principal principal) {
        ratingService.saveBookRating(interaction.getBookId(), interaction.getValue(), principal);
        return new ResultDto();
    }

    @Operation(summary = "Send review for book by registered user")
    @ControllerParamsCatch
    @ControllerResponseCatch
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/bookReview")
    @ResponseBody
    public ResultDto bookReview(@RequestBody InteractionWithBooksDto interaction, Principal principal) {
        bookReviewService.saveBookReview(interaction.getBookId(), interaction.getText(), principal);
        return new ResultDto();
    }

    @Operation(summary = "Set rating for book's review by registered user")
    @ControllerParamsCatch
    @ControllerResponseCatch
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/rateBookReview")
    @ResponseBody
    public ResultDto rateBookReview() {
        return new ResultDto();
    }
}
