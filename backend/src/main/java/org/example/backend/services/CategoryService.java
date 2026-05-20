package org.example.backend.services;

import org.example.backend.entities.Category;
import org.example.backend.repositories.CategoryRepository;

import javax.inject.Inject;
import java.util.List;

public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Inject
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category create(Category category) {
        validate(category);

        if (categoryRepository.findByName(category.getName()) != null) {
            throw new RuntimeException("Kategorija sa tim imenom već postoji.");
        }

        return categoryRepository.create(category);
    }

    public Category update(Long id, Category category) {
        validate(category);

        Category existing = categoryRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("Kategorija ne postoji.");
        }

        Category sameName = categoryRepository.findByName(category.getName());
        if (sameName != null && !sameName.getId().equals(id)) {
            throw new RuntimeException("Kategorija sa tim imenom već postoji.");
        }

        return categoryRepository.update(id, category);
    }

    public void delete(Long id) {
        if (categoryRepository.findById(id) == null) {
            throw new RuntimeException("Kategorija ne postoji.");
        }

        if (categoryRepository.hasNews(id)) {
            throw new RuntimeException("Ne možeš obrisati kategoriju koja ima vesti.");
        }

        categoryRepository.delete(id);
    }

    private void validate(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new RuntimeException("Ime kategorije je obavezno.");
        }

        if (category.getDescription() == null || category.getDescription().trim().isEmpty()) {
            throw new RuntimeException("Opis kategorije je obavezan.");
        }
    }
}