package com.bank.profile.controller;

import com.bank.profile.service.BasicCrudService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    public TDto update(@PathVariable Long id, @Valid @RequestBody TDto dto)
    {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id)
    {
        service.delete(id);
    }
}
