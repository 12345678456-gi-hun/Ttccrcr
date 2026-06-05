package com.example.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val workspaceDao: WorkspaceDao,
    private val projectFileDao: ProjectFileDao,
    private val settingDao: SettingDao
) : ViewModel() {

    private val TAG = "MainViewModel"

    // Persistent Workspaces
    val workspacesFlow: Flow<List<Workspace>> = workspaceDao.getAllWorkspaces()

    // Active Workspace State
    private val _activeWorkspace = MutableStateFlow<Workspace?>(null)
    val activeWorkspace: StateFlow<Workspace?> = _activeWorkspace.asStateFlow()

    // Workspace Files State (Hot Flow depending on active workspace)
    val currentFilesFlow: Flow<List<ProjectFile>> = _activeWorkspace.flatMapLatest { ws ->
        if (ws != null) {
            projectFileDao.getFilesByWorkspaceFlow(ws.id)
        } else {
            flowOf(emptyList())
        }
    }

    // Currently open file in the IDE editor
    private val _activeFile = MutableStateFlow<ProjectFile?>(null)
    val activeFile: StateFlow<ProjectFile?> = _activeFile.asStateFlow()

    // Editor live content state (synchronized with activeFile)
    private val _editorContent = MutableStateFlow("")
    val editorContent: StateFlow<String> = _editorContent.asStateFlow()

    // Terminal Logging Panel Buffer
    private val _terminalLogs = MutableStateFlow<List<String>>(
        listOf(
            "V.O.I.D. Shell v2.5 initialized.",
            "Ready for Virtual Operations and Integrated Development.",
            "Type /help or use operations to interact."
        )
    )
    val terminalLogs: StateFlow<List<String>> = _terminalLogs.asStateFlow()

    // Vercel token and deploy status
    private val _vercelToken = MutableStateFlow("")
    val vercelToken: StateFlow<String> = _vercelToken.asStateFlow()

    private val _isDeploying = MutableStateFlow(false)
    val isDeploying: StateFlow<Boolean> = _isDeploying.asStateFlow()

    // AI Panel State
    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiChatHistory = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("ai", "Greeting operative inside the V.O.I.D. ecosystem. I am your Gemini 2.5 Assistant. Let me draft templates, refine code blocks, or compile high-resolution Imagen graphics instantly.")
        )
    )
    val aiChatHistory: StateFlow<List<ChatMessage>> = _aiChatHistory.asStateFlow()

    // Settings Modal State
    private val _showSettings = MutableStateFlow(false)
    val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()

    // Auto-save debounce references
    private var autoSaveJob: Job? = null

    init {
        // Load initial global settings
        viewModelScope.launch {
            val savedToken = settingDao.getSettingValue("vercel_token") ?: ""
            _vercelToken.value = savedToken
        }
    }

    fun openWorkspace(workspace: Workspace) {
        _activeWorkspace.value = workspace
        _activeFile.value = null
        _editorContent.value = ""
        logToTerminal("[SYSTEM] Loaded workspace \"${workspace.name}\" successfully.")
        
        // Load first template or text file if available
        viewModelScope.launch {
            val files = projectFileDao.getFilesByWorkspaceSync(workspace.id)
            val indexFile = files.find { it.path == "index.html" || it.path == "manifest.json" || it.path.endsWith(".html") } 
                ?: files.firstOrNull { !it.isBinary }
            indexFile?.let { openFile(it) }
        }
    }

    fun closeWorkspace() {
        // Save current active file before leaving
        saveCurrentFileImmediately()
        _activeWorkspace.value = null
        _activeFile.value = null
        _editorContent.value = ""
        logToTerminal("[SYSTEM] Closed workspace.")
    }

    fun openFile(file: ProjectFile) {
        saveCurrentFileImmediately()
        
        _activeFile.value = file
        _editorContent.value = if (file.isBinary) "[Binary Graphics / Image Asset]" else file.content
        logToTerminal("[FILE] Opened \"${file.path}\" in editor.")
    }

    fun createWorkspace(name: String, templateType: String) {
        viewModelScope.launch {
            val workspace = Workspace(
                name = name.ifBlank { "VoidProject-" + System.currentTimeMillis() % 1000 },
                templateType = templateType,
                lastModified = System.currentTimeMillis()
            )
            val workspaceId = workspaceDao.insertWorkspace(workspace)
            val updatedWorkspace = workspace.copy(id = workspaceId)

            val templateFiles = when (templateType) {
                "GAMING" -> TemplateProvider.generateGamingTemplateFiles(workspaceId)
                "MINECRAFT" -> TemplateProvider.generateMinecraftTemplateFiles(workspaceId)
                else -> listOf(
                    ProjectFile(
                        workspaceId = workspaceId,
                        path = "index.html",
                        content = "<h1>Virtual Operation inside V.O.I.D.</h1>\n<p>Start coding with supreme quality dark neon UI elements.</p>"
                    )
                )
            }

            for (file in templateFiles) {
                projectFileDao.insertFile(file)
            }

            openWorkspace(updatedWorkspace)
        }
    }

    fun deleteWorkspace(workspace: Workspace) {
        viewModelScope.launch {
            if (_activeWorkspace.value?.id == workspace.id) {
                closeWorkspace()
            }
            workspaceDao.deleteWorkspace(workspace)
            logToTerminal("[SYSTEM] Deleted workspace \"${workspace.name}\".")
        }
    }

    fun updateEditorContent(newContent: String) {
        val currentFile = _activeFile.value ?: return
        if (currentFile.isBinary) return // Skip edits to binary image files
        _editorContent.value = newContent

        // Restart debounce timer for 3 seconds auto-save
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(3000)
            saveCurrentFileImmediately()
        }
    }

    fun saveCurrentFileImmediately() {
        val file = _activeFile.value ?: return
        val text = _editorContent.value
        if (file.isBinary || file.content == text) return

        viewModelScope.launch {
            projectFileDao.updateFileContent(file.workspaceId, file.path, text)
            _activeFile.value = file.copy(content = text)
            // Log saving silently
            Log.d(TAG, "Auto-saved file: ${file.path}")
        }
    }

    fun createNewFileInWorkspace(path: String, content: String = "") {
        val ws = _activeWorkspace.value ?: return
        if (path.isBlank()) return

        viewModelScope.launch {
            val existing = projectFileDao.getFileByPath(ws.id, path)
            if (existing != null) {
                logToTerminal("[ERROR] File with path \"$path\" already exists.")
                return@launch
            }

            val newFile = ProjectFile(
                workspaceId = ws.id,
                path = path,
                content = content,
                isBinary = false
            )
            projectFileDao.insertFile(newFile)
            logToTerminal("[FILE] Created file \"$path\" successfully.")
            openFile(newFile)
        }
    }

    fun deleteFileFromWorkspace(path: String) {
        val ws = _activeWorkspace.value ?: return
        viewModelScope.launch {
            projectFileDao.deleteFileByPath(ws.id, path)
            logToTerminal("[FILE] Deleted file \"$path\".")
            if (_activeFile.value?.path == path) {
                _activeFile.value = null
                _editorContent.value = ""
            }
        }
    }

    fun saveVercelToken(token: String) {
        _vercelToken.value = token
        viewModelScope.launch {
            settingDao.saveSetting(Setting("vercel_token", token))
            logToTerminal("[SETTINGS] Successfully persisted encrypted Vercel access credentials.")
        }
    }

    fun toggleSettings(show: Boolean) {
        _showSettings.value = show
    }

    // Deploy or Update handler built fully dynamically with Vercel API
    fun handleVercelDeployment() {
        val ws = _activeWorkspace.value ?: return
        val token = _vercelToken.value

        if (token.isBlank()) {
            logToTerminal("[ERROR] Deployment Failed! Vercel API Token is not configured. Access Settings and configure a token key.")
            return
        }

        viewModelScope.launch {
            _isDeploying.value = true
            val word = if (ws.vercelProjectId.isNullOrBlank()) "DEPLOYING (New Vercel Project)" else "UPDATING deployment (Existing ProjectId: ${ws.vercelProjectId})"
            logToTerminal("[VERCEL] Initiating $word...")
            
            // Fetch all source files for the deployment body
            val allFiles = projectFileDao.getFilesByWorkspaceSync(ws.id)

            val result = VercelService.deploy(
                token = token,
                projectName = ws.name,
                files = allFiles,
                projectId = ws.vercelProjectId
            )

            _isDeploying.value = false
            when (result) {
                is DeploymentResult.Success -> {
                    // Update locally saved MMKV projectId equivalent
                    workspaceDao.updateVercelProjectId(ws.id, result.projectId)
                    _activeWorkspace.value = ws.copy(vercelProjectId = result.projectId)

                    logToTerminal("[SUCCESS] Deployment successful! Project ID: ${result.projectId}")
                    logToTerminal("[SUCCESS] Deployment URL: ${result.url}")
                    
                    // Easter Egg programmatic log!
                    logToTerminal("Domain Expansion: V.O.I.D. Successfully Deployed! JJK Supremacy.")
                }
                is DeploymentResult.Error -> {
                    logToTerminal("[ERROR] Deployment failed: ${result.message}")
                }
            }
        }
    }

    // Minecraft Zip and share handler
    fun handleMinecraftExport(context: Context) {
        val ws = _activeWorkspace.value ?: return
        viewModelScope.launch {
            logToTerminal("[MCPACK] Gathering components for custom Bedrock modpack: \"${ws.name}\"...")
            val files = projectFileDao.getFilesByWorkspaceSync(ws.id)
            
            val zipFile = McpackExporter.exportAndShare(context, ws.name, files)
            if (zipFile != null) {
                logToTerminal("[MCPACK] Successfully built custom Herobrine mod package (Size: ${zipFile.length()} bytes). Sharing intent initialized!")
                
                // Programmatic Easter Egg logging:
                logToTerminal("Domain Expansion: V.O.I.D. Successfully Deployed! JJK Supremacy.")
            } else {
                logToTerminal("[ERROR] Packaging Minecraft Bedrock add-on directory failed. Double check file validity.")
            }
        }
    }

    // AI Refactoring and Chat refinement
    fun handleAiSubmitPrompt(userPrompt: String) {
        if (userPrompt.isBlank()) return
        
        val targetFile = _activeFile.value
        val isCodeEditRequest = userPrompt.contains("edit", ignoreCase = true) || 
                               userPrompt.contains("modify", ignoreCase = true) ||
                               userPrompt.contains("make", ignoreCase = true) ||
                               userPrompt.contains("add", ignoreCase = true) ||
                               userPrompt.contains("replace", ignoreCase = true)

        // Append user prompt to history
        val updatedHistory = _aiChatHistory.value.toMutableList()
        updatedHistory.add(ChatMessage("user", userPrompt))
        _aiChatHistory.value = updatedHistory

        viewModelScope.launch {
            _isAiLoading.value = true

            // Conditional behavior: Image Generation vs Code Refinement
            if (userPrompt.contains("image", ignoreCase = true) || 
                userPrompt.contains("draw", ignoreCase = true) ||
                userPrompt.contains("generate custom image", ignoreCase = true) ||
                userPrompt.contains("png", ignoreCase = true)) {
                
                logToTerminal("[AI] Operator requested graphic compilation. Calling Gemini 2.5 Image engine...")
                val imageBytes = GeminiService.generateImage(userPrompt)
                
                _isAiLoading.value = false
                if (imageBytes != null) {
                    // Save file inside project database
                    val imageCount = _aiChatHistory.value.count { it.isImage } + 1
                    val imageName = "images/generative_graphics_$imageCount.png"
                    
                    val ws = _activeWorkspace.value
                    if (ws != null) {
                        val newImageFile = ProjectFile(
                            workspaceId = ws.id,
                            path = imageName,
                            content = "",
                            isBinary = true,
                            binaryData = imageBytes
                        )
                        projectFileDao.insertFile(newImageFile)
                        logToTerminal("[FILE] Saved generated AI graphic asset as \"$imageName\" under local directory.")
                        
                        val successHistory = _aiChatHistory.value.toMutableList()
                        successHistory.add(ChatMessage("ai", "I have generated the PNG visual graphic package. It is loaded in your Explorer directory as \"$imageName\". You can click to view it inside our Workspace viewer!"))
                        _aiChatHistory.value = successHistory
                        
                        openFile(newImageFile)
                    } else {
                        val errorHistory = _aiChatHistory.value.toMutableList()
                        errorHistory.add(ChatMessage("ai", "[ERROR] AI generated graphic is ready, but couldn't be saved because no active Workspace is selected."))
                        _aiChatHistory.value = errorHistory
                    }
                } else {
                    val failHistory = _aiChatHistory.value.toMutableList()
                    failHistory.add(ChatMessage("ai", "I was unable to compile the visual asset block. Please verify your GEMINI_API_KEY supports the gemini-2.5-flash-image model in AI Studio!"))
                    _aiChatHistory.value = failHistory
                }
            } else {
                // Code editing or chat refinement
                val promptModifier = if (targetFile != null && isCodeEditRequest) {
                    """
                    You are an expert full-stack developer assistant in V.O.I.D. IDE.
                    The user is asking to edit, modify, or add features.
                    Active file opened in context is named "${targetFile.path}" with content:
                    ```
                    ${targetFile.content}
                    ```
                    
                    USER INSTRUCTION:
                    $userPrompt
                    
                    If you are generating/modifying code, PLEASE output the ENTIRE modified or original file source code in a single code block from start to finish, so the prompt-handler can automatically apply the changes for the user. Do not clip or truncate files. Format your code block with standard backticks. Otherwise, explain clearly and concisely.
                    """.trimIndent()
                } else {
                    "Operative Workspace Instruction: $userPrompt"
                }

                logToTerminal("[AI] Transmitting code package to Gemini 3.5 Assistant query...")
                val aiResponse = GeminiService.generateCode(promptModifier)
                _isAiLoading.value = false

                val responseHistory = _aiChatHistory.value.toMutableList()
                responseHistory.add(ChatMessage("ai", aiResponse))
                _aiChatHistory.value = responseHistory

                // Auto-apply code blocks if user requested modifications and we found a single robust block of code
                if (targetFile != null && isCodeEditRequest) {
                    val codeBlockRegex = Regex("```(?:[a-zA-Z0-9_-]+)?\\n([\\s\\S]*?)\\n```")
                    val matchResult = codeBlockRegex.find(aiResponse)
                    val codeContent = matchResult?.groups?.get(1)?.value
                    
                    if (!codeContent.isNullOrBlank() && codeContent.length > 10) {
                        updateEditorContent(codeContent)
                        logToTerminal("[AI_MODIFIER] Detected code blocks in AI response. Programmatically injected updated code package into active editor for file \"${targetFile.path}\"!")
                    }
                }
            }
        }
    }

    fun executeTerminalCommand(cmdString: String) {
        if (cmdString.isBlank()) return
        logToTerminal("> $cmdString")
        
        val tokens = cmdString.trim().split(Regex("\\s+"))
        val baseCommand = tokens[0].lowercase()

        when (baseCommand) {
            "/help" -> {
                logToTerminal("V.O.I.D. Available Operative Commands:")
                logToTerminal(" /help                - Show this menu block")
                logToTerminal(" /deploy              - Launch Vercel Sync")
                logToTerminal(" /mcpack              - Export Bedrock modules to .mcpack file")
                logToTerminal(" /clear               - Purge active shell buffer logs")
                logToTerminal(" /create <filename>   - Spawn new blank file")
                logToTerminal(" /delete <filename>   - Remove file from directory workspace")
            }
            "/clear" -> {
                _terminalLogs.value = emptyList()
            }
            "/deploy" -> {
                handleVercelDeployment()
            }
            "/mcpack" -> {
                logToTerminal("[MCPACK_DIRECT] Packaging Minecraft components...")
                // Trigger export via mock/null context first (requiring full trigger from Composable instead)
                logToTerminal("[COMMAND] For safety, please click the primary '.mcpack' export header visual button on top panels!")
            }
            "/create" -> {
                if (tokens.size < 2) {
                    logToTerminal("[ERROR] Usage: /create <filename>")
                } else {
                    createNewFileInWorkspace(tokens[1])
                }
            }
            "/delete" -> {
                if (tokens.size < 2) {
                    logToTerminal("[ERROR] Usage: /delete <filename>")
                } else {
                    deleteFileFromWorkspace(tokens[1])
                }
            }
            else -> {
                logToTerminal("V.O.I.D. error: Unidentified terminal block command \"$baseCommand\". Type /help to query system tools.")
            }
        }
    }

    private fun logToTerminal(message: String) {
        val current = _terminalLogs.value.toMutableList()
        current.add(message)
        _terminalLogs.value = current
    }
}

data class ChatMessage(
    val sender: String, // user, ai
    val message: String,
    val isImage: Boolean = false
)

class MainViewModelFactory(
    private val workspaceDao: WorkspaceDao,
    private val projectFileDao: ProjectFileDao,
    private val settingDao: SettingDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(workspaceDao, projectFileDao, settingDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
