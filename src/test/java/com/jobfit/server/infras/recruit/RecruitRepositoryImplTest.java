package com.jobfit.server.infras.recruit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.jobfit.server.domain.recruit.Category;
import com.jobfit.server.domain.recruit.CategoryRepository;
import com.jobfit.server.domain.recruit.Recruit;
import com.jobfit.server.domain.recruit.RecruitRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class RecruitRepositoryImplTest {

    @Autowired
    private RecruitRepository recruitRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Test
    void testFindAll() {
        // given
        Category category = createAndSaveCategory();
        Recruit recruit1 = createAndSaveRecruit("네이버", category);
        Recruit recruit2 = createAndSaveRecruit("카카오", category);
        
        // when
        List<Recruit> recruits = recruitRepository.findAll();
        
        // then
        assertThat(recruits).isNotEmpty();
        assertThat(recruits.size()).isGreaterThanOrEqualTo(2);
        assertThat(recruits).anyMatch(r -> r.getId().equals(recruit1.getId()));
        assertThat(recruits).anyMatch(r -> r.getId().equals(recruit2.getId()));
    }

    @Test
    void testSave() {
        // given
        Category category = createAndSaveCategory();
        
        // when
        Recruit recruit = new Recruit(
            category,
            "(주)네이버",
            "Java 백엔드 개발자 모집",
            "N-2023-001",
            "대졸 이상",
            "정규직",
            "서울 강남구 테헤란로 152",
            "Spring Boot 기반의 RESTful API 개발 및 운영",
            "경력",
            "월 300만원",
            "9:00 ~ 18:00",
            "정규직",
            "주 5일",
            "40시간",
            "4대보험",
            "서류전형, 코딩테스트, 기술면접",
            "이메일 접수",
            "이력서, 자기소개서",
            "김채용",
            "02-1234-5678",
            "인사팀",
            "서울 강남구 테헤란로 152",
            "Java 백엔드 개발자 채용",
            "https://recruit.jobfit.com/backend-dev",
            LocalDate.of(2023, 4, 1),
            LocalDate.of(2023, 4, 30)
        );
        
        Recruit savedRecruit = recruitRepository.save(recruit);
        
        // then
        assertThat(savedRecruit.getId()).isNotNull();
        assertThat(savedRecruit.getCompanyName()).isEqualTo("(주)네이버");
        assertThat(savedRecruit.getTitle()).isEqualTo("Java 백엔드 개발자 채용");
        
        // 데이터베이스에서 다시 조회하여 확인
        Recruit foundRecruit = recruitRepository.findById(savedRecruit.getId()).orElseThrow();
        assertThat(foundRecruit.getId()).isEqualTo(savedRecruit.getId());
        assertThat(foundRecruit.getCompanyName()).isEqualTo("(주)네이버");
        assertThat(foundRecruit.getJobType()).isEqualTo("정규직");
        assertThat(foundRecruit.getRegisterDate()).isEqualTo(LocalDate.of(2023, 4, 1));
        assertThat(foundRecruit.getEndDate()).isEqualTo(LocalDate.of(2023, 4, 30));
        assertThat(foundRecruit.getCategory()).isEqualTo(category);
    }
    
    @Test
    void testFindByCategory() {
        // given
        Category category1 = createCategory("백엔드 개발자", "서울 강남구");
        Category category2 = createCategory("프론트엔드 개발자", "경기도 성남시");
        
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        
        createAndSaveRecruit("네이버", category1);
        createAndSaveRecruit("라인", category1);
        createAndSaveRecruit("카카오", category2);
        
        // when
        List<Recruit> recruitsCategory1 = recruitRepository.findByCategory(category1);
        List<Recruit> recruitsCategory2 = recruitRepository.findByCategory(category2);
        
        // then
        assertThat(recruitsCategory1).hasSize(2);
        assertThat(recruitsCategory1).allMatch(r -> r.getCategory().equals(category1));
        
        assertThat(recruitsCategory2).hasSize(1);
        assertThat(recruitsCategory2).allMatch(r -> r.getCategory().equals(category2));
    }
    
    private Category createAndSaveCategory() {
        Category category = createCategory("백엔드 개발자", "서울 강남구");
        return categoryRepository.save(category);
    }
    
    private Category createCategory(String position, String location) {
        return new Category(
            "월 300만원",
            position,
            "3년 이상",
            2,
            location
        );
    }
    
    private Recruit createAndSaveRecruit(String companyName, Category category) {
        Recruit recruit = new Recruit(
            category,
            companyName,
            "개발자 모집",
            "R-2023-001",
            "대졸 이상",
            "정규직",
            "서울 강남구",
            "개발 업무",
            "경력",
            "월 300만원",
            "9:00 ~ 18:00",
            "정규직",
            "주 5일",
            "40시간",
            "4대보험",
            "서류전형, 면접",
            "이메일 접수",
            "이력서, 자기소개서",
            "채용담당자",
            "02-1234-5678",
            "인사팀",
            "서울 강남구",
            companyName + " 개발자 채용",
            "https://recruit.jobfit.com",
            LocalDate.of(2023, 4, 1),
            LocalDate.of(2023, 4, 30)
        );
        
        return recruitRepository.save(recruit);
    }
}
