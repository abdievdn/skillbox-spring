package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.ControllerParamsCatch;
import com.example.MyBookShopApp.aspect.annotations.ControllerResponseCatch;
import com.example.MyBookShopApp.data.dto.InteractionWithBooksDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.dto.ReviewLikeDto;
import com.example.MyBookShopApp.errors.EntityNotFoundError;
import com.example.MyBookShopApp.services.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Tag(description = "Ratings for books and user's reviews", name = "Rating")
@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class RatingController {

    private final RatingService ratingService;


    @Operation(summary = "Set rating for book by registered user")
    @ControllerParamsCatch
    @ControllerResponseCatch
    @PostMapping("/rateBook")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResultDto rateBook(@RequestBody InteractionWithBooksDto interaction, Principal principal) throws EntityNotFoundError {
        return ratingService.saveBookRating(interaction.getBookId(), interaction.getValue(), principal);
    }

    @Operation(summary = "Set rating for book's review by registered user")
    @ControllerParamsCatch
    @ControllerResponseCatch
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/rateBookReview")
    @ResponseBody
    public ResultDto rateBookReview(@RequestBody ReviewLikeDto likeDto, Principal principal) throws EntityNotFoundError {
        return ratingService.setLikeToReview(likeDto, principal);
    }
}
