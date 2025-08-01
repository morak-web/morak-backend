package com.xhae.morak.service

import com.xhae.morak.dto.AIFeedbackResponse
import com.xhae.morak.repository.AIFeedbackRepository
import org.springframework.stereotype.Service


@Service
class AIService(
    private val aiFeedbackRepository: AIFeedbackRepository // 별도 테이블 저장시
) {
    fun requestAIFeedback(projectId: Long, phase: String, fileUrl: String) {
        // AI 서버/gemini 연동: fileUrl을 넘겨 피드백 요청(비동기 트리거, 실제 분석은 따로)
        // 결과를 aiFeedbackRepository에 저장
        // 실제 구현 필요시 RestTemplate/WebClient로 AI 호출
    }

    fun getAIFeedback(projectId: Long, phase: String): AIFeedbackResponse {
        // 실제 구현: DB에서 결과 찾기
        val feedback = aiFeedbackRepository.findByProjectIdAndPhase(projectId, phase.uppercase())
            ?: throw NoSuchElementException("AI 피드백 없음")
        return AIFeedbackResponse(feedback.phase, feedback.content)
    }
}
