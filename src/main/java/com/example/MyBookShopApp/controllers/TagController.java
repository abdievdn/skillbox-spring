package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.ControllerParamsCatch;
import com.example.MyBookShopApp.aspect.annotations.ControllerResponseCatch;
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

    @ControllerParamsCatch
    @GetMapping("/tags/slug/{id}")
    public String booksByTag(@PathVariable("id") int id, Model model) {
        model.addAttribute("booksByTagList", bookService.getPageOfBooksByTag(id, 0, 20));
        model.addAttribute("tagId", id);
        model.addAttribute("tagName", tagService.getTagById(id).getName());
        return "/tags/slug";
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping("/tags/page/{id}")
    @ResponseBody
    public BooksPageDto tagsSlugPage(@PathVariable(value = "id", required = false) int id,
                                     @RequestParam("offset") int offset,
                                     @RequestParam("limit") int size) {
        return bookService.getPageOfBooksByTag(id, offset, size);
    }

    @GetMapping("tags/index")
    public String tags() {
        return "/tags/index";
    }
}
