package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.services.BookService;
import com.example.MyBookShopApp.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final BookService bookService;

    @GetMapping("/tags/slug/{id}")
    public String booksByTag(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("booksByTagList", bookService.getBooksByTag(id, 0, 20));
        model.addAttribute("tagId", id);
        model.addAttribute("tagName", tagService.getTagById(id).getName());
        return "/tags/slug";
    }

    @GetMapping("/tags/page/{id}")
    @ResponseBody
    public BooksPageDto tagsSlugPage(@PathVariable(value = "id", required = false) Integer id,
                                     @RequestParam("offset") Integer offset,
                                     @RequestParam("limit") Integer size) {
        return bookService.getBooksByTag(id, offset, size);
    }

    @GetMapping("tags/index")
    public String tags() {
        return "/tags/index";
    }
}
