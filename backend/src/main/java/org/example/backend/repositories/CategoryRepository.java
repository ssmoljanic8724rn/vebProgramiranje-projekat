package org.example.backend.repositories;

import org.example.backend.entities.Category;
import java.util.List;

public interface CategoryRepository {
    List<Category> findAll();
    Category findById(Long id);
    Category findByName(String name);
    Category create(Category category);
    Category update(Long id, Category category);
    void delete(Long id);
    boolean hasNews(Long categoryId);
}