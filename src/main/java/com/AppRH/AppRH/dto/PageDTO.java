package com.AppRH.AppRH.dto;

import java.util.List;

public class PageDTO<T> {
    private List<?> content;
    private Object contentId;
    private long totalElements;

    public PageDTO() {
    }

    public PageDTO(Object contentId) {
        this.contentId = contentId;
    }

    public PageDTO(List<?> content, long totalElements) {
        this.content = content;
        this.totalElements = totalElements;
    }

    public List<?> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public Object getTotalContentId() {
        return contentId;
    }
}
