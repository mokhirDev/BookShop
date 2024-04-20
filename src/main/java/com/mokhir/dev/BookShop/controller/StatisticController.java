package com.mokhir.dev.BookShop.controller;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserResponse;
import com.mokhir.dev.BookShop.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/statistic")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping("/user/week")
    @PreAuthorize("hasAuthority('FULL ACCESS')")
    public ResponseEntity<Page<UserResponse>> getLastWeekRegisteredUsers
            (@RequestParam("page") int page, @RequestParam("size") int size) {
        List<UserResponse> lastWeekRegisteredUsers = statisticService.getLastWeekRegisteredUsers();
        return ResponseEntity.ok().body(
                new PageImpl<>(lastWeekRegisteredUsers,
                        PageRequest.of(page, size),
                        lastWeekRegisteredUsers.size()));
    }

    @GetMapping("/books")
    @PreAuthorize("hasAuthority('FULL ACCESS')")
    public ResponseEntity<Page<BookResponse>> getBooks
            (@RequestParam("page") int page, @RequestParam("size") int size) {
        List<BookResponse> bookResponses = statisticService.getBook();
        return ResponseEntity.ok().body(
                new PageImpl<>(bookResponses,
                        PageRequest.of(page, size),
                        bookResponses.size()));
    }

    @GetMapping("/books/popular")
    @PreAuthorize("hasAuthority('FULL ACCESS')")
    public ResponseEntity<Page<BookResponse>> getAllPopularBooks
            (@RequestParam("page") int page, @RequestParam("size") int size) {
        List<BookResponse> allPopularBooks = statisticService.getAllPopularBooks();
        return ResponseEntity.ok().body(
                new PageImpl<>(allPopularBooks,
                        PageRequest.of(page, size),
                        allPopularBooks.size()));
    }

    @GetMapping("/users/active")
    @PreAuthorize("hasAuthority('FULL ACCESS')")
    public ResponseEntity<Page<UserResponse>> getAllActiveUsers
            (@RequestParam("page") int page, @RequestParam("size") int size) {
        List<UserResponse> allActiveUsers = statisticService.getAllActiveUsers();
        return ResponseEntity.ok().body(
                new PageImpl<>(allActiveUsers,
                        PageRequest.of(page, size),
                        allActiveUsers.size()));
    }

    @GetMapping("/users/period")
    @PreAuthorize("hasAuthority('FULL ACCESS')")
    public ResponseEntity<Page<UserResponse>> getAllUsersByPeriod
            (@RequestParam("page") int page, @RequestParam("size") int size) {
        List<UserResponse> allUsersByPeriod = statisticService.getAllUsersByPeriod();
        return ResponseEntity.ok().body(
                new PageImpl<>(allUsersByPeriod,
                        PageRequest.of(page, size),
                        allUsersByPeriod.size()));
    }

}
