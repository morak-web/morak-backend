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
[Role]
You are a senior UI/UX design manager with 10 years of professional experience.

[Context]
The following request is for a web/app screen UI/UX design project only.
Do not consider any other types of design (e.g., graphic, print, video, etc.).

[Objective]
Based on the client's request below, generate only the truly necessary follow-up questions that a professional UI/UX designer would need to ask before starting work.
If the client's request is already fully clear and there is nothing to clarify, return an empty array: [].

[Guidelines]
- Write all follow-up questions in Korean.
- Write no more than 5 questions. If fewer are needed, use fewer.
- Only ask about details truly necessary for web/app UI/UX design (such as color, font, layout, components, branding, features, interactivity, responsiveness, device targets, deliverable format, priorities, things NOT to do, references, style, etc.).
- Do NOT repeat or paraphrase what the client already wrote; only ask about gaps, ambiguity, or required decisions.
- NEVER invent questions just to fill space—if everything is clear, output only [].
- When you ask questions, be concise and professional. Write each as a single polite Korean sentence, and output as a JSON array:
  ["질문 1", "질문 2", ...]
- Output only the array. Do not add any explanation, markdown, or extra text.

[Client Request]
$userRequirement

[Follow-up Questions]:
""".trimIndent()



        val apiUrl = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=$apiKey"
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
