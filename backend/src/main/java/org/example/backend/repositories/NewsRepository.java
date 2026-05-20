package org.example.backend.repositories;

import org.example.backend.dto.CreateNewsRequest;
import org.example.backend.entities.News;

import java.util.List;

public interface NewsRepository {
    List<News> findAll();
    News findById(Long id);
    News create(CreateNewsRequest request, Long authorId);
    void delete(Long id);
    News update(Long id, CreateNewsRequest request);
    List<News> search(String query, int page, int pageSize);

}