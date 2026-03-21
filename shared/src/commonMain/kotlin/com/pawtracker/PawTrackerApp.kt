package com.pawtracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

/** Mock collar charge (0–100). At 100%, total runtime is [BATTERY_MAX_DAYS_AT_FULL_CHARGE] days. */
private const val DEMO_BATTERY_PERCENT = 82f
private const val BATTERY_MAX_DAYS_AT_FULL_CHARGE = 10f

private fun formatBatteryRuntimeEstimate(percent: Float): String {
    val days = (percent.coerceIn(0f, 100f) / 100f) * BATTERY_MAX_DAYS_AT_FULL_CHARGE
    val text = if (abs(days - days.roundToInt()) < 0.05f) {
        days.roundToInt().toString()
    } else {
        val tenths = (days * 10f).roundToInt()
        "${tenths / 10}.${abs(tenths % 10)}"
    }
    return "~$text days est."
}

private enum class PawTab(
    val label: String,
) {
    Home("Home"),
    Health("Health"),
    Near("Near"),
    AI("AI"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    PawTrackerTheme {
        var tab by remember { mutableStateOf(PawTab.Home) }
        var dogName by remember { mutableStateOf("Buddy") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "Paw Tracker",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                "Smart collar · ${getPlatformName()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                ) {
                    PawTab.entries.forEach { t ->
                        NavigationBarItem(
                            selected = tab == t,
                            onClick = { tab = t },
                            icon = {
                                Icon(
                                    when (t) {
                                        PawTab.Home -> Icons.Default.Home
                                        PawTab.Health -> Icons.Default.MonitorHeart
                                        PawTab.Near -> Icons.Default.LocationOn
                                        PawTab.AI -> Icons.Default.Psychology
                                    },
                                    contentDescription = t.label,
                                )
                            },
                            label = { Text(t.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        )
                    }
                }
            },
        ) { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            ),
                        ),
                    ),
            ) {
                when (tab) {
                    PawTab.Home -> HomeScreen(dogName = dogName)
                    PawTab.Health -> HealthScreen()
                    PawTab.Near -> NearScreen()
                    PawTab.AI -> AiAgentScreen(
                        dogName = dogName,
                        onDogNameChange = { dogName = it },
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(dogName: String) {
    val scroll = rememberScrollState()
    val displayName = dogName.trim().ifBlank { "your pup" }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            "Hi, $displayName",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        MoodHeroCard(mood = "Happy & playful", hint = "Tail wags up · relaxed breathing")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatChip(Icons.Default.Favorite, "118 bpm", "Heart")
            StatChip(Icons.Default.Pets, "2.4k", "Steps")
            StatChip(Icons.Default.BatteryChargingFull, "${DEMO_BATTERY_PERCENT.toInt()}%", "Battery")
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Emotional tracking", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Patterns look steady today. We will nudge you if stress spikes (noise, separation, or fatigue).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            "Tip: pair this with GPS safe zones so “far from home” alerts feel calm, not noisy.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MoodHeroCard(mood: String, hint: String) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        ),
                    ),
                )
                .padding(24.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Mood right now",
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    mood,
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    hint,
                    color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun RowScope.StatChip(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun HealthScreen() {
    val scroll = rememberScrollState()
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Health hub", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Heart rate", style = MaterialTheme.typography.labelLarge)
                Text(
                    "118",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text("bpm · resting", style = MaterialTheme.typography.bodyMedium)
                LinearProgressIndicator(
                    progress = 0.72f,
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                )
                Text(
                    "Main signal for wellness alerts (per your product plan).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MiniMetric("Steps", "2,412", "today")
            MiniMetric("Sleep", "7h 20m", "last night")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MiniMetric("Temp", "38.4°C", "skin")
            MiniMetric("Breaths", "28 / min", "calm")
        }
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("AI health insights", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Placeholder: on-device summaries + optional vet-friendly report export.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Pet hospital partners", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Cooperate with clinics you trust — share a short vitals snapshot before visits.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun RowScope.MiniMetric(title: String, value: String, caption: String) {
    Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(caption, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun NearScreen() {
    var radius by remember { mutableFloatStateOf(120f) }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Live GPS & safe range", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Card {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text("Map preview (frontend only)", textAlign = TextAlign.Center)
                    Text(
                        "Wire your map SDK here — range limit updates in real time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f))) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Distance from home", style = MaterialTheme.typography.titleSmall)
                Text("42 m · inside safe zone", fontWeight = FontWeight.SemiBold)
            }
        }
        Text("Safe radius: ${radius.toInt()} m", style = MaterialTheme.typography.titleSmall)
        Slider(
            value = radius,
            onValueChange = { radius = it },
            valueRange = 50f..500f,
        )
        Text(
            "Matches your todo: real-time GPS with a range limit you control.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private data class ChatMessage(
    val id: Int,
    val isUser: Boolean,
    val text: String,
)

private fun mockHealthAgentReply(userText: String, petName: String): String {
    val name = petName.trim().ifBlank { "your pet" }
    val m = userText.lowercase()
    return when {
        m.contains("health") || m.contains("vitals") || m.contains("okay") || m.contains("ok") ||
            m.contains("fine") || m.contains("feel") || m.contains("feeling") ->
            "From today’s collar signals for $name: resting heart looks calm (~118 bpm), activity is in a normal range, and we have not flagged sustained stress. If something seems off in real life (limping, eating, breathing), say what you are seeing and I will reason from that too."

        m.contains("today") || m.contains("day") || m.contains("went") ->
            "Quick read on $name’s day: mood looked mostly upbeat, steps around 2.4k, and no big anxiety spikes in the trace. Want a shorter or longer summary?"

        m.contains("sleep") || m.contains("rest") ->
            "Sleep last night looked solid (about 7h 20m in the demo data). If $name was restless, tell me and we can correlate with evening noise or activity."

        m.contains("eat") || m.contains("food") || m.contains("appetite") ->
            "I do not have live bowl data in this mock — for $name, combine what you observed with vitals: a sudden drop in activity plus skipped meals is worth a vet call."

        m.contains("thank") || m.contains("thanks") ->
            "You are welcome. Ask anytime about $name’s day or health snapshot."

        else ->
            "I am here to chat about $name’s health and how their day went. Try: “How is $name’s health?” or “How was today?”"
    }
}

@Composable
private fun AiAgentScreen(
    dogName: String,
    onDogNameChange: (String) -> Unit,
) {
    var vibes by remember { mutableStateOf(true) }
    val pet = dogName.trim().ifBlank { "your pet" }
    val listState = rememberLazyListState()
    var input by remember { mutableStateOf("") }
    var idSeq by remember(pet) { mutableIntStateOf(1) }
    val messages = remember(pet) {
        mutableStateListOf(
            ChatMessage(
                0,
                false,
                "Hi! Ask me how $pet’s health looks, or how their day went — I use your tracker demo data for now.",
            ),
        )
    }

    LaunchedEffect(messages.size) {
        delay(48)
        val count = listState.layoutInfo.totalItemsCount
        if (count > 0) listState.scrollToItem(count - 1)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text("AI agent", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    "On-device intelligence and chat — all in one place.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Dog name", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = dogName,
                            onValueChange = { if (it.length <= 28) onDogNameChange(it) },
                            label = { Text("Pet name") },
                            placeholder = { Text("e.g. Buddy") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            "Used in your home greeting and in chat.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            item {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("What it does", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Monitors movement and vitals, surfaces behavior patterns, and can push alerts when something looks off.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            item {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Battery", style = MaterialTheme.typography.titleMedium)
                        LinearProgressIndicator(
                            progress = DEMO_BATTERY_PERCENT / 100f,
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                        )
                        Text(
                            "${DEMO_BATTERY_PERCENT.toInt()}% · ${formatBatteryRuntimeEstimate(DEMO_BATTERY_PERCENT)}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            "No screen on the collar — more battery for the AI module, voice, and vitals.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            item {
                Card {
                    Row(
                        Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Gentle vibrations", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Find me / calm cue",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(checked = vibes, onCheckedChange = { vibes = it })
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Firmware", style = MaterialTheme.typography.titleSmall)
                        Text("v0.9.12-dev (UI mock)", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            item {
                Spacer(Modifier.height(4.dp))
                Text("Health chat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "Message the AI about $pet’s wellbeing or today’s summary.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(messages, key = { it.id }) { msg ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start,
                ) {
                    Card(
                        modifier = Modifier.widthIn(max = 320.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (msg.isUser) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                        ),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (msg.isUser) 16.dp else 4.dp,
                            bottomEnd = if (msg.isUser) 4.dp else 16.dp,
                        ),
                    ) {
                        Text(
                            msg.text,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("e.g. How was today? How is $pet?") },
                maxLines = 4,
            )
            IconButton(
                onClick = {
                    val trimmed = input.trim()
                    if (trimmed.isEmpty()) return@IconButton
                    messages.add(ChatMessage(idSeq++, true, trimmed))
                    input = ""
                    messages.add(ChatMessage(idSeq++, false, mockHealthAgentReply(trimmed, pet)))
                },
                enabled = input.trim().isNotEmpty(),
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}
