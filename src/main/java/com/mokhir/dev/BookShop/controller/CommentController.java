package com.mokhir.dev.BookShop.controller;

import com.mokhir.dev.BookShop.aggregation.dto.comments.CommentRequest;
import com.mokhir.dev.BookShop.aggregation.dto.comments.CommentResponse;
import com.mokhir.dev.BookShop.service.CommentService;
import io.github.jhipster.web.util.PaginationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static com.mokhir.dev.BookShop.utils.ApiUrls.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(COMMENT)
public class CommentController {
    private final CommentService service;

    @GetMapping(ALL)
    public ResponseEntity<Page<CommentResponse>> findAll(@RequestParam("page") int pageIndex,
                                                  @RequestParam("size") int pageSize,
                                                  @RequestParam MultiValueMap<String, String> queryParams,
                                                  UriComponentsBuilder uriBuilder) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<CommentResponse> page = service.findAll(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page);
    }

    @GetMapping(GET_BY_ID)
    public ResponseEntity<CommentResponse> getById(@PathVariable Long entityId) {
        CommentResponse byId = service.getById(entityId);
        return ResponseEntity.ok().body(byId);
    }

    @PostMapping(ADD)
    @PreAuthorize("hasAuthority('USER_ACCESS')")
    public ResponseEntity<CommentResponse> add(@RequestBody CommentRequest request) {
        return ResponseEntity.ok().body(service.register(request));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('USER_ACCESS')")
    public ResponseEntity<CommentResponse> update(@RequestBody CommentRequest request) {
        return ResponseEntity.ok().body(service.update(request));
    }

    @DeleteMapping
    public ResponseEntity<CommentResponse> delete(@RequestBody CommentRequest request) {
        return ResponseEntity.ok().body(service.remove(request));
    }
}
