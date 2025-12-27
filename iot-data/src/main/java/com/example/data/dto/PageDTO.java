package com.example.data.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageDTO<T> {
    private long total;
    private List<T> list;

    public PageDTO(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }
}