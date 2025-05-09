package com.jobfit.server.infras.challenge;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jobfit.server.domain.challenge.Challenge;
import com.jobfit.server.domain.challenge.ChallengeStatus;

public interface ChallengeJpaRepository extends JpaRepository<Challenge, Long> {

	List<Challenge> findAllByUserId(Long userId);

	Optional<Challenge> findByUserIdAndRecruitId(Long userId, Long recruitId);

	@Query("""
	SELECT new com.jobfit.server.infras.challenge.ChallengeListDto(
		c.id, c.userId, r.id, r.title, r.companyName, r.endDate, c.progress
	)
	from Challenge c
	LEFT JOIN Recruit r ON c.recruitId = r.id
	WHERE c.userId = :userId AND c.status = 'ACTIVE'
	""")
	List<ChallengeListDto> findAllByUserIdWithRecruitId(Long userId);

	@Query("""
	SELECT new com.jobfit.server.infras.challenge.ChallengeDetailDto(
		c.id, c.userId, r.id, r.title, r.companyName, r.registerDate, c.progress, r.careerType, c.strengths, c.weaknesses, c.strengthsPoint, c.weaknessesPoint, c.improvements, r.content
	)
	from Challenge c
	LEFT JOIN Recruit r ON c.recruitId = r.id
	WHERE c.id = :challengeId
	""")
	Optional<ChallengeDetailDto> findByIdWithRecruit(Long challengeId);

	List<Challenge> findAllByStatus(ChallengeStatus challengeStatus);
}
