package com.jobfit.server.infras.recruit;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.jobfit.server.domain.recruit.Category;
import com.jobfit.server.domain.recruit.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Category save(Category category) {
        return categoryJpaRepository.save(category);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryJpaRepository.findById(id);
    }
}