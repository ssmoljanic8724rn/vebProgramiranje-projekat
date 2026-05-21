package org.example.backend.repositories.impl;

import org.example.backend.dto.ReactionStats;
import org.example.backend.entities.News;
import org.example.backend.repositories.PublicNewsRepository;
import org.example.backend.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PublicNewsRepositoryImpl implements PublicNewsRepository {

    @Override
    public List<News> findLatest() {
        List<News> newsList = new ArrayList<>();

        String query = "SELECT * FROM news ORDER BY created_at DESC LIMIT 10";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                newsList.add(mapResultSetToNews(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newsList;
    }

    @Override
    public News findById(Long id) {
        String query = "SELECT * FROM news WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNews(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private News mapResultSetToNews(ResultSet rs) throws SQLException {
        News news = new News();

        news.setId(rs.getLong("id"));
        news.setTitle(rs.getString("title"));
        news.setContent(rs.getString("content"));
        news.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        news.setVisitCount(rs.getInt("visit_count"));
        news.setAuthorId(rs.getLong("author_id"));
        news.setCategoryId(rs.getLong("category_id"));
        news.setTags(findTagsForNews(news.getId()));
        news.setAuthorName(findAuthorName(news.getAuthorId()));

        return news;
    }

    @Override
    public void incrementVisitCount(Long newsId) {

        String query =
                "UPDATE news SET visit_count = visit_count + 1 WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, newsId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasUserViewedNews(Long newsId, String sessionId) {

        String query =
                "SELECT COUNT(*) FROM news_views " +
                        "WHERE news_id = ? AND session_id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, newsId);
            statement.setString(2, sessionId);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    public void saveView(Long newsId, String sessionId) {

        String query =
                "INSERT INTO news_views(news_id, session_id) VALUES (?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, newsId);
            statement.setString(2, sessionId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<News> findMostRead() {
        List<News> newsList = new ArrayList<>();

        String query =
                "SELECT * FROM news " +
                        "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
                        "ORDER BY visit_count DESC, created_at DESC " +
                        "LIMIT 10";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                newsList.add(mapResultSetToNews(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newsList;
    }

    @Override
    public List<News> findByCategory(Long categoryId, int page, int pageSize) {
        List<News> newsList = new ArrayList<>();

        String sql = "SELECT * FROM news " +
                "WHERE category_id = ? " +
                "ORDER BY created_at DESC " +
                "LIMIT ? OFFSET ?";

        int offset = (page - 1) * pageSize;

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, categoryId);
            statement.setInt(2, pageSize);
            statement.setInt(3, offset);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    newsList.add(mapResultSetToNews(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newsList;
    }

    @Override
    public List<News> search(String query, int page, int pageSize) {
        List<News> newsList = new ArrayList<>();

        String sql = "SELECT * FROM news " +
                "WHERE title LIKE ? OR content LIKE ? " +
                "ORDER BY created_at DESC " +
                "LIMIT ? OFFSET ?";

        int offset = (page - 1) * pageSize;
        String pattern = "%" + query + "%";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pattern);
            statement.setString(2, pattern);
            statement.setInt(3, pageSize);
            statement.setInt(4, offset);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    newsList.add(mapResultSetToNews(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newsList;
    }

    @Override
    public List<News> findByTag(String tagName, int page, int pageSize) {
        List<News> newsList = new ArrayList<>();

        String sql =
                "SELECT n.* FROM news n " +
                        "JOIN news_tags nt ON n.id = nt.news_id " +
                        "JOIN tags t ON nt.tag_id = t.id " +
                        "WHERE t.name = ? " +
                        "ORDER BY n.created_at DESC " +
                        "LIMIT ? OFFSET ?";

        int offset = (page - 1) * pageSize;

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, tagName);
            statement.setInt(2, pageSize);
            statement.setInt(3, offset);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    newsList.add(mapResultSetToNews(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newsList;
    }

    @Override
    public void reactToNews(Long newsId, String sessionId, String reaction) {
        String query =
                "INSERT INTO news_reactions(news_id, session_id, reaction) " +
                        "VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE reaction = VALUES(reaction)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, newsId);
            statement.setString(2, sessionId);
            statement.setString(3, reaction);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ReactionStats getNewsReactionStats(Long newsId) {
        String query =
                "SELECT " +
                        "SUM(CASE WHEN reaction = 'LIKE' THEN 1 ELSE 0 END) AS likes, " +
                        "SUM(CASE WHEN reaction = 'DISLIKE' THEN 1 ELSE 0 END) AS dislikes " +
                        "FROM news_reactions " +
                        "WHERE news_id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, newsId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new ReactionStats(
                            rs.getInt("likes"),
                            rs.getInt("dislikes")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new ReactionStats(0, 0);
    }

    @Override
    public List<News> findMostReacted() {
        List<News> newsList = new ArrayList<>();

        String query =
                "SELECT n.* FROM news n " +
                        "LEFT JOIN news_reactions nr ON n.id = nr.news_id " +
                        "GROUP BY n.id " +
                        "ORDER BY COUNT(nr.id) DESC, n.created_at DESC " +
                        "LIMIT 3";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                newsList.add(mapResultSetToNews(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newsList;
    }

    @Override
    public List<News> findRelatedNews(Long newsId) {

        List<News> newsList = new ArrayList<>();

        String query =
                "SELECT DISTINCT n.* " +
                        "FROM news n " +
                        "JOIN news_tags nt ON n.id = nt.news_id " +
                        "WHERE nt.tag_id IN ( " +
                        "   SELECT tag_id FROM news_tags WHERE news_id = ? " +
                        ") " +
                        "AND n.id != ? " +
                        "LIMIT 3";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, newsId);
            statement.setLong(2, newsId);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    newsList.add(mapResultSetToNews(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newsList;
    }

    private List<String> findTagsForNews(Long newsId) {
        List<String> tags = new ArrayList<>();

        String query =
                "SELECT t.name FROM tags t " +
                        "JOIN news_tags nt ON t.id = nt.tag_id " +
                        "WHERE nt.news_id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, newsId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    tags.add(rs.getString("name"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tags;
    }

    private String findAuthorName(Long authorId) {

        String query =
                "SELECT first_name, last_name " +
                        "FROM users " +
                        "WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setLong(1, authorId);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {

                    return rs.getString("first_name")
                            + " "
                            + rs.getString("last_name");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}