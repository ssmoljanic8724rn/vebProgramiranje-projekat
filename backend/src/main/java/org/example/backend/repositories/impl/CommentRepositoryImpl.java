package org.example.backend.repositories.impl;

import org.example.backend.dto.CreateCommentRequest;
import org.example.backend.dto.ReactionStats;
import org.example.backend.entities.Comment;
import org.example.backend.repositories.CommentRepository;
import org.example.backend.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentRepositoryImpl implements CommentRepository {

    @Override
    public List<Comment> findByNewsId(Long newsId) {
        List<Comment> comments = new ArrayList<>();

        String query =
                "SELECT * FROM comments " +
                        "WHERE news_id = ? " +
                        "ORDER BY created_at DESC";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, newsId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return comments;
    }

    @Override
    public Comment create(Long newsId, CreateCommentRequest request) {
        String query =
                "INSERT INTO comments(news_id, author_name, content) " +
                        "VALUES (?, ?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, newsId);
            statement.setString(2, request.getAuthorName());
            statement.setString(3, request.getContent());

            statement.executeUpdate();

            Long id = null;

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    id = keys.getLong(1);
                }
            }

            return findById(id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();

        comment.setId(rs.getLong("id"));
        comment.setNewsId(rs.getLong("news_id"));
        comment.setAuthorName(rs.getString("author_name"));
        comment.setContent(rs.getString("content"));
        comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        return comment;
    }
    @Override
    public Comment findById(Long id) {
        String query = "SELECT * FROM comments WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComment(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void reactToComment(Long commentId, String sessionId, String reaction) {
        String query =
                "INSERT INTO comment_reactions(comment_id, session_id, reaction) " +
                        "VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE reaction = VALUES(reaction)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, commentId);
            statement.setString(2, sessionId);
            statement.setString(3, reaction);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ReactionStats getCommentReactionStats(Long commentId) {
        String query =
                "SELECT " +
                        "SUM(CASE WHEN reaction = 'LIKE' THEN 1 ELSE 0 END) AS likes, " +
                        "SUM(CASE WHEN reaction = 'DISLIKE' THEN 1 ELSE 0 END) AS dislikes " +
                        "FROM comment_reactions " +
                        "WHERE comment_id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, commentId);

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
}