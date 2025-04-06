package com.ecom.service;

import java.util.List;
import org.springframework.data.domain.Page;

import com.ecom.model.Category;

public interface CategoryService {

    Category saveCategory(Category category);

    Boolean existCategory(String name);

    List<Category> getAllCategory();

    Boolean deleteCategory(int id);

    Category getCategoryById(int id);

    /**
     * Trả về danh sách category theo phân trang.
     * @param pageNo
     * @param pageSize
     * @return Page<Category>
     */
    Page<Category> getAllCategoryPagination(Integer pageNo, Integer pageSize);
}
