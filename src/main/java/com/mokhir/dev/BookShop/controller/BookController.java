package com.mokhir.dev.BookShop.controller;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookRequest;
import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.service.BookService;
import io.github.jhipster.web.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.mokhir.dev.BookShop.utils.ApiUrls.*;
@RestController
@RequiredArgsConstructor
@RequestMapping(BOOK_MAIN_URL)
public class BookController {
    private final BookService service;

    @GetMapping(ALL)
    @PreAuthorize("hasAuthority('AUTHOR_ACCESS')")
    ResponseEntity<List<BookResponse>> findAll(@RequestParam("page") int pageIndex,
                                                 @RequestParam("size") int pageSize,
                                                 @RequestParam MultiValueMap<String, String> queryParams,
                                                 UriComponentsBuilder uriBuilder,
                                               @RequestBody BookRequest request) {
        Page<BookResponse> page = service.findAllBooksByCreatedBy(request,PageRequest.of(pageIndex, pageSize));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('AUTHOR_ACCESS')")
    ResponseEntity<BookResponse> remove(@RequestBody BookRequest request) {
        return ResponseEntity.ok().body(service.remove(request));
    }


    @DeleteMapping(DELETE_BY_ID)
    @PreAuthorize("hasAuthority('AUTHOR_ACCESS')")
    ResponseEntity<BookResponse> removeById(@RequestBody BookRequest request) {
        return ResponseEntity.ok().body(service.remove(request));
    }

    @PutMapping(UPDATE)
    @PreAuthorize("hasAuthority('AUTHOR_ACCESS')")
    ResponseEntity<BookResponse> update(@RequestBody BookRequest request) {
        return ResponseEntity.ok().body(service.update(request));
    }

    @GetMapping(GET_BY_ID)
    @PreAuthorize("hasAuthority('AUTHOR_ACCESS')")
    ResponseEntity<BookResponse> getById(@PathVariable String entityId) {
        return ResponseEntity.ok().body(service.getById(entityId));
    }

    @PostMapping(ADD_BOOK)
    @PreAuthorize("hasAuthority('AUTHOR_ACCESS')")
    ResponseEntity<BookResponse> addBook(@RequestBody BookRequest request) {
        return ResponseEntity.ok().body(service.addBook(request));
    }
}
