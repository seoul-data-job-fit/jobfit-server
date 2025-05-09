package com.jobfit.server.interfaces.scheduler.challenge;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jobfit.server.service.challenge.ChallengeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChallengeScheduler {

	private final ChallengeService challengeService;

	@Scheduled(cron = "0 */5 * * * *")
	public void runChallengeJob() throws JsonProcessingException {
		log.info("Running challenge job");
		challengeService.challenges();
	}
}
