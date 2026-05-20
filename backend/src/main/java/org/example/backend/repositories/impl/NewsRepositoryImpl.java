package org.example.backend.repositories.impl;

import org.example.backend.dto.CreateNewsRequest;
import org.example.backend.entities.News;
import org.example.backend.repositories.NewsRepository;
import org.example.backend.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NewsRepositoryImpl implements NewsRepository {

    @Override
    public List<News> findAll() {
        List<News> newsList = new ArrayList<>();

        String query = "SELECT * FROM news ORDER BY created_at DESC";

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

    @Override
    public News create(CreateNewsRequest request, Long authorId) {
        String insertNews =
                "INSERT INTO news(title, content, author_id, category_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBUtil.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement =
                         connection.prepareStatement(insertNews, Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, request.getTitle());
                statement.setString(2, request.getContent());
                statement.setLong(3, authorId);
                statement.setLong(4, request.getCategoryId());

                statement.executeUpdate();

                Long newsId;

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new RuntimeException("Nije moguće kreirati vest.");
                    }

                    newsId = keys.getLong(1);
                }

                if (request.getTags() != null) {
                    for (String tagName : request.getTags()) {
                        if (tagName == null || tagName.trim().isEmpty()) {
                            continue;
                        }

                        Long tagId = findOrCreateTag(connection, tagName.trim());
                        linkNewsAndTag(connection, newsId, tagId);
                    }
                }

                connection.commit();

                return findById(newsId);

            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException(e);
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM news WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Long findOrCreateTag(Connection connection, String tagName) throws SQLException {
        String findQuery = "SELECT id FROM tags WHERE name = ?";

        try (PreparedStatement statement = connection.prepareStatement(findQuery)) {
            statement.setString(1, tagName);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }

        String insertQuery = "INSERT INTO tags(name) VALUES (?)";

        try (PreparedStatement statement =
                     connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, tagName);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }

        throw new RuntimeException("Nije moguće kreirati tag.");
    }

    private void linkNewsAndTag(Connection connection, Long newsId, Long tagId) throws SQLException {
        String query = "INSERT IGNORE INTO news_tags(news_id, tag_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, newsId);
            statement.setLong(2, tagId);
            statement.executeUpdate();
        }
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

        return news;
    }

    @Override
    public News update(Long id, CreateNewsRequest request) {
        String updateNews =
                "UPDATE news SET title = ?, content = ?, category_id = ? WHERE id = ?";

        try (Connection connection = DBUtil.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(updateNews)) {
                statement.setString(1, request.getTitle());
                statement.setString(2, request.getContent());
                statement.setLong(3, request.getCategoryId());
                statement.setLong(4, id);

                statement.executeUpdate();

                deleteTagsForNews(connection, id);

                if (request.getTags() != null) {
                    for (String tagName : request.getTags()) {
                        if (tagName == null || tagName.trim().isEmpty()) {
                            continue;
                        }

                        Long tagId = findOrCreateTag(connection, tagName.trim());
                        linkNewsAndTag(connection, id, tagId);
                    }
                }

                connection.commit();

                return findById(id);

            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException(e);
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void deleteTagsForNews(Connection connection, Long newsId) throws SQLException {
        String query = "DELETE FROM news_tags WHERE news_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, newsId);
            statement.executeUpdate();
        }
    }

    @Override
    public List<News> search(String query, int page, int pageSize) {
        List<News> newsList = new ArrayList<>();

        String sql = "SELECT * FROM news " +
                "WHERE title LIKE ? OR content LIKE ? " +
                "ORDER BY created_at DESC " +
                "LIMIT ? OFFSET ?";

        int offset = (page - 1) * pageSize;

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String searchPattern = "%" + query + "%";

            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
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

}