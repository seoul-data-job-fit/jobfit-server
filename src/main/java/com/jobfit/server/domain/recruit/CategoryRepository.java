package com.jobfit.server.domain.recruit;

import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(Long id);
}
