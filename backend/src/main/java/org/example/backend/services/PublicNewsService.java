package org.example.backend.services;

import org.example.backend.dto.ReactionStats;
import org.example.backend.entities.News;
import org.example.backend.repositories.PublicNewsRepository;

import javax.inject.Inject;
import java.util.List;

public class PublicNewsService {

    private final PublicNewsRepository publicNewsRepository;

    @Inject
    public PublicNewsService(PublicNewsRepository publicNewsRepository) {
        this.publicNewsRepository = publicNewsRepository;
    }

    public List<News> findLatest() {
        return publicNewsRepository.findLatest();
    }

    public News findById(Long id, String sessionId) {

        News news = publicNewsRepository.findById(id);

        if (news == null) {
            throw new RuntimeException("Vest ne postoji.");
        }

        boolean alreadyViewed =
                publicNewsRepository.hasUserViewedNews(id, sessionId);

        if (!alreadyViewed) {

            publicNewsRepository.saveView(id, sessionId);

            publicNewsRepository.incrementVisitCount(id);

            news.setVisitCount(news.getVisitCount() + 1);
        }

        return news;
    }

    public List<News> findMostRead() {
        return publicNewsRepository.findMostRead();
    }

    public List<News> findByCategory(Long categoryId, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize <= 0) pageSize = 10;

        return publicNewsRepository.findByCategory(categoryId, page, pageSize);
    }

    public List<News> search(String query, int page, int pageSize) {
        if (query == null || query.trim().isEmpty()) {
            throw new RuntimeException("Tekst pretrage je obavezan.");
        }

        if (page < 1) page = 1;
        if (pageSize <= 0) pageSize = 10;

        return publicNewsRepository.search(query.trim(), page, pageSize);
    }

    public List<News> findByTag(String tagName, int page, int pageSize) {
        if (tagName == null || tagName.trim().isEmpty()) {
            throw new RuntimeException("Tag je obavezan.");
        }

        if (page < 1) page = 1;
        if (pageSize <= 0) pageSize = 10;

        return publicNewsRepository.findByTag(tagName.trim(), page, pageSize);
    }

    public void reactToNews(Long newsId, String sessionId, String reaction) {
        News news = publicNewsRepository.findById(newsId);

        if (news == null) {
            throw new RuntimeException("Vest ne postoji.");
        }

        if (!"LIKE".equals(reaction) && !"DISLIKE".equals(reaction)) {
            throw new RuntimeException("Reakcija mora biti LIKE ili DISLIKE.");
        }

        publicNewsRepository.reactToNews(newsId, sessionId, reaction);
    }

    public ReactionStats getNewsReactionStats(Long newsId) {
        if (publicNewsRepository.findById(newsId) == null) {
            throw new RuntimeException("Vest ne postoji.");
        }

        return publicNewsRepository.getNewsReactionStats(newsId);
    }
}