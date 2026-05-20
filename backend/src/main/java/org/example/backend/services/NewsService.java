package org.example.backend.services;

import org.example.backend.dto.CreateNewsRequest;
import org.example.backend.entities.News;
import org.example.backend.repositories.NewsRepository;

import javax.inject.Inject;
import java.util.List;

public class NewsService {

    private final NewsRepository newsRepository;

    @Inject
    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<News> findAll() {
        return newsRepository.findAll();
    }

    public News create(CreateNewsRequest request, Long authorId) {
        validate(request);
        return newsRepository.create(request, authorId);
    }

    public void delete(Long id, Long currentUserId, String role) {
        News existing = newsRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("Vest ne postoji.");
        }

        if (!role.equals("ADMIN") && !existing.getAuthorId().equals(currentUserId)) {
            throw new RuntimeException("Nemate dozvolu da obrišete ovu vest.");
        }

        newsRepository.delete(id);
    }

    public News update(Long id, CreateNewsRequest request, Long currentUserId, String role) {
        validate(request);

        News existing = newsRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("Vest ne postoji.");
        }

        if (!role.equals("ADMIN") && !existing.getAuthorId().equals(currentUserId)) {
            throw new RuntimeException("Nemate dozvolu da menjate ovu vest.");
        }

        return newsRepository.update(id, request);
    }

    public List<News> search(String query, int page, int pageSize) {
        if (query == null || query.trim().isEmpty()) {
            throw new RuntimeException("Tekst pretrage je obavezan.");
        }

        if (page < 1) {
            page = 1;
        }

        if (pageSize <= 0) {
            pageSize = 10;
        }

        return newsRepository.search(query, page, pageSize);
    }

    private void validate(CreateNewsRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Naslov je obavezan.");
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("Tekst vesti je obavezan.");
        }

        if (request.getCategoryId() == null) {
            throw new RuntimeException("Kategorija je obavezna.");
        }
    }

}