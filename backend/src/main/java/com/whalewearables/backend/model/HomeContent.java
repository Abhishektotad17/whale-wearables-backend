package com.whalewearables.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "home_content")
public class HomeContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String heading;
    private String highlightText;
    @Column(columnDefinition = "TEXT")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getHighlightText() {
        return highlightText;
    }

    public void setHighlightText(String highlightText) {
        this.highlightText = highlightText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
