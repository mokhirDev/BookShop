package com.mokhir.dev.BookShop.controller;

import com.mokhir.dev.BookShop.aggregation.dto.authors.AuthorRequest;
import com.mokhir.dev.BookShop.aggregation.dto.authors.AuthorResponse;
import com.mokhir.dev.BookShop.aggregation.dto.books.BookRequest;
import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.service.AuthorService;
import com.mokhir.dev.BookShop.service.BookService;
import io.github.jhipster.web.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<List<BookResponse>> findAll(@RequestParam("page") int pageIndex,
                                                 @RequestParam("size") int pageSize,
                                                 @RequestParam MultiValueMap<String, String> queryParams,
                                                 UriComponentsBuilder uriBuilder) {
        Page<BookResponse> page = service.findAll(PageRequest.of(pageIndex, pageSize));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping(ADD)
    ResponseEntity<BookResponse> add(@RequestBody BookRequest request) {
        return ResponseEntity.ok().body(service.add(request));
    }

    @DeleteMapping
    ResponseEntity<BookResponse> remove(@RequestBody BookRequest request) {
        return ResponseEntity.ok().body(service.remove(request));
    }


    @DeleteMapping(DELETE_BY_ID)
    ResponseEntity<BookResponse> removeById(@RequestBody BookRequest request) {
        return ResponseEntity.ok().body(service.remove(request));
    }

    @PutMapping(UPDATE)
    ResponseEntity<BookResponse> update(@RequestBody BookRequest request) {
        return ResponseEntity.ok().body(service.update(request));
    }

    @GetMapping(GET_BY_ID)
    ResponseEntity<BookResponse> getById(@PathVariable String entityId) {
        return ResponseEntity.ok().body(service.getById(entityId));
    }
}
