package com.mokhir.dev.BookShop.controller;

import com.mokhir.dev.BookShop.aggregation.dto.price.PriceRequest;
import com.mokhir.dev.BookShop.aggregation.dto.price.PriceResponse;
import com.mokhir.dev.BookShop.service.PriceService;
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
@RequestMapping(PRICE_MAIN_URL)
public class PriceController {
    private final PriceService service;
    @GetMapping(ALL)
    ResponseEntity<List<PriceResponse>> findAll(@RequestParam("page") int pageIndex,
                                               @RequestParam("size") int pageSize,
                                               @RequestParam MultiValueMap<String, String> queryParams,
                                               UriComponentsBuilder uriBuilder) {
        Page<PriceResponse> page = service.findAll(PageRequest.of(pageIndex, pageSize));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping(SIGN_UP)
    ResponseEntity<PriceResponse> add(@RequestBody PriceRequest request) {
        return ResponseEntity.ok().body(service.signUp(request));
    }

    @DeleteMapping
    ResponseEntity<PriceResponse> remove(@RequestBody PriceRequest request) {
        return ResponseEntity.ok().body(service.remove(request));
    }


    @DeleteMapping(DELETE_BY_ID)
    ResponseEntity<PriceResponse> removeById(@RequestBody PriceRequest request) {
        return ResponseEntity.ok().body(service.remove(request));
    }

    @PutMapping(UPDATE)
    ResponseEntity<PriceResponse> update(@RequestBody PriceRequest request) {
        return ResponseEntity.ok().body(service.update(request));
    }

    @GetMapping(GET_BY_ID)
    ResponseEntity<PriceResponse> getById(@PathVariable String entityId) {
        return ResponseEntity.ok().body(service.getById(entityId));
    }
}
