package com.bank.profile.controller;

import com.bank.profile.service.BasicCrudService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class BasicCrudController<TService extends BasicCrudService<TDto>, TDto> {

    protected final TService service;

    public BasicCrudController(TService service) {
        this.service = service;
    }

    @GetMapping("")
    public List<TDto> getAll()
    {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TDto getOne(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping("")
    public TDto create(@Valid @RequestBody TDto dto)
    {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public TDto change(@Valid @RequestBody TDto dto)
    {
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id)
    {
        service.delete(id);
    }
}
