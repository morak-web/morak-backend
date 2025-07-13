package com.xhae.morak.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.concurrent.atomic.AtomicInteger

private val log = KotlinLogging.logger {}

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
class GeminiRateLimitException(message: String) : RuntimeException(message)

@Service
class GeminiService(
    @Value("\${gemini.api.key}") private val apiKey: String,
    @Value("\${gemini.limit-request-per-minute}") private val reqLimit: Int
) {
    private val client = OkHttpClient()
    private val totalReqCount = AtomicInteger(0)
    @Volatile private var lastResetMillis = System.currentTimeMillis()
    private val resetIntervalMillis = 60_000L // 1분

    private fun resetIfNeeded() {
        val now = System.currentTimeMillis()
        if (now - lastResetMillis > resetIntervalMillis) {
            totalReqCount.set(0)
            lastResetMillis = now
            log.info { "Gemini 전체 요청 카운트 리셋됨" }
        }
    }
    private fun checkRateLimit() {
        resetIfNeeded()
        val cnt = totalReqCount.incrementAndGet()
        if (cnt > reqLimit) {
            log.warn { "Gemini 전체 요청 제한 초과: cnt=$cnt, limit=$reqLimit" }
            throw GeminiRateLimitException("Gemini 서버 전체 요청 제한 초과: 1분에 ${reqLimit}회 제한")
        }
    }

    /**
     * 현업 디자이너가 추가질문이 거의 불필요할 정도로
     * 실제 실무에서 '한 번에 받아야 할' 모든 추가질문을 생성.
     */
    fun generateFollowupQuestions(userRequirement: String): List<String> {
        checkRateLimit()

        val prompt = """
        [역할] 당신은 10년차 UI/UX 디자인 매니저입니다.
        [목적] 아래 의뢰인의 외주 요청서를 바탕으로, 디자이너가 절대로 재질문을 하지 않도록
        반드시 받아야 할 *모든* 추가질문을 실무적으로 꼼꼼히 한글로 작성하세요.

        [조건]
        - 실무 기준에서 작업 중 빠질 수 있는 항목(색상, 폰트, 컨셉, 스타일, 인터랙션, 반응형, 예산, 일정, 참고자료, 브랜드규정, 산출물형태, 플랫폼, 정책, 우선순위, '하지 말아야 할 것' 등)을 빠짐없이 체크하세요.
        - 요구서에 애매하거나 불명확한 부분, 선택지가 필요한 부분, 실제 업무에서 추후 오해가 될 만한 부분은 반드시 구체적으로 질문하세요.
        - 질문 개수 제한 없이, 누락 없이 *모든* 추가질문을 배열(리스트)로, 한 문장 존댓말로, ["질문1", "질문2", ...] 형식으로만 출력. 불필요한 설명 없이 질문만.
        - 질문마다 중복, 모호함, 두루뭉술함 없이 실무 핵심만. (최소 10개 이상 권장)

        [의뢰 요청]
        $userRequirement

        [추가질문 리스트]:
        """.trimIndent()

        val apiUrl = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-pro:generateContent?key=$apiKey"
        val bodyJson = """{
            "contents": [
                { "parts": [ { "text": ${Json.encodeToString(String.serializer(), prompt)} } ] }
            ]
        }"""

        log.info { "Gemini 추가질문 프롬프트 전송:\n$prompt" }

        val request = Request.Builder()
            .url(apiUrl)
            .post(bodyJson.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                log.error { "Gemini API 실패: ${response.code} ${response.body?.string()}" }
                throw RuntimeException("Gemini API 호출 실패: ${response.code}")
            }
            val responseBody = response.body?.string()
                ?: throw IllegalStateException("Gemini 응답 없음")
            log.info { "Gemini 응답: $responseBody" }

            try {
                val geminiResponse = Json { ignoreUnknownKeys = true }
                    .decodeFromString(GeminiApiRawResponse.serializer(), responseBody)
                val resultJson = geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: throw IllegalStateException("Gemini 응답에 결과 없음")
                val cleanJson = resultJson.replace(Regex("```json|```|\\s+$"), "").trim()
                log.info { "Gemini 결과(클린): $cleanJson" }
                return Json.decodeFromString(ListSerializer(String.serializer()), cleanJson)
            } catch (e: Exception) {
                log.error(e) { "Gemini 파싱 에러: 실제 응답=$responseBody" }
                throw e
            }
        }
    }
}

@Serializable
data class GeminiApiRawResponse(val candidates: List<Candidate>)
@Serializable
data class Candidate(val content: Content)
@Serializable
data class Content(val parts: List<Part>)
@Serializable
data class Part(val text: String)
