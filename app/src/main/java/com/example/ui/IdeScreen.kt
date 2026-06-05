package com.example.ui

import android.widget.Space
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.ProjectFile
import com.example.data.Workspace
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun IdeScreen(viewModel: MainViewModel) {
    val activeWorkspace by viewModel.activeWorkspace.collectAsState()
    val showSettings by viewModel.showSettings.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
    ) {
        if (activeWorkspace == null) {
            DashboardView(viewModel)
        } else {
            WorkspaceView(viewModel)
        }

        if (showSettings) {
            SettingsDialog(viewModel)
        }
    }
}

// ======================== DASHBOARD MODE ========================

@Composable
fun DashboardView(viewModel: MainViewModel) {
    val workspaces by viewModel.workspacesFlow.collectAsState(initial = emptyList())
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedTemplate by remember { mutableStateOf("NONE") }
    
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // High polish cyber header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "V . O . I . D .",
                    fontSize = 28.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = VoidPurpleGlow,
                    modifier = Modifier.testTag("void_hub_title")
                )
                Text(
                    text = "Virtual Operations & Integrated Development",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    color = VoidTextSecondary
                )
            }
            
            IconButton(
                onClick = { viewModel.toggleSettings(true) },
                modifier = Modifier
                    .background(VoidSurface, RoundedCornerShape(8.dp))
                    .testTag("dashboard_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Access Settings",
                    tint = VoidTeal
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Template Selection Header
        Text(
            text = "⚡ SPAWN V.O.I.D. TEMPLATE",
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = VoidTextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Gaming Template Item (Free Fire MAX)
            TemplateSpawnCard(
                title = "Free Fire MAX",
                subtitle = "Gaming companion, squad generator + wheel",
                iconRes = Icons.Default.PlayArrow,
                glowColor = VoidMagenta,
                onClick = {
                    selectedTemplate = "GAMING"
                    showCreateDialog = true
                },
                modifier = Modifier.testTag("spawn_gaming_template")
            )

            // Minecraft Behavior addon
            TemplateSpawnCard(
                title = "Minecraft addon",
                subtitle = "Herobrine boss-battle logic pack",
                iconRes = Icons.Default.Build,
                glowColor = VoidTeal,
                onClick = {
                    selectedTemplate = "MINECRAFT"
                    showCreateDialog = true
                },
                modifier = Modifier.testTag("spawn_minecraft_template")
            )

            // Blank standard HTML
            TemplateSpawnCard(
                title = "Vanilla HTML5",
                subtitle = "Raw blank sandbox workspace",
                iconRes = Icons.Default.Create,
                glowColor = VoidPurple,
                onClick = {
                    selectedTemplate = "NONE"
                    showCreateDialog = true
                },
                modifier = Modifier.testTag("spawn_blank_template")
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Spaces Header
        Text(
            text = "📁 RECENT OPERATIONS",
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = VoidTextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (workspaces.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VoidSurface)
                    .border(1.dp, VoidSurfaceLight, RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Empty Spaces List",
                        tint = VoidTextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Zero Operations Initialized.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = VoidTextPrimary
                    )
                    Text(
                        text = "Spawn a template from the deck above to begin coding.",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = VoidTextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(workspaces, key = { it.id }) { ws ->
                    RecentWorkspaceCard(
                        workspace = ws,
                        onOpen = { viewModel.openWorkspace(ws) },
                        onDelete = { viewModel.deleteWorkspace(ws) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        ProjectCreateDialog(
            templateType = selectedTemplate,
            onDismiss = { showCreateDialog = false },
            onConfirm = { name ->
                viewModel.createWorkspace(name, selectedTemplate)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun TemplateSpawnCard(
    title: String,
    subtitle: String,
    iconRes: ImageVector,
    glowColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = VoidSurface),
        modifier = modifier
            .width(240.dp)
            .height(120.dp)
            .border(1.dp, glowColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = VoidTextPrimary
                )
                Icon(
                    imageVector = iconRes,
                    contentDescription = null,
                    tint = glowColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = VoidTextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun RecentWorkspaceCard(
    workspace: Workspace,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(VoidSurface)
            .clickable { onOpen() }
            .border(1.dp, VoidSurfaceLight, RoundedCornerShape(10.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            val badgeColor = when (workspace.templateType) {
                "GAMING" -> VoidMagenta
                "MINECRAFT" -> VoidTeal
                else -> VoidPurpleGlow
            }
            
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(badgeColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = workspace.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = VoidTextPrimary
                )
                Text(
                    text = "Type: ${workspace.templateType} | Vercel ID: ${workspace.vercelProjectId ?: "None"}",
                    fontSize = 11.sp,
                    color = VoidTextSecondary
                )
            }
        }
        
        IconButton(
            onClick = onDelete,
            modifier = Modifier.testTag("delete_workspace_${workspace.id}")
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete operation",
                tint = VoidMagenta.copy(alpha = 0.8f)
            )
        }
    }
}

// ======================== OPERATION WORKSPACE MODE ========================

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceView(viewModel: MainViewModel) {
    val activeWorkspace by viewModel.activeWorkspace.collectAsState()
    val files by viewModel.currentFilesFlow.collectAsState(initial = emptyList())
    val activeFile by viewModel.activeFile.collectAsState()
    val isDeploying by viewModel.isDeploying.collectAsState()

    var showSidebar by remember { mutableStateOf(true) }
    var showAiAssistant by remember { mutableStateOf(false) }
    var selectedBottomTab by remember { mutableStateOf("TERMINAL") } // TERMINAL, WEBVIEW, MCPACK
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = activeWorkspace?.name ?: "V.O.I.D. Space",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = VoidTextPrimary
                        )
                        Text(
                            text = if (activeWorkspace?.vercelProjectId != null) "Vercel Workspace: Sync Mode" else "Vercel Workspace: Offline",
                            fontSize = 9.sp,
                            color = VoidTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.closeWorkspace() },
                        modifier = Modifier.testTag("exit_workspace_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Save and exit",
                            tint = VoidTeal
                        )
                    }
                },
                actions = {
                    // Left Explorer toggle
                    IconButton(
                        onClick = { showSidebar = !showSidebar },
                        modifier = Modifier.testTag("toggle_explorer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Toggle Files explorer",
                            tint = if (showSidebar) VoidTeal else VoidTextSecondary
                        )
                    }

                    // Secure settings entry
                    IconButton(
                        onClick = { viewModel.toggleSettings(true) },
                        modifier = Modifier.testTag("menu_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Vercel Tokens Config",
                            tint = VoidTextSecondary
                        )
                    }

                    // Vercel deployment action (conditional Deploy vs Update)
                    val buttonLabel = if (activeWorkspace?.vercelProjectId == null) "Deploy" else "Update"
                    Button(
                        onClick = { viewModel.handleVercelDeployment() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeWorkspace?.vercelProjectId == null) VoidPurple else VoidTeal
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("vercel_deploy_button")
                    ) {
                        if (isDeploying) {
                            CircularProgressIndicator(
                                color = VoidTextPrimary,
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = buttonLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VoidBlack
                            )
                        }
                    }

                    // Special .mcpack Exporter trigger
                    IconButton(
                        onClick = { viewModel.handleMinecraftExport(context) },
                        modifier = Modifier.testTag("export_mcpack_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Export .mcpack",
                            tint = VoidMagenta
                        )
                    }

                    // AI Ghost Panel Toggle
                    IconButton(
                        onClick = { showAiAssistant = !showAiAssistant },
                        modifier = Modifier.testTag("toggle_ai_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "AI Assistant (Ghost)",
                            tint = if (showAiAssistant) VoidTeal else VoidTextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VoidBlack)
            )
        },
        bottomBar = {
            // Main navigation container padding handling
            Surface(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                color = VoidBlack
            ) {}
        },
        containerColor = VoidBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Split Workspace Horizontal Panel Layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.55f)
            ) {
                // File explorer left sidebar
                AnimatedVisibility(
                    visible = showSidebar,
                    enter = slideInHorizontally() + fadeIn(),
                    exit = slideOutHorizontally() + fadeOut()
                ) {
                    FileExplorerPanel(
                        viewModel = viewModel,
                        files = files,
                        activeFile = activeFile,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(180.dp)
                            .background(VoidSurface)
                            .border(
                                width = 1.dp,
                                color = VoidSurfaceLight,
                                shape = RoundedCornerShape(0.dp)
                            )
                    )
                }

                // Center Main Code Editor or Binary Asset Viewer
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    if (activeFile != null) {
                        if (activeFile!!.isBinary) {
                            // Render AI Generated Visual graphics directly
                            BinaryAssetViewer(activeFile!!)
                        } else {
                            CodeEditorPanel(viewModel)
                        }
                    } else {
                        // Empty context indicator
                        EmptyEditorState()
                    }
                }
                
                // AI panel drawer
                AnimatedVisibility(
                    visible = showAiAssistant,
                    enter = expandHorizontally() + fadeIn(),
                    exit = shrinkHorizontally() + fadeOut()
                ) {
                    AiAssistantPanel(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(260.dp)
                            .background(VoidSurface)
                            .border(width = 1.dp, color = VoidSurfaceLight)
                    )
                }
            }

            // Divider spacer bar
            HorizontalDivider(color = VoidSurfaceLight, thickness = 1.dp)

            // Dynamic bottom multi-tools (Terminal output, HTML live view, Mod pack details)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.45f)
            ) {
                // Tab Selection Bar
                TabRow(
                    selectedTabIndex = when (selectedBottomTab) {
                        "TERMINAL" -> 0
                        "WEBVIEW" -> 1
                        else -> 2
                    },
                    containerColor = VoidBlack,
                    contentColor = VoidTeal,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[
                                when (selectedBottomTab) {
                                    "TERMINAL" -> 0
                                    "WEBVIEW" -> 1
                                    else -> 2
                                }
                            ]),
                            color = VoidTeal
                        )
                    }
                ) {
                    Tab(
                        selected = selectedBottomTab == "TERMINAL",
                        onClick = { selectedBottomTab = "TERMINAL" },
                        text = { Text("💬 Terminal", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                        modifier = Modifier.testTag("tab_terminal_terminal")
                    )
                    Tab(
                        selected = selectedBottomTab == "WEBVIEW",
                        onClick = { selectedBottomTab = "WEBVIEW" },
                        text = { Text("🌐 Web Sandbox", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                        modifier = Modifier.testTag("tab_terminal_webview")
                    )
                    Tab(
                        selected = selectedBottomTab == "MCPACK",
                        onClick = { selectedBottomTab = "MCPACK" },
                        text = { Text("📦 Modpack Specs", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                        modifier = Modifier.testTag("tab_terminal_mcpack")
                    )
                }

                // Dynamic display content based on selection
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (selectedBottomTab) {
                        "TERMINAL" -> TerminalConsole(viewModel)
                        "WEBVIEW" -> SimulatedWebviewSandbox(files)
                        "MCPACK" -> ModpackSpecPanel(files)
                    }
                }
            }
        }
    }
}

// ======================== PANEL COMPONENTS ========================

@Composable
fun FileExplorerPanel(
    viewModel: MainViewModel,
    files: List<ProjectFile>,
    activeFile: ProjectFile?,
    modifier: Modifier = Modifier
) {
    var showCreateFileDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FILES SYSTEM",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = VoidTextSecondary
            )
            IconButton(
                onClick = { showCreateFileDialog = true },
                modifier = Modifier
                    .size(20.dp)
                    .testTag("create_file_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New file in workspace",
                    tint = VoidTeal,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(files, key = { it.path }) { file ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (activeFile?.path == file.path) VoidSurfaceLight else Color.Transparent)
                        .clickable { viewModel.openFile(file) }
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val icon = when {
                            file.isBinary -> "🖼️"
                            file.path.endsWith(".html") -> "🌐"
                            file.path.endsWith(".css") -> "🎨"
                            file.path.endsWith(".js") -> "⚡"
                            file.path.endsWith(".json") -> "🛠️"
                            else -> "📄"
                        }
                        
                        Text(text = icon, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = file.path,
                            fontSize = 12.sp,
                            color = if (activeFile?.path == file.path) VoidTeal else VoidTextPrimary,
                            maxLines = 1
                        )
                    }
                    
                    IconButton(
                        onClick = { viewModel.deleteFileFromWorkspace(file.path) },
                        modifier = Modifier.size(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove file",
                            tint = VoidMagenta.copy(alpha = 0.6f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }

    if (showCreateFileDialog) {
        var filePathText by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showCreateFileDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = VoidSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, VoidTeal.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "🆕 CREATE FILE",
                        color = VoidTextPrimary,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = filePathText,
                        onValueChange = { filePathText = it },
                        label = { Text("File Name (e.g. game.js)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VoidTeal,
                            unfocusedBorderColor = VoidSurfaceLight,
                            focusedTextColor = VoidTextPrimary,
                            unfocusedTextColor = VoidTextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_file_input")
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showCreateFileDialog = false }) {
                            Text("CANCEL", color = VoidTextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.createNewFileInWorkspace(filePathText)
                                showCreateFileDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VoidTeal)
                        ) {
                            Text("CREATE", color = VoidBlack)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CodeEditorPanel(viewModel: MainViewModel) {
    val activeFile by viewModel.activeFile.collectAsState()
    val editorContent by viewModel.editorContent.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidDark)
    ) {
        // Quick visual keyboard shortcuts bar for typing source code easily on screen!
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(VoidBlack)
                .border(width = 0.5.dp, color = VoidSurfaceLight)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val keys = listOf("< >", "{ }", "[ ]", ";", "\" \"", "=", "+", "const", "function", "minecraft:", "void:")
            items(keys) { key ->
                Box(
                    modifier = Modifier
                        .background(VoidSurface, RoundedCornerShape(4.dp))
                        .clickable {
                            // Inject at end of current content for simple mobile interaction
                            viewModel.updateEditorContent(editorContent + " " + key.replace(" ", ""))
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = key,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = VoidTeal
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Simulated accurate Line numbers!
            val totalLines = editorContent.split('\n').size
            val lineNumbersString = remember(totalLines) {
                (1..totalLines).joinToString("\n")
            }

            Text(
                text = lineNumbersString,
                color = VoidPurpleGlow.copy(alpha = 0.5f),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                    .width(24.dp)
            )

            // Custom styled code field
            BasicTextField(
                value = editorContent,
                onValueChange = { viewModel.updateEditorContent(it) },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = VoidTextPrimary,
                    lineHeight = 18.sp
                ),
                cursorBrush = SolidColor(VoidTeal),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(8.dp)
                    .testTag("code_editor_field")
            )
        }
        
        // Auto-save live feedback label
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VoidBlack)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Live Auto-saver: active",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = VoidTextSecondary
            )
            Text(
                text = "File saved perfectly inside V.O.I.D. local database.",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = VoidTeal
            )
        }
    }
}

@Composable
fun BinaryAssetViewer(file: ProjectFile) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🖼️ Visual Graphic Package",
                color = VoidTeal,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            if (file.binaryData != null) {
                AsyncImage(
                    model = file.binaryData,
                    contentDescription = "Generative Image Asset",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, VoidTeal, RoundedCornerShape(8.dp))
                        .background(VoidSurface)
                )
            } else {
                Text("Error: Empty asset bytes.", color = VoidMagenta, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Path: ${file.path}",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = VoidTextSecondary
            )
        }
    }
}

@Composable
fun EmptyEditorState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidDark),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = VoidTextSecondary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "< Null Editor >",
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                color = VoidTextSecondary
            )
            Text(
                text = "Open a file inside the file explorer sidebar on the left.",
                fontSize = 10.sp,
                color = VoidTextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// ======================== BOTTOM EXPANDABLE TOOLS ========================

@Composable
fun TerminalConsole(viewModel: MainViewModel) {
    val logs by viewModel.terminalLogs.collectAsState()
    val scrollState = rememberLazyListState()
    var consoleInputText by remember { mutableStateOf("") }

    // Auto scroll terminal to latest
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            scrollState.animateScrollToItem(logs.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(8.dp)
    ) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(logs) { logLine ->
                val color = when {
                    logLine.startsWith("[ERROR]") -> VoidMagenta
                    logLine.startsWith("[SUCCESS]") -> VoidTeal
                    logLine.startsWith("[SYSTEM]") -> VoidPurpleGlow
                    logLine.contains("Domain Expansion:") -> VoidTeal // Easter Egg JJK Color
                    else -> VoidTextPrimary
                }
                
                Text(
                    text = logLine,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = color,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Command Prompt Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VoidSurface, RoundedCornerShape(4.dp))
                .border(0.5.dp, VoidSurfaceLight, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$ ",
                color = VoidTeal,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            BasicTextField(
                value = consoleInputText,
                onValueChange = { consoleInputText = it },
                textStyle = TextStyle(
                    color = VoidTextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                ),
                cursorBrush = SolidColor(VoidTeal),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        viewModel.executeTerminalCommand(consoleInputText)
                        consoleInputText = ""
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .testTag("terminal_input_field")
            )
        }
    }
}

@Composable
fun SimulatedWebviewSandbox(files: List<ProjectFile>) {
    val indexHtml = files.find { it.path == "index.html" }?.content ?: ""
    
    // Parse dynamic simulated actions from indexHtml
    var activeSquadName by remember { mutableStateOf("DJ Alok") }
    var actionLogText by remember { mutableStateOf("Simulation ready.") }
    var luckySpinResult by remember { mutableStateOf("Get ready for dynamic rewards...") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidDark)
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (indexHtml.isBlank()) {
            Text(
                text = "Wait... no HTML5 files detected inside the current workspace. Create an 'index.html' file to activate preview rendering.",
                color = VoidTextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            // If Gaming Template, display premium simulated UI!
            if (indexHtml.contains("FREE FIRE MAX")) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "📱 SIMULATED LIVE WEBVIEW PREVIEW (FREE FIRE MAX PORTAL)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VoidMagenta
                    )
                    
                    // Live dynamic panel
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VoidSurface),
                        border = BorderStroke(1.dp, VoidSurfaceLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🏆 Free Fire MAX Portal Live",
                                color = VoidTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Synergy Leader: $activeSquadName",
                                color = VoidTeal,
                                fontSize = 12.sp
                            )
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Dynamic Character Grid Selector inside preview
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        activeSquadName = "DJ Alok"
                                        actionLogText = "Drop the Beat synchronized! Team speeds +15%."
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VoidSurfaceLight),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("🎵 Alok", fontSize = 10.sp, color = VoidTextPrimary)
                                }
                                Button(
                                    onClick = {
                                        activeSquadName = "Chrono"
                                        actionLogText = "Shield Turner synced! Force field damage mitigation active."
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VoidSurfaceLight),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("🛡️ Chrono", fontSize = 10.sp, color = VoidTextPrimary)
                                }
                                Button(
                                    onClick = {
                                        activeSquadName = "Wukong"
                                        actionLogText = "Bush Camouflage sync complete. Reduced tracking priority."
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VoidSurfaceLight),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("🍃 Wukong", fontSize = 10.sp, color = VoidTextPrimary)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Loot spin
                            Text(
                                text = "Spin Weapon Wheel Crate:",
                                color = VoidTextSecondary,
                                fontSize = 11.sp
                            )
                            Button(
                                onClick = {
                                    val prizes = listOf(
                                        "Evolutionary MP40 Cobra Skin!",
                                        "Diamond Voucher x10",
                                        "Rare Incubator Blueprint",
                                        "Alok Shard Box",
                                        "Lucky Ticket"
                                    )
                                    luckySpinResult = "🏆 Winner: " + prizes.random()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VoidMagenta),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("SPIN WEB RUNNER WHEEL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VoidTextPrimary)
                            }
                            
                            Text(
                                text = luckySpinResult,
                                color = VoidTeal,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            // Combat Log
                            Text(
                                text = "Combat Simulation Logs:",
                                color = VoidTextSecondary,
                                fontSize = 11.sp
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            ) {
                                Text(
                                    text = actionLogText,
                                    fontFamily = FontFamily.Monospace,
                                    color = VoidTeal,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // Vanilla preview rendering
                Column {
                    Text(
                        text = "📱 SIMULATED WEBVIEW RUNNER MOCKUP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VoidTeal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VoidSurface)
                            .border(1.dp, VoidSurfaceLight)
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "DOM Rendering Success:",
                                color = VoidTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Draw raw source lines
                            Text(
                                text = indexHtml,
                                color = VoidTextPrimary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 10
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModpackSpecPanel(files: List<ProjectFile>) {
    val manifest = files.find { it.path == "manifest.json" }?.content ?: ""
    val hasMobs = files.any { it.path.contains("entities/herobrine.json") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidDark)
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (manifest.isBlank()) {
            Text(
                text = "No Minecraft behavior/manifest files detected. Spawn a '.mcpack Minecraft Addon' template on Hub dashboard to inspect configurations.",
                color = VoidTextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "📦 MINECRAFT BEDROCK PACK COMPILER SPECIFICATIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VoidTeal
                )
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = VoidSurface),
                    border = BorderStroke(1.dp, VoidSurfaceLight)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Addon: Herobrine Boss Creator", fontWeight = FontWeight.Bold, color = VoidTextPrimary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Manifest Registry: OK\nBehavior Packs Sync Status: LOADED\nResource Packs Controller: SYNCED",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = VoidTextSecondary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text("Summoning Info:", fontWeight = FontWeight.Bold, color = VoidTextPrimary, fontSize = 12.sp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "/summon void:herobrine",
                                fontFamily = FontFamily.Monospace,
                                color = VoidTeal,
                                fontSize = 11.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Packaging Checklist:\n- Behavior Entity Configs (entities/herobrine.json)\n- Resource Entity Renders (herobrine.entity.json)\n- Dynamic manifest UUID bindings.",
                            fontSize = 11.sp,
                            color = VoidTextSecondary
                        )
                    }
                }
            }
        }
    }
}

// ======================== AI ASSISTANT GHOST PANEL ========================

@Composable
fun AiAssistantPanel(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val chatHistory by viewModel.aiChatHistory.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    var aiQueryText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Column(modifier = modifier.padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    tint = VoidTeal,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "GEMINI GHOST",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = VoidTextPrimary
                )
            }
            
            Box(
                modifier = Modifier
                    .background(VoidSurfaceLight, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = "Flash 2.5", fontSize = 9.sp, color = VoidTeal, fontFamily = FontFamily.Monospace)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chats History Box
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(VoidBlack, RoundedCornerShape(8.dp))
                .border(0.5.dp, VoidSurfaceLight, RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatHistory) { bubble ->
                val aligning = if (bubble.sender == "user") Alignment.End else Alignment.Start
                val bg = if (bubble.sender == "user") VoidSurfaceLight else VoidSurface
                val border = if (bubble.sender == "user") VoidTeal else VoidPurpleGlow

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = aligning
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(bg)
                            .border(0.5.dp, border.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = bubble.message,
                            fontSize = 11.sp,
                            color = VoidTextPrimary,
                            lineHeight = 15.sp
                        )
                    }
                    Text(
                        text = if (bubble.sender == "user") "Operative" else "Gemini",
                        fontSize = 8.sp,
                        color = VoidTextSecondary,
                        modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                    )
                }
            }

            if (isAiLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = VoidTeal,
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 1.5.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gemini computing variables...", fontSize = 9.sp, color = VoidTextSecondary, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // AI shortcut templates
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val suggestions = listOf("Draw cool game banner", "Refactor Code", "Fix typos")
            for (action in suggestions) {
                Box(
                    modifier = Modifier
                        .background(VoidSurface, RoundedCornerShape(4.dp))
                        .clickable { aiQueryText = action }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(action, fontSize = 9.sp, color = VoidTextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // AI Field Prompt input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VoidSurface, RoundedCornerShape(6.dp))
                .border(0.5.dp, VoidSurfaceLight, RoundedCornerShape(6.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = aiQueryText,
                onValueChange = { aiQueryText = it },
                textStyle = TextStyle(color = VoidTextPrimary, fontSize = 11.sp),
                cursorBrush = SolidColor(VoidTeal),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        viewModel.handleAiSubmitPrompt(aiQueryText)
                        aiQueryText = ""
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .testTag("ai_prompt_field")
            )
            IconButton(
                onClick = {
                    viewModel.handleAiSubmitPrompt(aiQueryText)
                    aiQueryText = ""
                },
                modifier = Modifier.size(32.dp).testTag("ai_send_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Submit prompt",
                    tint = VoidTeal,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ======================== MODAL OVERLAY DIALOGS ========================

@Composable
fun SettingsDialog(viewModel: MainViewModel) {
    val savedToken by viewModel.vercelToken.collectAsState()
    var tokenInputText by remember { mutableStateOf(savedToken) }

    Dialog(onDismissRequest = { viewModel.toggleSettings(false) }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = VoidSurface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, VoidPurpleGlow.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "⚙️ OPERATIVE SETTINGS",
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = VoidTextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Secure local database storage (zero network upload, 100% encrypted in space).",
                    fontSize = 11.sp,
                    color = VoidTextSecondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = tokenInputText,
                    onValueChange = { tokenInputText = it },
                    label = { Text("Vercel REST API Token", color = VoidTextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VoidTeal,
                        unfocusedBorderColor = VoidSurfaceLight,
                        focusedTextColor = VoidTextPrimary,
                        unfocusedTextColor = VoidTextPrimary
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("vercel_token_field")
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { viewModel.toggleSettings(false) }) {
                        Text("CANCEL", color = VoidTextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            viewModel.saveVercelToken(tokenInputText)
                            viewModel.toggleSettings(false)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VoidTeal),
                        modifier = Modifier.testTag("save_settings_button")
                    ) {
                        Text("PERSIST CONFIG", color = VoidBlack)
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectCreateDialog(
    templateType: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var projectNameText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = VoidSurface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, VoidTeal.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "🚀 SPAWN OPERATIVE WORKSPACE",
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = VoidTextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Choose a name for your custom workspace. Files for $templateType template will compile automatically.",
                    fontSize = 11.sp,
                    color = VoidTextSecondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = projectNameText,
                    onValueChange = { projectNameText = it },
                    label = { Text("Workspace Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VoidTeal,
                        unfocusedBorderColor = VoidSurfaceLight,
                        focusedTextColor = VoidTextPrimary,
                        unfocusedTextColor = VoidTextPrimary
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("project_name_field")
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL", color = VoidTextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(projectNameText) },
                        colors = ButtonDefaults.buttonColors(containerColor = VoidTeal),
                        modifier = Modifier.testTag("confirm_project_create")
                    ) {
                        Text("LAUNCH SPACE", color = VoidBlack)
                    }
                }
            }
        }
    }
}
