package org.example.backend.repositories;

import org.example.backend.dto.CreateCommentRequest;
import org.example.backend.dto.ReactionStats;
import org.example.backend.entities.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> findByNewsId(Long newsId);
    Comment create(Long newsId, CreateCommentRequest request);
    void reactToComment(Long commentId, String sessionId, String reaction);
    ReactionStats getCommentReactionStats(Long commentId);
    Comment findById(Long id);
}