package com.jobfit.server.service.challenge;

import lombok.Data;

@Data
public class EvaluationResult {
	private Long recruitId;
	private Long progress;
	private String strengths;
	private String weaknesses;
	private String strengthsPoint;
	private String weaknessesPoint;
	private String improvements;
}
