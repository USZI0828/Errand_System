package com.arrend_system.pojo.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BaseQuery {
    @Schema(description = "当前页",example = "1")
    private Integer currentPage;
    @Schema(description = "当前页大小",example = "10")
    private Integer pageSize;
}
