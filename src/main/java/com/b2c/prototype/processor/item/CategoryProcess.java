package com.b2c.prototype.processor.item;

import com.b2c.prototype.manager.item.ICategoryManager;
import com.b2c.prototype.modal.dto.payload.constant.CategoryDto;

import java.util.List;
import java.util.Map;

public class CategoryProcess implements ICategoryProcess {

    private final ICategoryManager categoryManager;

    public CategoryProcess(ICategoryManager categoryManager) {
        this.categoryManager = categoryManager;
    }

    @Override
    public void createCategoryList(Map<String, String> requestParams, List<CategoryDto> categoryDtoList) {
        categoryManager.saveCategoryList(categoryDtoList);
    }

    @Override
    public void updateSingleCategory(Map<String, String> requestParams, CategoryDto categoryDto) {
        String categoryName = requestParams.get("category");
        categoryManager.updateSingleCategory(categoryDto);
    }

    @Override
    public void updateCategory(Map<String, String> requestParams, List<CategoryDto> categoryDtoList) {
        String categoryName = requestParams.get("category");
        categoryManager.updateCategory(categoryDtoList);
    }

    @Override
    public void deleteCategory(Map<String, String> requestParams) {
        String categoryName = requestParams.get("category");
        categoryManager.deleteCategory(categoryName);
    }

    @Override
    public CategoryDto getCategoryByCategoryName(Map<String, String> requestParams) {
        String categoryName = requestParams.get("category");
        return categoryManager.getCategoryByCategoryName(categoryName);
    }

    @Override
    public List<CategoryDto> getAllFirstLineCategories(Map<String, String> requestParams) {
        return categoryManager.getAllFirstLineCategories();
    }

}
