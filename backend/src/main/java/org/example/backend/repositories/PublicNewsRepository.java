package org.example.backend.repositories;

import org.example.backend.dto.ReactionStats;
import org.example.backend.entities.News;

import java.util.List;

public interface PublicNewsRepository {
    List<News> findLatest();
    News findById(Long id);
    void incrementVisitCount(Long newsId);
    boolean hasUserViewedNews(Long newsId, String sessionId);
    void saveView(Long newsId, String sessionId);
    List<News> findMostRead();
    List<News> findByCategory(Long categoryId, int page, int pageSize);
    List<News> search(String query, int page, int pageSize);
    List<News> findByTag(String tagName, int page, int pageSize);
    void reactToNews(Long newsId, String sessionId, String reaction);
    ReactionStats getNewsReactionStats(Long newsId);
    List<News> findMostReacted();
    List<News> findRelatedNews(Long newsId);
}