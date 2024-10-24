package com.intuit.craft.photographer.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedResponse <T> {
    private List<T> content;  // The list of items on the current page
    private int page;          // The current page number
    private int totalPages;    // The total number of pages
    private long totalElements; // The total number of elements

    public PaginatedResponse(List<T> content, int page, int totalPages, long totalElements) {
        this.content = content;
        this.page = page;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
