package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.GenerationConfig
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.api.SerenityResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface SerenityUiState {
    object Idle : SerenityUiState
    object Loading : SerenityUiState
    data class Success(val response: SerenityResponse, val isMock: Boolean = false) : SerenityUiState
    data class Error(val message: String) : SerenityUiState
}

class SerenityViewModel : ViewModel() {
    private val _reflectionText = MutableStateFlow("")
    val reflectionText: StateFlow<String> = _reflectionText.asStateFlow()

    private val _guidancePreference = MutableStateFlow("General Wisdom")
    val guidancePreference: StateFlow<String> = _guidancePreference.asStateFlow()

    private val _uiState = MutableStateFlow<SerenityUiState>(SerenityUiState.Idle)
    val uiState: StateFlow<SerenityUiState> = _uiState.asStateFlow()

    fun updateReflectionText(text: String) {
        _reflectionText.value = text
    }

    fun updateGuidancePreference(pref: String) {
        _guidancePreference.value = pref
    }

    fun hasValidApiKey(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY" && !key.startsWith("placeholder")
    }

    fun analyzeAndProvideWisdom(simulate: Boolean = false) {
        val reflection = _reflectionText.value.trim()
        val preference = _guidancePreference.value

        if (reflection.isBlank()) {
            _uiState.value = SerenityUiState.Error("Please write how you are feeling before seeking wisdom.")
            return
        }

        _uiState.value = SerenityUiState.Loading

        viewModelScope.launch {
            if (simulate || !hasValidApiKey()) {
                // Return immediate realistic mocking response to user
                withContext(Dispatchers.IO) {
                    kotlinx.coroutines.delay(1800) // Aesthetic delay for reflection loading effect
                    val mock = generateMockResponse(reflection, preference)
                    _uiState.value = SerenityUiState.Success(mock, isMock = !simulate && !hasValidApiKey())
                }
                return@launch
            }

            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val systemPrompt = """
                    You are Serenity AI - a compassionate emotional wellness companion designed to support students and young adults managing stress or anxiety. 
                    Your mission is to provide comforting and structured guidance based on their emotional reflection and their chosen Wisdom/Scriptural preference.
                    
                    CRITICAL SAFETY RULES:
                    - You are NOT a therapist, counselor, or medical professional.
                    - Do NOT provide clinical diagnoses, psychiatric assessments, crisis intervention, or medical advice.
                    - Do NOT provide religious rulings or legal advice.
                    - Use supportive, compassionate, safe language. Avoid fear, urgency, or judgment.
                    - If severe self-harm or deep psychological emergency is detected, calmly and gently point them in the final "practical_steps" or "encouragement" to speak with a professional or trusted companion, while keeping the other fields completely supportive.
                    
                    STEPS & CONTENT FOR RESPONSIBILITY:
                    1. Analyze the user's emotional message to determine:
                       - Primary Emotion (e.g. Lonely, Exhausted, Anxious, Self-Doubt, Overwhelmed, Discouraged)
                       - Secondary Emotion (e.g. Unmotivated, Weary, Overloaded, Scared, Disappointed)
                       - Stress Level (Low, Medium, High)
                       - Confidence Level (Low, Medium, High)
                       - Root Cause (e.g. Academic Pressure, Fear of Failure, Burnout, Loneliness, Self-Doubt, Family Expectations, Career Uncertainty, Emotional Fatigue).
                    2. Formulate a personalized response in English based on their Guidance Preference: "$preference".
                    3. Structure your response to exactly fill these JSON properties. Let lengths be strictly enforced:
                       - "emotion_summary": Short empathetic summary of what the user may be experiencing. Maximum 2 sentences.
                       - "wisdom_quote": A highly relevant quote or verse matching the preference. 
                         * Islamic: Provide a relevant Qur'an verse or authentic teaching with numeric references (e.g. Surah Al-Imran 3:139).
                         * Christian: Provide a relevant Bible verse with reference (e.g. Matthew 11:28).
                         * Hindu: Provide a relevant Bhagavad Gita verse or Hindu philosophical teaching (e.g. Bhagavad Gita 2.14).
                         * General Wisdom: Provide a timeless original or historical philosophical quote/reflection (specify author or philosophical school, e.g. Marcus Aurelius).
                       - "source": The precise citation reference for the wisdom quote.
                       - "historical_context": Explain the historical background, philosophical context, or revelational reason behind this teaching. Keep it simple, respectful, and maximum 120 words.
                       - "personalized_reflection": Warm, hopeful connection tying the wisdom directly to the user's emotional situation. Explain why it is relevant and what they can learn. Maximum 150 words.
                       - "practical_steps": A list of exactly 3 simple, realistic, immediately achievable, non-medical steps.
                       - "encouragement": One uplifting, hopeful message. Maximum 20 words.
                       - "image_prompt": A highly detailed and peace-inducing prompt suitable for generating a calming landscape painting or landscape photo, tailored to represent hope, transition, and light overcoming shadows matching their current mood (e.g. "Peaceful misty green forest walkway at sunrise with golden shafts of light breaking through trees, oil painting, high-resolution style").
                    
                    Your output MUST be strictly invalid-free JSON matching the JSON schema. Do NOT return markdown formatting tags other than the JSON object itself.
                """.trimIndent()

                val userPrompt = """
                    User Emotional Reflection: "$reflection"
                    Guidance Preference: "$preference"
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = userPrompt)))
                    ),
                    generationConfig = GenerationConfig(
                        responseMimeType = "application/json",
                        temperature = 0.7f
                    ),
                    systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: throw Exception("Empty response received from server.")

                val parsed = withContext(Dispatchers.Default) {
                    val cleanedJson = cleanJsonString(rawText)
                    val adapter = RetrofitClient.moshi.adapter(SerenityResponse::class.java)
                    adapter.fromJson(cleanedJson) ?: throw Exception("Failed to structure reflection data.")
                }

                _uiState.value = SerenityUiState.Success(parsed, isMock = false)
            } catch (e: Exception) {
                _uiState.value = SerenityUiState.Error(e.message ?: "An unexpected error occurred while connecting to Serenity AI.")
            }
        }
    }

    private fun cleanJsonString(input: String): String {
        var clean = input.trim()
        if (clean.startsWith("```json")) {
            clean = clean.substring("```json".length)
        } else if (clean.startsWith("```")) {
            clean = clean.substring("```".length)
        }
        if (clean.endsWith("```")) {
            clean = clean.substring(0, clean.length - "```".length)
        }
        return clean.trim()
    }

    fun resetState() {
        _uiState.value = SerenityUiState.Idle
    }

    private fun generateMockResponse(reflection: String, preference: String): SerenityResponse {
        val lowercaseReflection = reflection.lowercase()
        val isBurnout = lowercaseReflection.contains("exhaust") || lowercaseReflection.contains("burn") || lowercaseReflection.contains("tired") || lowercaseReflection.contains("overwhelm")
        val isExam = lowercaseReflection.contains("exam") || lowercaseReflection.contains("study") || lowercaseReflection.contains("poor") || lowercaseReflection.contains("fail")
        val isAnxious = lowercaseReflection.contains("anxious") || lowercaseReflection.contains("future") || lowercaseReflection.contains("worry")
        
        val primary: String
        val secondary: String
        val stress: String
        val rootCause: String
        val s1: String
        
        if (isBurnout) {
            primary = "Exhaustion"
            secondary = "Weariness"
            stress = "High"
            rootCause = "Burnout"
            s1 = "It sounds like you are carrying a heavy weight, pushing your limits to the point of profound exhaustion."
        } else if (isExam) {
            primary = "Discouragement"
            secondary = "Disappointment"
            stress = "Medium"
            rootCause = "Academic Pressure"
            s1 = "You've worked incredibly hard, making the weight of these disappointing academic outcomes feel deeply painful."
        } else if (isAnxious) {
            primary = "Anxiety"
            secondary = "Fear of Uncertainty"
            stress = "High"
            rootCause = "Career Uncertainty"
            s1 = "Taking steps into the unknown has brought a profound sense of anxiety about what lies ahead."
        } else {
            primary = "Lonely"
            secondary = "Weary"
            stress = "Medium"
            rootCause = "Loneliness"
            s1 = "You are feeling a sense of disconnect and low motivation, carrying this burden by yourself today."
        }

        val parsedMock = when (preference) {
            "Islamic Reflection" -> SerenityResponse(
                primary_emotion = primary,
                secondary_emotion = secondary,
                stress_level = stress,
                confidence_level = "High",
                root_cause = rootCause,
                emotion_summary = s1,
                wisdom_quote = "For indeed, with hardship [will be] ease. Indeed, with hardship [will be] ease.",
                source = "Qur'an | Surah Al-Inshirah 94:5-6",
                historical_context = "This comfort was revealed to comfort Prophet Muhammad (PBUH) during intense periods of social isolation, grief, and early struggles in Makkah. It served to reassure him that easing and relief are already adjacent to every trial.",
                personalized_reflection = "In your current challenge, the pressure you feel can seem constant. However, this divine insight guarantees that difficulty is not your permanent state. Just as day inevitably breaks after night, the relief is already bound to your endurance. Let this assure you that your struggles are developing unique strength.",
                practical_steps = listOf(
                    "Perform a slow 5-minute deep breathing exercise in a quiet room.",
                    "Sip a warm cup of herbal tea or warm water consciously.",
                    "Set aside all school/work screens and rest your eyes for 20 minutes."
                ),
                encouragement = "Your endurance is powerful. Ease is closer than your anxious expectations.",
                image_prompt = "Peaceful morning sunrise over calm misty lakes and green hills, soft golden lighting filtering through clouds, oil painting, high resolution, soothing art"
            )
            "Christian Reflection" -> SerenityResponse(
                primary_emotion = primary,
                secondary_emotion = secondary,
                stress_level = stress,
                confidence_level = "High",
                root_cause = rootCause,
                emotion_summary = s1,
                wisdom_quote = "Come to me, all you who are weary and burdened, and I will give you rest. Take my yoke upon you and learn from me, for I am gentle and humble in heart, and you will find rest for your souls.",
                source = "The Holy Bible | Matthew 11:28-29",
                historical_context = "Jesus uttered these words in Galilee during an era of burdensome religious demands and crushing social strains. It stood out as a revolutionary invitation to lay aside heavy labor and experience actual mental and spiritual rejuvenation.",
                personalized_reflection = "When you feel exhausted, it is easy to assume you must keep pushing harder. But this invitation asks you to do the exact opposite: to surrender that heavy burden and accept a gentle, humble rest. Rest is not a reward for when you complete everything; it is the spiritual foundation that allows you to cope.",
                practical_steps = listOf(
                    "Journal your heaviest anxious thoughts on paper to release them.",
                    "Go outside and place your bare feet on grass if possible, otherwise sit by a window.",
                    "Send a brief text to a trusted friend just to greet them and connect."
                ),
                encouragement = "You do not have to carry everything alone today. Allow yourself to rest.",
                image_prompt = "Calm streams meandering gently through a luscious valley, warm dappled sunlight through willow trees, romanticist style watercolor painting"
            )
            "Hindu Reflection" -> SerenityResponse(
                primary_emotion = primary,
                secondary_emotion = secondary,
                stress_level = stress,
                confidence_level = "High",
                root_cause = rootCause,
                emotion_summary = s1,
                wisdom_quote = "The contacts of the senses, O son of Kunti, giving cold and heat, pleasure and pain, they are non-permanent, coming and going. Endure them bravely, O Bharata.",
                source = "Bhagavad Gita | Chapter 2, Verse 14",
                historical_context = "Spoken by Lord Krishna to Arjuna on the battlefield of Kurukshetra when Arjuna was paralyzed by high self-doubt, family expectations, and career uncertainty. Lord Krishna speaks to help him find his inner center amidst changing circumstances.",
                personalized_reflection = "This teaching reminds us that emotions of disappointment, burnout, and fear of failure are like changing seasons. Sensory contacts and transient anxieties come and go. Realize your eternal nature is above these passing storms, allowing you to endure waves with courage and mindfulness.",
                practical_steps = listOf(
                    "Sit straight and focus closely on the cooling air entering your nostrils.",
                    "Step away from your desk and stretch your back and limbs gently for 5 minutes.",
                    "Tackle just one small micro-task next without thinking about the larger work."
                ),
                encouragement = "This season of stress will pass. Stand anchored in your quiet center.",
                image_prompt = "Serene forest path under towering giant redwood trees, brilliant light beams poking through leaves onto wildflowers, watercolor illustration"
            )
            else -> SerenityResponse(
                primary_emotion = primary,
                secondary_emotion = secondary,
                stress_level = stress,
                confidence_level = "High",
                root_cause = rootCause,
                emotion_summary = s1,
                wisdom_quote = "You have power over your mind - not outside events. Realize this, and you will find strength.",
                source = "Marcus Aurelius | Meditations",
                historical_context = "Penned by the Stoic Roman Emperor Marcus Aurelius in his private journals while leading military campaigns and managing a deadly empire-wide plague. It served as a personal cognitive blueprint to maintain serenity.",
                personalized_reflection = "In moments of high academic or life pressure, outside expectations feel overwhelming because we cannot control them. Stoic wisdom reminds us that our true shield is our immediate perspective. By filtering what is in your power and letting the rest unfold, you unlock deep, steady resilience.",
                practical_steps = listOf(
                    "Close your eyes and slowly inhale for 4 seconds, hold for 4, exhale for 4.",
                    "Write down three external things you cannot control and make peace with them.",
                    "Wash your face with cold water to refresh your sensory responses immediately."
                ),
                encouragement = "Power lies within your current response. Move forward one small breath at a time.",
                image_prompt = "Stunning sunrise peaking above mountain silhouettes, clear reflecting water in the foreground, digital vector art style"
            )
        }
        return parsedMock
    }
}
