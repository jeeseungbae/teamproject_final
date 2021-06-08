package com.finalproject.shelter.business.service.logic;

import com.finalproject.shelter.domain.model.entity.noticationDomain.Category;
import com.finalproject.shelter.domain.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryLogicService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> read(Long id) {
        return categoryRepository.findCategoryByCategorytableId(id);
    }

    public Category findId(Long id){
        return categoryRepository.getOne(id);
    }
}
