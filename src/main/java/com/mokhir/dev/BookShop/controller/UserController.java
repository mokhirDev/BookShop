package com.mokhir.dev.BookShop.controller;

import com.mokhir.dev.BookShop.aggregation.dto.users.UserRequest;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserResponse;
import com.mokhir.dev.BookShop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import io.github.jhipster.web.util.PaginationUtil;
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
@RequestMapping(USER)
public class UserController {
    private final UserService service;

    @GetMapping(ALL)
    @PreAuthorize("hasAuthority('ADMIN_ACCESS')")
    public ResponseEntity<List<UserResponse>> findAll(@RequestParam("page") int pageIndex,
                                               @RequestParam("size") int pageSize,
                                               @RequestParam MultiValueMap<String, String> queryParams,
                                               UriComponentsBuilder uriBuilder) {
        Page<UserResponse> page = service.findAll(PageRequest.of(pageIndex, pageSize));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('ADMIN_ACCESS')")
    public ResponseEntity<UserResponse> remove(@RequestBody UserRequest request) {
        return ResponseEntity.ok().body(service.remove(request));
    }


    @DeleteMapping(DELETE_BY_ID)
    @PreAuthorize("hasAuthority('ADMIN_ACCESS')")
    public ResponseEntity<UserResponse> removeById(@RequestBody UserRequest request) {
        return ResponseEntity.ok().body(service.remove(request));
    }

    @PutMapping(UPDATE)
    public ResponseEntity<UserResponse> update(@RequestBody UserRequest request) {
        return ResponseEntity.ok().body(service.update(request));
    }

    @GetMapping(GET_BY_ID)
    @PreAuthorize("hasAuthority('ADMIN_ACCESS')")
    ResponseEntity<UserResponse> getById(@PathVariable String entityId) {
        return ResponseEntity.ok().body(service.getById(entityId));
    }

    @PostMapping(ADD_ADMIN)
    @PreAuthorize("hasAuthority('ADMIN_ACCESS')")
    public ResponseEntity<UserResponse> addAdmin(@RequestBody UserRequest request) {
        return ResponseEntity.ok().body(service.addAdmin(request));
    }

    @PostMapping(SIGN_IN)
    public ResponseEntity<?> signIn(@RequestBody SignIn signIn) {
        return ResponseEntity.ok().body(service.signIn(signIn));
    }

    @PostMapping(SIGN_UP)
    public ResponseEntity<UserResponse> add(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok().body(service.register(request));
    }

    @PostMapping(ADD_AUTHOR)
    @PreAuthorize("hasAuthority('ADMIN_ACCESS')")
    public ResponseEntity<UserResponse> addAuthor(@RequestBody UserRequest request) {
        return ResponseEntity.ok().body(service.addAuthor(request));
    }

    @GetMapping
    public ResponseEntity<UserResponse> info() {
        return ResponseEntity.ok().body(service.getCurrentUser());
    }
}