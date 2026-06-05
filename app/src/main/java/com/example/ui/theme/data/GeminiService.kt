package com.example.data

import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    // Moshi Models for REST
    data class Content(val parts: List<Part>)
    data class Part(
        val text: String? = null,
        val inlineData: InlineData? = null
    )
    data class InlineData(
        val mimeType: String,
        val data: String
    )
    data class GenerationConfig(
        val temperature: Float? = null,
        val imageConfig: ImageConfig? = null,
        val responseModalities: List<String>? = null
    )
    data class ImageConfig(
        val aspectRatio: String,
        val imageSize: String
    )
    data class GenerateContentRequest(
        val contents: List<Content>,
        val generationConfig: GenerationConfig? = null,
        val systemInstruction: Content? = null
    )

    // Response Models
    data class GenerateContentResponse(
        val candidates: List<Candidate>?
    )
    data class Candidate(
        val content: Content?
    )

    private fun getApiKey(): String {
        val key = BuildConfig.GEMINI_API_KEY
        return if (key == "MY_GEMINI_API_KEY" || key.isBlank()) {
            ""
        } else {
            key
        }
    }

    /**
     * Generates code or text using Gemini 3.5 Flash.
     */
    suspend fun generateCode(prompt: String, systemInstruction: String? = null): String {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return "Error: Gemini API Key is missing! Please configure GEMINI_API_KEY inside the Secrets Panel or .env file before utilizing AI features."
        }

        val requestPayload = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            ),
            systemInstruction = systemInstruction?.let {
                Content(parts = listOf(Part(text = it)))
            }
        )

        val adapter = moshi.adapter(GenerateContentRequest::class.java)
        val jsonBody = adapter.toJson(requestPayload)

        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            if (response.isSuccessful && responseBody != null) {
                val resAdapter = moshi.adapter(GenerateContentResponse::class.java)
                val parsed = resAdapter.fromJson(responseBody)
                val generatedText = parsed?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                generatedText ?: "No code was returned. Try adjusting your instruction prompt."
            } else {
                "Error: Failed to fetch AI response (${response.code}). Msg: ${responseBody ?: ""}"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini Code Generation Error", e)
            "Error calling Gemini API: ${e.message}"
        }
    }

    /**
     * Generates an image using Gemini 2.5 Flash Image.
     * Returns the raw Byte Array of the PNG or JPEG image.
     */
    suspend fun generateImage(prompt: String): ByteArray? {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            Log.e(TAG, "Gemini API Key is missing for image generation!")
            return null
        }

        val requestPayload = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            ),
            generationConfig = GenerationConfig(
                imageConfig = ImageConfig(aspectRatio = "1:1", imageSize = "1K"),
                responseModalities = listOf("TEXT", "IMAGE")
            )
        )

        val adapter = moshi.adapter(GenerateContentRequest::class.java)
        val jsonBody = adapter.toJson(requestPayload)

        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-image:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val resAdapter = moshi.adapter(GenerateContentResponse::class.java)
                val parsed = resAdapter.fromJson(responseBody)
                
                // Find inlineData in the response candidates
                val parts = parsed?.candidates?.firstOrNull()?.content?.parts
                val imagePart = parts?.firstOrNull { it.inlineData != null }
                
                if (imagePart?.inlineData != null) {
                    val base64Data = imagePart.inlineData.data
                    Base64.decode(base64Data, Base64.DEFAULT)
                } else {
                    Log.e(TAG, "Could not find image inline data in response: $responseBody")
                    null
                }
            } else {
                Log.e(TAG, "Failed image generation: ${response.code}, response: $responseBody")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini Image Generation Error", e)
            null
        }
    }
}
