package com.mokhir.dev.BookShop.controller;

import com.mokhir.dev.BookShop.aggregation.dto.users.UserRequest;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserResponse;
import com.mokhir.dev.BookShop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import io.github.jhipster.web.util.PaginationUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.mokhir.dev.BookShop.utils.ApiUrls.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(USER_MAIN_URL)
public class UserController {
    private final UserService service;

    @GetMapping(ALL)
    ResponseEntity<List<UserResponse>> findAll(@RequestParam("page") int pageIndex,
                                               @RequestParam("size") int pageSize,
                                               @RequestParam MultiValueMap<String, String> queryParams,
                                               UriComponentsBuilder uriBuilder) {
        Page<UserResponse> page = service.findAll(PageRequest.of(pageIndex, pageSize));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping(ADD)
    ResponseEntity<UserResponse> add(@RequestBody UserRequest request) {
        return ResponseEntity.ok().body(service.add(request));
    }

    @DeleteMapping
    ResponseEntity<UserResponse> remove(@RequestBody UserRequest request) {
        return ResponseEntity.ok().body(service.remove(request));
    }


    @DeleteMapping(DELETE_BY_ID)
    ResponseEntity<UserResponse> removeById(@RequestBody UserRequest request) {
        return ResponseEntity.ok().body(service.remove(request));
    }

    @PutMapping(UPDATE)
    ResponseEntity<UserResponse> update(@RequestBody UserRequest request) {
        return ResponseEntity.ok().body(service.update(request));
    }

    @GetMapping(GET_BY_ID)
    ResponseEntity<UserResponse> getById(@PathVariable String entityId) {
        return ResponseEntity.ok().body(service.getById(entityId));
    }

}