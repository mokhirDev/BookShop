package com.mokhir.dev.BookShop.controller;

import com.mokhir.dev.BookShop.aggregation.dto.users.UserRequest;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserResponse;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
import com.mokhir.dev.BookShop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import io.github.jhipster.web.util.PaginationUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.mokhir.dev.BookShop.utils.ApiUrls.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(MAIN_URL)
public class UserController {
    private final UserService service;
    private final JwtProvider provider;

    @GetMapping(ALL)
//    @PreAuthorize("hasAuthority('USER_ACCESS')")
    ResponseEntity<List<UserResponse>> findAll(@RequestParam("page") int pageIndex,
                                               @RequestParam("size") int pageSize,
                                               @RequestParam MultiValueMap<String, String> queryParams,
                                               UriComponentsBuilder uriBuilder) {
        System.out.println("------->pageSize: " + pageSize);
        Page<UserResponse> page = service.findAll(PageRequest.of(pageIndex, pageSize));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping(SIGN_UP)
    ResponseEntity<UserResponse> add(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok().body(service.signUp(request));
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

    @PostMapping(ADD_ADMIN)
//    @PreAuthorize("hasAuthority('ADMIN_ACCESS')")
    ResponseEntity<UserResponse> addAdmin(@RequestBody UserRequest request) {
        return ResponseEntity.ok().body(service.addAdmin(request));
    }

    @PostMapping(SIGN_IN)
    public ResponseEntity<?> login(@RequestBody SignIn signIn) {
        return new ResponseEntity<>(service.signIn(signIn), HttpStatus.OK);
    }

    @PostMapping("/info")
//    @PreAuthorize("hasAuthority('USER_ACCESS')")
    public ResponseEntity<?> info() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        System.out.println("currentUserName=============>" + currentUserName);
        return null;
    }
}