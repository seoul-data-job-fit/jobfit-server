package com.jobfit.server.infras.recruit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.jobfit.server.domain.recruit.Category;
import com.jobfit.server.domain.recruit.CategoryRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class CategoryRepositoryImplTest {

  @Autowired
  private CategoryRepository categoryRepository;

  @Test
  void testSave() {
    // given
    Category category1 = new Category(
        "월 300만원",
        "백엔드 개발자",
        "3년 이상",
        2,
        "서울 강남구"
    );

    Category category2 = new Category(
        "연봉 5000만원",
        "프론트엔드 개발자",
        "신입",
        1,
        "경기도 성남시"
    );

    Category category3 = new Category(
        "월 350만원",
        "데이터 엔지니어",
        "5년 이상",
        3,
        "서울 마포구"
    );

    // when
    Category savedCategory1 = categoryRepository.save(category1);
    Category savedCategory2 = categoryRepository.save(category2);
    Category savedCategory3 = categoryRepository.save(category3);

    // then
    assertThat(savedCategory1.getId()).isNotNull();
    assertThat(savedCategory1.getWages()).isEqualTo("월 300만원");
    assertThat(savedCategory1.getPosition()).isEqualTo("백엔드 개발자");
    
    assertThat(savedCategory2.getId()).isNotNull();
    assertThat(savedCategory2.getWages()).isEqualTo("연봉 5000만원");
    assertThat(savedCategory2.getPosition()).isEqualTo("프론트엔드 개발자");
    
    assertThat(savedCategory3.getId()).isNotNull();
    assertThat(savedCategory3.getWages()).isEqualTo("월 350만원");
    assertThat(savedCategory3.getPosition()).isEqualTo("데이터 엔지니어");
  }
}
