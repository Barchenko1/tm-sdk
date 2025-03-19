package com.b2c.prototype.processor.item;

import com.b2c.prototype.modal.dto.payload.constant.CategoryDto;

import java.util.List;
import java.util.Map;

public interface ICategoryProcess {
    void createCategoryList(Map<String, String> requestParams, List<CategoryDto> categoryDtoList);
    void updateSingleCategory(Map<String, String> requestParams, CategoryDto categoryDto);
    void updateCategory(Map<String, String> requestParams, List<CategoryDto> categoryDtoList);
    void deleteCategory(Map<String, String> requestParams);

    CategoryDto getCategoryByCategoryName(Map<String, String> requestParams);
    List<CategoryDto> getAllFirstLineCategories(Map<String, String> requestParams);
}
