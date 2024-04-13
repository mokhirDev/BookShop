package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.price.PriceRequest;
import com.mokhir.dev.BookShop.aggregation.dto.price.PriceResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Prices;
import com.mokhir.dev.BookShop.aggregation.mapper.PriceMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import com.mokhir.dev.BookShop.repository.interfaces.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PriceService
        implements EntityServiceInterface<Prices, PriceRequest, PriceResponse, String> {
    private final PriceRepository repository;
    private final PriceMapper mapper;
    @Override
    public PriceResponse getById(String id) {
        try {
            Long realId = Long.valueOf(id);
            Optional<Prices> byId = repository.findById(realId);
            if (byId.isEmpty()) {
                throw new NotFoundException(id + ": not found");
            }
            Prices prices = byId.get();
            return mapper.toDto(prices);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }

    }

    @Override
    public Page<PriceResponse> findAll(Pageable pageable) {
        try {
            Page<Prices> all = repository.findAll(pageable);
            if (all.isEmpty()) {
                throw new NotFoundException("Book didn't found");
            }
            return all.map(mapper::toDto);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public PriceResponse signUp(PriceRequest request) {
        try {
            Prices entity = mapper.toEntity(request);
            Prices save = repository.save(entity);
            return mapper.toDto(save);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public PriceResponse remove(PriceRequest request) {
        try {
            Prices entity = mapper.toEntity(request);
            repository.delete(entity);
            return mapper.toDto(entity);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public PriceResponse removeById(String id) {
        try {
            Long realId = Long.valueOf(id);
            Optional<Prices> byId = repository.findById(realId);
            if (byId.isPresent()) {
                repository.deleteById(realId);
                return mapper.toDto(byId.get());
            }
            throw new NotFoundException(realId+": Doesn't exist");
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public PriceResponse update(PriceRequest request) {
        try{
            Long id = request.getId();
            Optional<Prices> byId = repository.findById(id);
            if (byId.isPresent()){
                mapper.updateFromDto(request, byId.get());
                Prices entity = mapper.toEntity(request);
                return mapper.toDto(entity);
            }
            throw new NotFoundException(id+": Didn't found");
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }
}
