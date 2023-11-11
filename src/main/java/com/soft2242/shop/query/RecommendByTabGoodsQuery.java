package com.soft2242.shop.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
public class RecommendByTabGoodsQuery extends Query {
    @Schema(description = "分类 tabId")
    private Integer subType;
}