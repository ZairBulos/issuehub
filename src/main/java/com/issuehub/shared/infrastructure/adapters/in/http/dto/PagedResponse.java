package com.issuehub.shared.infrastructure.adapters.in.http.dto;

import java.util.List;

public record PagedResponse<T>(
        List<T> items,
        int page,
        int pageSize,
        int totalItems
) {

    public static <T> PagedResponse<T> of(List<T> items, int page, int pageSize) {
        return new PagedResponse<>(items, page, pageSize, items.size());
    }

}
