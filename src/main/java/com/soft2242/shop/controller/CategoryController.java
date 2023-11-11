package com.soft2242.shop.controller;

import com.soft2242.shop.common.result.PageResult;
import com.soft2242.shop.common.result.Result;
import com.soft2242.shop.entity.Category;
import com.soft2242.shop.query.Query;
import com.soft2242.shop.service.CategoryService;
import com.soft2242.shop.service.GoodsService;
import com.soft2242.shop.vo.CategoryVO;
import com.soft2242.shop.vo.RecommendGoodsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ycshang
 * @since 2023-11-07
 */
@Tag(name = "分类管理")
@RestController
@RequestMapping("category")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final GoodsService goodsService;

    @GetMapping("index")
    @Operation(summary = "首页-商品分类")
    public Result<List<Category>> getIndexCategoryList() {
        List<Category> list = categoryService.getIndexCategoryList();
        return Result.ok(list);
    }

    @GetMapping("top")
    @Operation(summary = "tab分类页-商品分类")
    public Result<List<CategoryVO>> getList() {
        List<CategoryVO> list = categoryService.getCategoryList();
        return Result.ok(list);
    }


}
