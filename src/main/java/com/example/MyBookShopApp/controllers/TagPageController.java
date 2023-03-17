package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TagPageController {

    private final TagService tagService;

    @GetMapping("/tags/slug/{slug}")
    public String booksByTag(@PathVariable("slug") String slug, Model model) {
        model.addAttribute("booksByTagList", tagService.getBooksByTag(slug, 0, 20));
        model.addAttribute("tagSlug", slug);
        model.addAttribute("tagName", TagService.tagName);
        return "/tags/slug";
    }

    @GetMapping("/tags/page/{slug}")
    @ResponseBody
    public BooksPageDto tagsSlugPage(@PathVariable(value = "slug", required = false) String slug,
                                     @RequestParam("offset") Integer offset,
                                     @RequestParam("limit") Integer size) {
        return new BooksPageDto(tagService.getBooksByTag(slug, offset, size));
    }

    @GetMapping("tags/index")
    public String tags() {
        return "/tags/index";
    }
}
