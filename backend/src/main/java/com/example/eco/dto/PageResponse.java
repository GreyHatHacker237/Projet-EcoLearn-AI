package com.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    
    public static <T> PageResponse<T> of(
            List<T> content,
            int pageNumber,
            int pageSize,
            long totalElements) {
        
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        
        return new PageResponse<>(
            content,
            pageNumber,
            pageSize,
            totalElements,
            totalPages,
            pageNumber == 0,
            pageNumber >= totalPages - 1
        );
    }
}