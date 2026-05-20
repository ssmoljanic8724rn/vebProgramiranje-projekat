package org.example.backend.services;

import org.example.backend.dto.CreateCommentRequest;
import org.example.backend.dto.ReactionStats;
import org.example.backend.entities.Comment;
import org.example.backend.repositories.CommentRepository;

import javax.inject.Inject;
import java.util.List;

public class CommentService {

    private final CommentRepository commentRepository;

    @Inject
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> findByNewsId(Long newsId) {
        return commentRepository.findByNewsId(newsId);
    }

    public Comment create(Long newsId, CreateCommentRequest request) {
        if (request.getAuthorName() == null || request.getAuthorName().trim().isEmpty()) {
            throw new RuntimeException("Ime autora komentara je obavezno.");
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("Tekst komentara je obavezan.");
        }

        return commentRepository.create(newsId, request);
    }

    public void reactToComment(Long commentId, String sessionId, String reaction) {
        Comment comment = commentRepository.findById(commentId);

        if (comment == null) {
            throw new RuntimeException("Komentar ne postoji.");
        }

        if (!"LIKE".equals(reaction) && !"DISLIKE".equals(reaction)) {
            throw new RuntimeException("Reakcija mora biti LIKE ili DISLIKE.");
        }

        commentRepository.reactToComment(commentId, sessionId, reaction);
    }

    public ReactionStats getCommentReactionStats(Long commentId) {
        if (commentRepository.findById(commentId) == null) {
            throw new RuntimeException("Komentar ne postoji.");
        }

        return commentRepository.getCommentReactionStats(commentId);
    }
}
