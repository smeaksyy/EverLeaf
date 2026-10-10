package com.everleaf.dao;

import com.everleaf.model.Category;
import java.util.List;

public interface CategoryDAO {
    boolean createCategory(Category category);
    List<Category> getCategoriesByUser(int userId);
    Category getCategoryById(int categoryId);
    boolean updateCategory(int categoryId, String newName, int userId);
    boolean deleteCategory(int categoryId, int userId);
    int getCategoryCountByUser(int userId);
}
