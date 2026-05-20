package org.example.backend.dto;

public class CreateCommentRequest {
    private String authorName;
    private String content;

    public CreateCommentRequest() {}

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}