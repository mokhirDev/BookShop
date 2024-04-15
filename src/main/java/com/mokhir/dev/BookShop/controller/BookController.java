package com.mokhir.dev.BookShop.controller;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookRequest;
import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.service.BookService;
import io.github.jhipster.web.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static com.mokhir.dev.BookShop.utils.ApiUrls.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(BOOK)
public class BookController {
    private final BookService service;

    @GetMapping(ALL)
    @PreAuthorize("hasAuthority('AUTHOR_ACCESS')")
    ResponseEntity<Page<BookResponse>> findAll(@RequestParam("page") int pageIndex,
                                               @RequestParam("size") int pageSize,
                                               @RequestParam MultiValueMap<String, String> queryParams,
                                               UriComponentsBuilder uriBuilder) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<BookResponse> page = service.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page);
    }

    @GetMapping(ALL_BOOKS)
    @PreAuthorize("hasAuthority('ADMIN_ACCESS')")
    ResponseEntity<Page<BookResponse>> findAllBooks(@RequestParam("page") int pageIndex,
                                                    @RequestParam("size") int pageSize,
                                                    @RequestParam MultiValueMap<String, String> queryParams,
                                                    UriComponentsBuilder uriBuilder) {
        Page<BookResponse> page = service.findAll(PageRequest.of(pageIndex, pageSize));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page);
    }

    @GetMapping(ALL_BOOKS_BY_AUTHOR)
    @PreAuthorize("hasAuthority('ADMIN_ACCESS')")
    ResponseEntity<Page<BookResponse>> findAllBooksByAuthor(@RequestParam("page") int pageIndex,
                                                            @RequestParam("size") int pageSize,
                                                            @RequestParam MultiValueMap<String, String> queryParams,
                                                            UriComponentsBuilder uriBuilder,
                                                            @RequestBody BookRequest request) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<BookResponse> page = service.findAllBooksByCreatedBy(pageable, request.getCreatedBy());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page);
    }


    @DeleteMapping
    @PreAuthorize("hasAuthority('AUTHOR_ACCESS')")
    ResponseEntity<BookResponse> remove(@RequestBody BookRequest request) {
        return ResponseEntity.ok().body(service.remove(request));
    }


    @PutMapping(UPDATE)
    @PreAuthorize("hasAuthority('AUTHOR_ACCESS')")
    ResponseEntity<BookResponse> update(@RequestBody BookRequest request) {
        return ResponseEntity.ok().body(service.update(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('AUTHOR_ACCESS')")
    ResponseEntity<BookResponse> getBookById(@RequestBody BookRequest request) {
        return ResponseEntity.ok().body(service.getBookById(request));
    }

    @PostMapping(ADD_BOOK)
    @PreAuthorize("hasAuthority('AUTHOR_ACCESS')")
    ResponseEntity<BookResponse> addBook(@RequestBody BookRequest request) {
        return ResponseEntity.ok().body(service.addBook(request));
    }
}
