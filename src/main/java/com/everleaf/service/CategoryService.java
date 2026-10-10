package com.everleaf.service;

import com.everleaf.dao.CategoryDAO;
import com.everleaf.dao.CategoryDAOImpl;
import com.everleaf.model.Category;

public class CategoryService {
    private final CategoryDAO categoryDAO = new CategoryDAOImpl();

    public void createDefaultCategories(int userId) {
        String[] defaults = {"Academic", "Personal", "Financial", "Office", "Other"};
        for (String catName : defaults) {
            Category cat = new Category();
            cat.setUserId(userId);
            cat.setCategoryName(catName);
            categoryDAO.createCategory(cat);
        }
    }
}
