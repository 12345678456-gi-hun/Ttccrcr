package com.example.data

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

object VercelService {
    private const val TAG = "VercelService"
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    data class VercelFile(val file: String, val data: String)
    data class VercelProjectSettings(val framework: String? = null)
    
    // Deployment response structures
    data class DeploymentResponse(
        val id: String?,
        val url: String?,
        val name: String?,
        val projectId: String?,
        val status: String?,
        val error: DeploymentError?
    )
    
    data class DeploymentError(
        val code: String?,
        val message: String?
    )

    /**
     * Deploys the workspace files to Vercel.
     * If [projectId] is null, this creates a new project.
     * If [projectId] is provided, it deploys directly to that project.
     */
    suspend fun deploy(
        token: String,
        projectName: String,
        files: List<ProjectFile>,
        projectId: String? = null
    ): DeploymentResult {
        if (token.isBlank()) {
            return DeploymentResult.Error("Vercel API Token is missing! Please configure it in Settings first.")
        }

        val formattedName = projectName.lowercase()
            .replace(Regex("[^a-z0-9-]"), "-")
            .trim('-')
            .ifBlank { "void-app-" + System.currentTimeMillis() % 100000 }

        // Compile files for upload
        val vercelFiles = files.filter { !it.isBinary }.map { file ->
            // In Vercel, the directory structure must be properly specified.
            // E.g. "index.html", "script.js"
            VercelFile(file = file.path, data = file.content)
        }

        if (vercelFiles.isEmpty()) {
            return DeploymentResult.Error("No files found in workspace to deploy!")
        }

        // Build the payload
        val requestMap = mutableMapOf<String, Any>(
            "name" to formattedName,
            "files" to vercelFiles,
            "projectSettings" to VercelProjectSettings(framework = null)
        )

        if (!projectId.isNullOrBlank()) {
            requestMap["projectId"] = projectId
        }

        val adapter = moshi.adapter(Map::class.java)
        val jsonPayload = adapter.toJson(requestMap)

        val requestBody = jsonPayload.toRequestBody("application/json".toMediaType())
        val url = "https://api.vercel.com/v13/deployments"
        
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            Log.d(TAG, "Vercel Response ($response): $responseBody")

            if (responseBody != null) {
                val responseAdapter = moshi.adapter(DeploymentResponse::class.java)
                val deployResponse = responseAdapter.fromJson(responseBody)
                
                if (response.isSuccessful && deployResponse != null) {
                    val deployUrl = "https://${deployResponse.url}"
                    val newProjectId = deployResponse.projectId ?: deployResponse.id
                    DeploymentResult.Success(
                        url = deployUrl,
                        projectId = newProjectId ?: "",
                        deploymentId = deployResponse.id ?: ""
                    )
                } else {
                    val errMsg = deployResponse?.error?.message ?: "Vercel API Error: Code ${response.code}"
                    DeploymentResult.Error(errMsg)
                }
            } else {
                DeploymentResult.Error("Empty response received from Vercel.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Deployment failed", e)
            DeploymentResult.Error("Network error: ${e.message}")
        }
    }
}

sealed class DeploymentResult {
    data class Success(val url: String, val projectId: String, val deploymentId: String) : DeploymentResult()
    data class Error(val message: String) : DeploymentResult()
}
