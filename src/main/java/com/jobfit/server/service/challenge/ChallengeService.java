package com.jobfit.server.service.challenge;

import static com.jobfit.server.support.exception.BusinessError.*;

import java.util.List;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobfit.server.domain.challenge.Challenge;
import com.jobfit.server.domain.challenge.ChallengeRepository;
import com.jobfit.server.domain.challenge.ChallengeStatus;
import com.jobfit.server.domain.recruit.Recruit;
import com.jobfit.server.domain.recruit.RecruitRepository;
import com.jobfit.server.domain.skill.UserSkill;
import com.jobfit.server.domain.skill.UserSkillRepository;
import com.jobfit.server.infras.challenge.ChallengeDetailDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeService {

	private final OpenAiChatModel chatModel;
	private final ObjectMapper objectMapper;
	private final RecruitRepository recruitRepository;
	private final UserSkillRepository userSkillRepository;
	private final ChallengeRepository challengeRepository;

	@Transactional
	public ChallengeInfo createChallenge(CreateChallengeCommand command) {

		challengeRepository.findByUserIdAndRecruitId(command.getUserId(), command.getRecruitId())
			.ifPresent(challenge -> {
				throw ALREADY_CHALLENGE_RECRUIT.exception();
			});

		Challenge challenge = new Challenge(command.getUserId(), command.getRecruitId(), 0L, null, null, ChallengeStatus.PENDING);

		challengeRepository.save(challenge);
		return ChallengeInfo.from(challenge);
	}

	@Transactional
	public ChallengeInfo deleteChallenge(Long challengeId) {
		Challenge challenge = challengeRepository.findById(challengeId)
			.orElseThrow(NOT_FOUND_CHALLENGE_ERROR::exception);
		challengeRepository.delete(challenge);
		return ChallengeInfo.from(challenge);
	}

	@Transactional
	public List<ChallengeListInfo> getChallenges(Long userId) {
		return challengeRepository.findAllByUserId(userId).stream()
			.map(ChallengeListInfo::from)
			.toList();
	}

	@Transactional
	public ChallengeDetailInfo getChallenge(Long challengeId) {
		ChallengeDetailDto dto = challengeRepository.findByIdWithRecruit(challengeId)
			.orElseThrow();
		return ChallengeDetailInfo.from(dto);
	}

	// 챌린지 스케줄러
	@Transactional
	public void challenges() throws JsonProcessingException  {

		List<Challenge> pendingChallenge = challengeRepository.findAllByStatus(ChallengeStatus.PENDING);

		for (int i = 0; i < pendingChallenge.size(); i++) {
			Challenge challenge = pendingChallenge.get(i);
			UserSkill userSkill = userSkillRepository.findByUserId(challenge.getUserId()).orElseThrow();
			Recruit recruit = recruitRepository.findById(challenge.getRecruitId()).orElseThrow();
			String prompt = getPrompt(recruit, userSkill);

			ChatResponse response = chatModel.call(
				new Prompt(prompt,
					OpenAiChatOptions.builder()
						.model("gpt-4o")
						.temperature(0.3)
						.build()
				)
			);

			String content = response.getResult().getOutput().getText().trim();
			if (content.startsWith("```json")) {
				content = content.replaceAll("(?s)```json\\s*", "").replaceAll("```\\s*", "").trim();
			}
			challenge.apply(objectMapper.readValue(content, EvaluationResult.class));
		}

	}

	private String getPrompt(Recruit recruit, UserSkill skills) throws JsonProcessingException {
		return String.format("""
			아래 채용공고와 사용자 스펙 정보를 바탕으로,
			해당 사용자가 이 채용공고에 얼마나 부합하는지를 분석해주세요.
			
			1. 결과는 반드시 아래 JSON 형식으로만 응답하세요:
			{
			  "recruitId": <채용공고 ID>,
			  "progress": <0~100 사이 정수>,
			  "strengths": "<충족 요약>",
			  "weaknesses": "<미흡 요약>",
			  "strengthsPoint": "<강점에 대한 자세한 설명>",
			  "weaknessesPoint": "<약점에 대한 자세한 설명>",
			  "improvements": "<부족한 부분을 보완하기 위한 개선방안>",
			}
			
			2. 항목 설명:
			- progress는 채용공고 전체내용이 지원자에게 얼마나 가능성이 있는지 관련업무의 경력이 있는지 객관적으로 판단하여 0 ~ 100 사이의 점수를 작성해주세요.
			- strengths, weaknesses는 단어를 콤마로 구분하여 작성해주세요. (예: 자격증소지, 경력충분)
			- strengths_point는 지원자의 강점을 간단히 요약해서 작성해주세요.
			- weaknesses_point는 지원자의 약점을 간단히 요약해서 작성해주세요.
			- improvements는 지원자가 보완해야 할 점과 준비해야 할 사항을 300자 이내로 요약해주세요.
			
			3. 다음은 채용공고입니다:
			%s
			
			4. 다음은 사용자 정보입니다:
			%s
			""", objectMapper.writeValueAsString(recruit), objectMapper.writeValueAsString(skills)
		);

	}
}
