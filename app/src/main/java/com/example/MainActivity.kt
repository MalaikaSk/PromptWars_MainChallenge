package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.api.SerenityResponse
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRose
import com.example.ui.theme.CalmEmerald
import com.example.ui.theme.CalmTeal
import com.example.ui.theme.CozyBorder
import com.example.ui.theme.CozySurface
import com.example.ui.theme.DeepSlateBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmGold
import com.example.viewmodel.SerenityUiState
import com.example.viewmodel.SerenityViewModel
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DeepSlateBg
                ) { innerPadding ->
                    SerenityApp(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SerenityApp(
    modifier: Modifier = Modifier,
    viewModel: SerenityViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reflectionText by viewModel.reflectionText.collectAsState()
    val chosenPreference by viewModel.guidancePreference.collectAsState()

    var showSafetyTip by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSlateBg)
            .drawBehind {
                // Top-Left Blue Glow (approx #1E3A8A / #3B82F6 at 15% opacity)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x243B82F6), Color.Transparent),
                        center = Offset(-size.width * 0.1f, -size.height * 0.1f),
                        radius = size.width * 0.7f
                    ),
                    center = Offset(-size.width * 0.1f, -size.height * 0.1f),
                    radius = size.width * 0.7f
                )
                // Bottom-Right Teal Glow (approx #115E59 / #14B8A6 at 15% opacity)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x2414B8A6), Color.Transparent),
                        center = Offset(size.width * 1.1f, size.height * 0.85f),
                        radius = size.width * 0.6f
                    ),
                    center = Offset(size.width * 1.1f, size.height * 0.85f),
                    radius = size.width * 0.6f
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    // S Icon with gradient to-tr from-blue-400 to-teal-400 (CalmEmerald & CalmTeal)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(CalmEmerald, CalmTeal),
                                    start = Offset(0f, 0f),
                                    end = Offset(1f, 1f)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "S",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepSlateBg
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Serenity AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                        color = TextPrimary,
                        letterSpacing = (-0.3).sp
                    )
                }

                // Profile Access Circle/Info Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CozySurface)
                        .border(1.dp, CozyBorder, CircleShape)
                        .clickable { showSafetyTip = !showSafetyTip },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Quick Toggle Notice",
                        tint = CalmTeal,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Safety Warning/Boundary Banner
            AnimatedVisibility(visible = showSafetyTip) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = CozySurface.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Important Notice",
                            tint = WarmGold,
                            modifier = Modifier
                                .size(20.dp)
                                .offset(y = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "A Safe Space for Reflection",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Serenity AI acts as an emotional soundboard to help you find calm and focus. It is not a clinical therapist or emergency channel. If you are in high distress, please seek support from loved ones or professionals.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 17.sp
                            )
                        }
                        IconButton(
                            onClick = { showSafetyTip = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Notice",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Demo Mode Notice
            if (!viewModel.hasValidApiKey()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = CozySurface.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, WarmGold.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Demo Mode",
                            tint = WarmGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "💡 Offline Serenity Mode: Generating comforting responses via our tailored local reflection engine without requiring API keys.",
                            fontSize = 12.sp,
                            color = WarmGold,
                            lineHeight = 17.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            when (uiState) {
                is SerenityUiState.Idle, is SerenityUiState.Error -> {
                    // Show Input View
                    SerenityInputForm(
                        reflectionText = reflectionText,
                        chosenPreference = chosenPreference,
                        onTextChange = { viewModel.updateReflectionText(it) },
                        onPreferenceSelect = { viewModel.updateGuidancePreference(it) },
                        onSubmit = { simulate -> viewModel.analyzeAndProvideWisdom(simulate) },
                        errorMessage = (uiState as? SerenityUiState.Error)?.message,
                        onResetError = { viewModel.resetState() }
                    )
                }

                is SerenityUiState.Loading -> {
                    // Calm Loading State
                    SerenityLoadingView()
                }

                is SerenityUiState.Success -> {
                    // Wisdom Companion Output
                    val successState = uiState as SerenityUiState.Success
                    SerenityOutputView(
                        response = successState.response,
                        onReset = { viewModel.resetState() },
                        isMock = successState.isMock
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SerenityInputForm(
    reflectionText: String,
    chosenPreference: String,
    onTextChange: (String) -> Unit,
    onPreferenceSelect: (String) -> Unit,
    onSubmit: (Boolean) -> Unit,
    errorMessage: String?,
    onResetError: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    // Preset prompts
    val presets = listOf(
        PresetPrompt("Exhausted & overwhelmed", "I feel exhausted and overwhelmed. Everything feels too heavy right now."),
        PresetPrompt("Disappointing exam results", "I studied hard but my exam results were poor. I feel stupid and unmotivated."),
        PresetPrompt("Anxious of future", "I feel so anxious and lost about my future after graduation. I do not know where to go."),
        PresetPrompt("Lonely & isolated", "I feel deeply lonely, unmotivated, and isolated from my friends. It is hard to begin simple tasks.")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 600.dp)
    ) {
        // Form Title
        Text(
            text = "Tell us how you are feeling...",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Large Reflection Text Area
        OutlinedTextField(
            value = reflectionText,
            onValueChange = {
                onTextChange(it)
                if (errorMessage != null) onResetError()
            },
            placeholder = {
                Text(
                    text = "Reflect honestly on your feelings today... E.g., 'I feel burnout from my classes and constant failure...'",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .testTag("reflection_input"),
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = CozySurface.copy(alpha = 0.5f),
                unfocusedContainerColor = CozySurface.copy(alpha = 0.3f),
                focusedBorderColor = CalmEmerald,
                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        // Counter and Error Message
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = AccentRose,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
            Text(
                text = "${reflectionText.length} chars",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Preset Chips
        Text(
            text = "Or tap a safe reflection scenario:",
            fontSize = 13.sp,
            color = CalmTeal,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CozySurface.copy(alpha = if (reflectionText == preset.textValue) 1f else 0.4f))
                        .border(
                            width = 1.dp,
                            color = if (reflectionText == preset.textValue) CalmEmerald else TextSecondary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            onTextChange(preset.textValue)
                            onResetError()
                            focusManager.clearFocus()
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = preset.label,
                        color = if (reflectionText == preset.textValue) CalmEmerald else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Guidance Preference Selection Title
        Text(
            text = "Select your Wisdom Path:",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Row of Premium Segmented Option-Chips for Guidance Preference
        val preferences = listOf(
            GuidanceOption("General Wisdom", "📜", "general"),
            GuidanceOption("Islamic Reflection", "🌙", "islamic"),
            GuidanceOption("Christian Reflection", "✝️", "christian"),
            GuidanceOption("Hindu Reflection", "🕉️", "hindu")
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            preferences.forEach { option ->
                val isSelected = chosenPreference == option.name
                val borderCol by animateColorAsState(targetValue = if (isSelected) CalmEmerald else TextSecondary.copy(alpha = 0.15f))
                val bgCol by animateColorAsState(targetValue = if (isSelected) CalmEmerald.copy(alpha = 0.12f) else CozySurface.copy(alpha = 0.4f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(bgCol)
                        .border(1.dp, borderCol, RoundedCornerShape(14.dp))
                        .clickable {
                            onPreferenceSelect(option.name)
                            focusManager.clearFocus()
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("guidance_selector_${option.tagKey}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option.icon,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = option.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) CalmEmerald else TextPrimary
                        )
                        val desc = when(option.name) {
                            "General Wisdom" -> "Original timeless philosophical perspective"
                            "Islamic Reflection" -> "Quranic verses, historical contexts & motivation"
                            "Christian Reflection" -> "Comforting Bible verses and compassionate teachings"
                            "Hindu Reflection" -> "Bhagavad Gita insight & centering advice"
                            else -> ""
                        }
                        Text(
                            text = desc,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = CalmEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Main Submit / Seek Wisdom Button (Styled with beautiful from-blue-500 to-teal-500 gradient)
        Button(
            onClick = {
                focusManager.clearFocus()
                onSubmit(false)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("btn_seek_wisdom"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color(0xFF0F1419)
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(CalmEmerald, CalmTeal)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Seek Serenity & Reflection",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SerenityLoadingView() {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Slow pulsing soft circle drawing back reflection
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                CalmEmerald.copy(alpha = pulseAlpha * 0.4f),
                                CalmTeal.copy(alpha = pulseAlpha * 0.1f),
                                Color.Transparent
                            )
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CalmEmerald.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = CalmEmerald.copy(alpha = pulseAlpha),
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Gathering comforting wisdom...",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Please take a gentle, slow breath in & out.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // Pulse dot visual indicator
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { dI ->
                val delay = dI * 300
                val dotAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(900, delayMillis = delay),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CalmEmerald.copy(alpha = dotAlpha))
                )
            }
        }
    }
}

@Composable
fun SerenityOutputView(
    response: SerenityResponse,
    onReset: () -> Unit,
    isMock: Boolean
) {
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    // Practical checklist state manager
    // Allows checking off 3 recommended actions for therapeutic reward loop
    val stepCheckedList = remember { mutableStateListOf(false, false, false) }
    // Initialize checked list accurately to list size
    while (stepCheckedList.size < response.practical_steps.size) {
        stepCheckedList.add(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 600.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back Navigation Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            OutlinedButton(
                onClick = onReset,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CalmEmerald),
                border = BorderStroke(1.dp, CalmEmerald.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_reset")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reflect Again", fontSize = 13.sp)
                }
            }
        }

        // Title and Mood Diagnosis Indicators
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CozySurface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, CozyBorder)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Pulsing amber dot
                        val pulseTransition = rememberInfiniteTransition()
                        val pulseAlpha by pulseTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1.0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000),
                                repeatMode = RepeatMode.Reverse
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(WarmGold.copy(alpha = pulseAlpha))
                        )
                        Text(
                            text = "CURRENT STATE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    }

                    // Stress Indicator badge
                    val stressCol = when(response.stress_level.lowercase()) {
                        "high" -> AccentRose
                        "medium" -> WarmGold
                        else -> AccentGreen
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(stressCol.copy(alpha = 0.12f))
                            .border(1.dp, stressCol.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${response.stress_level} Stress",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = stressCol
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Emotions analyzed chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (response.primary_emotion.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(CalmEmerald.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Emotion: ${response.primary_emotion}",
                                fontSize = 11.sp,
                                color = CalmEmerald,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    if (response.root_cause.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(WarmGold.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Source: ${response.root_cause}",
                                fontSize = 11.sp,
                                color = WarmGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Empathetic Summary
                Text(
                    text = response.emotion_summary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextPrimary.copy(alpha = 0.9f),
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 2: Holy Wisdom Quote Card (Stylized centering & left bordered background context)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Big wisdom quote
                Text(
                    text = "“${response.wisdom_quote}”",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFFDBEAFE), // Elegant blue-100 text
                    lineHeight = 29.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Citation
                Text(
                    text = response.source.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.2.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Section 3: Historical or Contextual Background Styled Context Block
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CalmEmerald.copy(alpha = 0.05f))
                        .drawBehind {
                            // Left border (border-l-2 border-blue-400/30)
                            drawLine(
                                color = CalmEmerald.copy(alpha = 0.35f),
                                start = Offset(0f, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 3.dp.toPx()
                            )
                        }
                        .padding(start = 18.dp, top = 14.dp, end = 14.dp, bottom = 14.dp)
                ) {
                    Column {
                        Text(
                            text = "HISTORICAL CONTEXT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CalmTeal,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = response.historical_context,
                            fontSize = 13.sp,
                            color = TextPrimary.copy(alpha = 0.85f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 4: Personalized Connection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CozySurface),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.11f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "🤝 COMPASSIONATE REFLECTION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalmTeal,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = response.personalized_reflection,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    lineHeight = 21.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 5: Practical Action Steps & Checklists (Styled into an interactive 3-column grid)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = "PRACTICAL STEPS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                response.practical_steps.forEachIndexed { sIndex, stepText ->
                    if (sIndex < stepCheckedList.size) {
                        val isChecked = stepCheckedList[sIndex]
                        val cardBg by animateColorAsState(
                            targetValue = if (isChecked) CalmTeal.copy(alpha = 0.15f) else CozySurface
                        )
                        val borderCol by animateColorAsState(
                            targetValue = if (isChecked) CalmTeal.copy(alpha = 0.7f) else CozyBorder
                        )

                        // Select step icon emoji depending on keywords
                        val emoji = when {
                            stepText.lowercase().contains("breath") || stepText.lowercase().contains("inhale") || stepText.lowercase().contains("exhale") -> "🧘"
                            stepText.lowercase().contains("walk") || stepText.lowercase().contains("outside") || stepText.lowercase().contains("run") -> "🚶"
                            stepText.lowercase().contains("journal") || stepText.lowercase().contains("write") || stepText.lowercase().contains("unload") || stepText.lowercase().contains("mind") -> "📓"
                            stepText.lowercase().contains("water") || stepText.lowercase().contains("drink") || stepText.lowercase().contains("sip") || stepText.lowercase().contains("tea") -> "🍵"
                            stepText.lowercase().contains("stretch") || stepText.lowercase().contains("body") || stepText.lowercase().contains("yoga") -> "🤸"
                            sIndex == 0 -> "🧘"
                            sIndex == 1 -> "🚶"
                            else -> "📓"
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(cardBg)
                                .border(1.dp, borderCol, RoundedCornerShape(20.dp))
                                .clickable { stepCheckedList[sIndex] = !isChecked }
                                .padding(horizontal = 8.dp, vertical = 14.dp)
                                .testTag("checklist_item_$sIndex"),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Text(
                                text = stepText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isChecked) CalmTeal else TextPrimary.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 7: Ambient Nature Generation Canvas
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CozySurface),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "🎨 ARTWORK & NATURE VISION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalmTeal,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "This calming generator prompt replicates your feeling and transitions it to hope. You can copy this for AI generator tools.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Generates customized slow wave art matching their emotion
                AmbientSerenityCanvas(
                    primaryEmotion = response.primary_emotion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Prompt description text field copy container
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DeepSlateBg.copy(alpha = 0.6f))
                        .border(1.dp, TextSecondary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = response.image_prompt,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(response.image_prompt))
                                isCopied = true
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = CalmEmerald.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.Share,
                                contentDescription = "Copy Prompt",
                                tint = CalmEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = if (isCopied) "Copied!" else "Copy",
                            fontSize = 9.sp,
                            color = CalmTeal,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 6: Gentle Encouragement Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .drawBehind {
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                CalmEmerald.copy(alpha = 0.05f),
                                CalmTeal.copy(alpha = 0.05f)
                            )
                        )
                    )
                }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = response.encouragement,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = WarmGold,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Premium full width Reset button inside footer
        Button(
            onClick = onReset,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("btn_reset_bottom"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color(0xFF0F1419)
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(CalmEmerald, CalmTeal)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Reflect Again",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Custom beautiful responsive wave illustrations matching analyzed feelings!
@Composable
fun AmbientSerenityCanvas(
    primaryEmotion: String,
    modifier: Modifier = Modifier
) {
    val emotionKey = primaryEmotion.lowercase()
    val isSad = emotionKey.contains("lonely") || emotionKey.contains("unmotivated") || emotionKey.contains("sad")
    val isTired = emotionKey.contains("exhaust") || emotionKey.contains("burn") || emotionKey.contains("weary") || emotionKey.contains("fatigue")
    val isDisappointed = emotionKey.contains("fail") || emotionKey.contains("poor") || emotionKey.contains("doubt")

    // Animations of slow waves
    val infiniteTransition = rememberInfiniteTransition()
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = { it }),
            repeatMode = RepeatMode.Restart
        )
    )

    // Palette depending on emotion
    val colors = when {
        isSad -> listOf(
            Color(0xFF2E86C1), // Peaceful Lake Blue
            Color(0xFF1ABC9C), // Calming Mint
            Color(0xFF2C3E50)  // Deep Evening Slate
        )
        isTired -> listOf(
            Color(0xFF7D3C98), // Restful Lavender
            Color(0xFF3498DB), // Cool Wind Blue
            Color(0xFF1C2833)  // Quiet Evening Midnight
        )
        isDisappointed -> listOf(
            Color(0xFFE59866), // Sunset Amber
            Color(0xFFE74C3C).copy(alpha = 0.4f), // Peach Glow
            Color(0xFF5D6D7E)  // Soft Mountain Mist
        )
        else -> listOf(
            Color(0xFF16A085), // Emerald Growth
            Color(0xFFF1C40F), // Sunbeam Gold
            Color(0xFF2E4053)  // Comforting Deep Grey
        )
    }

    Canvas(
        modifier = modifier
    ) {
        val width = size.width
        val height = size.height

        // Draw standard deep matching background gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(colors[2], Color(0xFF0F172A))
            )
        )

        // Draw multiple beautiful slow undulating sine-wave curves to mimic flowing hills or gentle ocean horizon
        drawWavePair(colors, waveOffset, width, height, 0.45f, 0.12f, 1)
        drawWavePair(colors, -waveOffset + 1.25f, width, height, 0.55f, 0.08f, 2)
        drawWavePair(colors, waveOffset * 0.7f, width, height, 0.65f, 0.06f, 3)

        // Draw small soft sun of hope rising gently
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors[1].copy(alpha = 0.5f), Color.Transparent),
                center = Offset(width * 0.8f, height * 0.35f),
                radius = 80.dp.toPx()
            ),
            center = Offset(width * 0.8f, height * 0.35f),
            radius = 80.dp.toPx()
        )
    }
}

private fun DrawScope.drawWavePair(
    colors: List<Color>,
    offset: Float,
    width: Float,
    height: Float,
    baseHeightPercent: Float,
    amplitudePercent: Float,
    waveNum: Int
) {
    val path = Path()
    path.moveTo(0f, height)
    
    val baseHeight = height * baseHeightPercent
    val amp = height * amplitudePercent

    for (x in 0..width.toInt() step 5) {
        // Compose smooth sine wave
        val angle = (x.toFloat() / width) * 2f * Math.PI.toFloat() * 1.2f + offset
        val y = baseHeight + sin(angle) * amp
        path.lineTo(x.toFloat(), y)
    }
    path.lineTo(width, height)
    path.close()

    val paintBrush = if (waveNum == 1) {
        Brush.verticalGradient(
            colors = listOf(colors[0].copy(alpha = 0.4f), colors[2].copy(alpha = 0.2f))
        )
    } else if (waveNum == 2) {
        Brush.verticalGradient(
            colors = listOf(colors[1].copy(alpha = 0.3f), colors[2].copy(alpha = 0.1f))
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(colors[0].copy(alpha = 0.2f), Color.Transparent)
        )
    }

    drawPath(
        path = path,
        brush = paintBrush
    )
}

// Helpers
data class PresetPrompt(
    val label: String,
    val textValue: String
)

data class GuidanceOption(
    val name: String,
    val icon: String,
    val tagKey: String
)
