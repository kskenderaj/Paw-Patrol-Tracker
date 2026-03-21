@file:OptIn(ExperimentalMaterial3Api::class)

package com.pawtracker

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

enum class CollarSize(val label: String) {
    Small("Small"),
    Medium("Medium"),
    Large("Large"),
}

data class CollarColorChoice(val name: String, val color: Color)

/** Tuned to match the physical smart-collar reference (safety orange strap, gold hardware, matte module). */
val collarColorChoices: List<CollarColorChoice> = listOf(
    CollarColorChoice("Signal", Color(0xFFFF7F27)),
    CollarColorChoice("Blaze", Color(0xFFFF6B35)),
    CollarColorChoice("Tangerine", Color(0xFFFF9800)),
    CollarColorChoice("Ember", Color(0xFFE65100)),
    CollarColorChoice("Coral", Color(0xFFFF8A65)),
    CollarColorChoice("Sand", Color(0xFFFFF3E0)),
    CollarColorChoice("Gold", Color(0xFFD4AF37)),
    CollarColorChoice("Graphite", Color(0xFF455A64)),
    CollarColorChoice("Cocoa", Color(0xFF6D4C41)),
    CollarColorChoice("Ink", Color(0xFF37474F)),
)

enum class CollarPattern(val label: String) {
    Solid("Solid color"),
    Weave("Fabric weave"),
    Stripes("Diagonal stripes"),
    Dots("Polka dots"),
    Hearts("Hearts"),
    Stars("Stars"),
    Zigzag("Zigzag pattern"),
    Paws("Paw prints"),
    Waves("Wavy lines"),
    Camo("Camouflage"),
    Plaid("Plaid check"),
}

/** Demo lifecycle for the smart collar hub (idle → movement → alert). */
enum class CollarHubPhase {
    Idle,
    Active,
    Alert,
}

/** Fixed orange safety-collar look (not user-customizable). */
private object AiCollarTheme {
    val bandDeep = Color(0xFFBF360C)
    val bandMid = Color(0xFFFF6B35)
    val bandLight = Color(0xFFFFAB91)
    val accent = Color(0xFFFFC107)
    val accentGlow = Color(0xFFFF9800)
    val accentDim = Color(0xFFF57C00)
    val patternInk = Color(0xFFE65100)
    val nameOnBand = Color(0xFFFFF8F0)
    val moduleFace = Color(0xFF3E2723)
    val moduleFaceActive = Color(0xFF5D4037)
}

@Composable
fun CollarPreviewBand(
    dogName: String,
    modifier: Modifier = Modifier,
    simulateHubCycle: Boolean = true,
) {
    val baseColor = AiCollarTheme.bandMid
    val patternColor = AiCollarTheme.patternInk
    val pattern = CollarPattern.Weave
    val bandHeight = 54.dp
    val nameSize = 14.sp
    val displayName = dogName.trim().ifBlank { "Your dog" }
    val ink = AiCollarTheme.nameOnBand
    val bandShape = RoundedCornerShape(12.dp)
    val density = LocalDensity.current.density

    var phase by remember { mutableStateOf(CollarHubPhase.Idle) }
    LaunchedEffect(simulateHubCycle) {
        if (!simulateHubCycle) return@LaunchedEffect
        while (true) {
            delay(6000)
            phase = when (phase) {
                CollarHubPhase.Idle -> CollarHubPhase.Active
                CollarHubPhase.Active -> CollarHubPhase.Alert
                CollarHubPhase.Alert -> CollarHubPhase.Idle
            }
        }
    }

    val dataFlowTransition = rememberInfiniteTransition(label = "dataFlow")
    val dataFlowShift by dataFlowTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
        ),
    )

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val auraElevation = when (phase) {
            CollarHubPhase.Idle -> 6.dp
            CollarHubPhase.Active -> 14.dp
            CollarHubPhase.Alert -> 12.dp
        }
        val auraAmbient = when (phase) {
            CollarHubPhase.Alert -> Color(0xFFEF4444).copy(alpha = 0.45f)
            else -> AiCollarTheme.accentGlow.copy(alpha = 0.38f)
        }
        val auraSpot = when (phase) {
            CollarHubPhase.Alert -> Color(0xFFDC2626).copy(alpha = 0.55f)
            else -> AiCollarTheme.accent.copy(alpha = 0.45f)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(bandHeight)
                .shadow(
                    elevation = auraElevation + 4.dp,
                    shape = bandShape,
                    clip = false,
                    ambientColor = auraAmbient,
                    spotColor = auraSpot,
                ),
        ) {
            BoxWithConstraints(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationX = -15f
                        cameraDistance = 14f * density
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
                    .clip(bandShape),
            ) {
                val bandW = maxWidth
                val trackerOffsetTarget = when (phase) {
                    CollarHubPhase.Idle -> bandW * 0.08f
                    CollarHubPhase.Active -> bandW * 0.42f
                    CollarHubPhase.Alert -> bandW * 0.1f
                }
                val trackerX by animateDpAsState(
                    targetValue = trackerOffsetTarget,
                    animationSpec = tween(1000),
                    label = "trackerSlide",
                )

                val isMoving = phase == CollarHubPhase.Active
                val trackerElevation = if (isMoving) 10.dp else 3.dp
                val trackerColor = when (phase) {
                    CollarHubPhase.Idle -> AiCollarTheme.moduleFace
                    CollarHubPhase.Active -> AiCollarTheme.moduleFaceActive
                    CollarHubPhase.Alert -> Color(0xFFC62828)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(bandShape)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                            bandShape,
                        ),
                ) {
                    // Layer 1 — orange collar band + warm specular
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colorStops = arrayOf(
                                        0f to AiCollarTheme.bandDeep,
                                        0.14f to AiCollarTheme.bandMid,
                                        0.32f to AiCollarTheme.accent.copy(alpha = 0.65f),
                                        0.48f to AiCollarTheme.bandLight,
                                        0.62f to AiCollarTheme.bandMid,
                                        0.82f to AiCollarTheme.bandDeep,
                                        1f to Color(0xFF8D2800),
                                    ),
                                ),
                            ),
                    )
                    // Layer 2 — vertical falloff: top highlight + bottom occlusion (rounded cross-section)
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0f to Color.White.copy(alpha = 0.16f),
                                        0.35f to Color.Transparent,
                                        0.72f to Color.Black.copy(alpha = 0.06f),
                                        1f to Color.Black.copy(alpha = 0.14f),
                                    ),
                                ),
                            ),
                    )
                    // Layer 2a — edge vignette (strap curves away at sides)
                    Canvas(Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.1f),
                                ),
                                center = Offset(w * 0.5f, h * 0.42f),
                                radius = max(w, h) * 0.85f,
                            ),
                            topLeft = Offset.Zero,
                            size = size,
                        )
                    }
                    // Layer 2b — woven nylon texture (always on; “Fabric weave” pattern adds more on top)
                    Canvas(Modifier.fillMaxSize()) {
                        drawFabricWeave(baseColor, strength = 0.24f, tightness = 1f)
                    }
                    // Layer 3 — data stream shimmer
                    Canvas(Modifier.fillMaxSize()) {
                        val w = size.width
                        val t = (dataFlowShift / 1000f).coerceIn(0f, 1f)
                        val x0 = t * (w + 120f) - 60f
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    AiCollarTheme.accent.copy(alpha = 0.32f),
                                    Color.White.copy(alpha = 0.12f),
                                    Color.Transparent,
                                ),
                                start = Offset(x0, 0f),
                                end = Offset(x0 + w * 0.45f, size.height),
                            ),
                        )
                    }

                    PatternOverlay(pattern = pattern, patternColor = patternColor)

                    // Specular rib (top edge catch-light on curved strap)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth(0.72f)
                            .height(5.dp)
                            .offset(y = (-3).dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0f),
                                        Color.White.copy(alpha = 0.28f),
                                        Color.White.copy(alpha = 0.22f),
                                        Color.White.copy(alpha = 0f),
                                    ),
                                ),
                            ),
                    )

                    Text(
                        text = displayName.uppercase(),
                        color = ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = nameSize,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 56.dp),
                    )

                    // Smart tracker module + pulsing LED
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(vertical = 4.dp, horizontal = 6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .offset(x = trackerX)
                                .graphicsLayer {
                                    rotationX = -6f
                                    cameraDistance = 10f * density
                                    transformOrigin = TransformOrigin(0.5f, 1f)
                                }
                                .shadow(
                                    elevation = trackerElevation + 2.dp,
                                    shape = RoundedCornerShape(9.dp),
                                    clip = false,
                                    ambientColor = Color.Black.copy(alpha = if (isMoving) 0.22f else 0.12f),
                                    spotColor = AiCollarTheme.accent.copy(alpha = if (isMoving) 0.5f else 0.22f),
                                )
                                .clip(RoundedCornerShape(9.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colorStops = arrayOf(
                                            0f to lightenColor(trackerColor, 0.12f),
                                            0.38f to trackerColor,
                                            1f to darkenColor(trackerColor, 0.2f),
                                        ),
                                    ),
                                )
                                .border(1.dp, AiCollarTheme.accent.copy(alpha = 0.55f), RoundedCornerShape(9.dp))
                                .padding(horizontal = 4.dp, vertical = 3.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 3.dp, height = 12.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xFFFFF8E1),
                                                    HardwareGold,
                                                    Color(0xFF8D6E1F),
                                                ),
                                            ),
                                        )
                                        .border(0.5.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(2.dp)),
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CollarStatusLedStrip(phase = phase)
                                    Text(
                                        text = "AI",
                                        color = textOnCollar(trackerColor),
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** LED + sensor strip similar to the physical module (G / R / sensor / …). */
@Composable
private fun CollarStatusLedStrip(phase: CollarHubPhase) {
    val inf = key(phase) { rememberInfiniteTransition(label = "sensorPulse") }
    val pulse by inf.animateFloat(
        initialValue = 1f,
        targetValue = when (phase) {
            CollarHubPhase.Active -> 1.18f
            CollarHubPhase.Alert -> 1.12f
            else -> 1.06f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (phase) {
                    CollarHubPhase.Active -> 380
                    CollarHubPhase.Alert -> 500
                    else -> 900
                },
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    val green = Color(0xFF66BB6A)
    val red = Color(0xFFEF5350)
    val grey = Color(0xFF9E9E9E)
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(width = 3.dp, height = 4.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(grey),
        )
        Box(Modifier.size(3.dp).clip(CircleShape).background(green))
        Box(Modifier.size(3.dp).clip(CircleShape).background(red))
        Box(Modifier.size(3.dp).clip(CircleShape).background(red))
        Box(
            modifier = Modifier
                .size(4.dp)
                .graphicsLayer {
                    scaleX = pulse
                    scaleY = pulse
                    transformOrigin = TransformOrigin.Center
                }
                .clip(CircleShape)
                .background(Color(0xFF212121)),
        )
        Box(Modifier.size(3.dp).clip(CircleShape).background(green))
        Box(Modifier.size(3.dp).clip(CircleShape).background(red))
    }
}

private fun Color.luminance(): Float {
    val r = red
    val g = green
    val b = blue
    return 0.299f * r + 0.587f * g + 0.114f * b
}

private fun darkenColor(c: Color, amount: Float): Color = Color(
    max(0f, c.red - amount),
    max(0f, c.green - amount),
    max(0f, c.blue - amount),
    c.alpha,
)

private fun lightenColor(c: Color, amount: Float): Color = Color(
    min(1f, c.red + amount),
    min(1f, c.green + amount),
    min(1f, c.blue + amount),
    c.alpha,
)

/** Pattern ink in the same hue family as the chosen pattern color. */
private fun patternStroke(ink: Color): Color {
    val dark = ink.luminance() > 0.52f
    val c = if (dark) darkenColor(ink, 0.26f) else lightenColor(ink, 0.06f)
    return c.copy(alpha = 0.52f)
}

private fun patternFill(ink: Color): Color {
    val dark = ink.luminance() > 0.52f
    val c = if (dark) darkenColor(ink, 0.1f) else lightenColor(ink, 0.16f)
    return c.copy(alpha = 0.36f)
}

private fun patternOutline(ink: Color): Color {
    val dark = ink.luminance() > 0.52f
    val c = if (dark) darkenColor(ink, 0.38f) else darkenColor(ink, 0.1f)
    return c.copy(alpha = 0.48f)
}

/** Stable micro-jitter so repeats aren’t on a rigid grid (reduces “fixed” look). */
private fun patternJitter(px: Float, py: Float, amount: Float): Offset {
    val a = (sin((px * 0.11 + py * 0.07).toDouble()) * amount).toFloat()
    val b = (cos((px * 0.09 - py * 0.13).toDouble()) * amount).toFloat()
    return Offset(a, b)
}

private fun blendTowardNeutral(c: Color, t: Float): Color {
    val tt = t.coerceIn(0f, 1f)
    val n = if (c.luminance() > 0.5f) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    return Color(
        red = c.red * (1f - tt) + n.red * tt,
        green = c.green * (1f - tt) + n.green * tt,
        blue = c.blue * (1f - tt) + n.blue * tt,
        alpha = c.alpha,
    )
}

private val HardwareGold = Color(0xFFD4AF37)

/** Woven nylon / canvas cross-hatch like the physical strap (subtle at low strength). */
private fun DrawScope.drawFabricWeave(baseColor: Color, strength: Float, tightness: Float = 1f) {
    val w = size.width
    val h = size.height
    val step = (2.4f / tightness).coerceIn(1.6f, 3.8f)
    val dark = darkenColor(baseColor, 0.2f).copy(alpha = 0.03f + strength * 0.11f)
    val light = lightenColor(baseColor, 0.1f).copy(alpha = 0.025f + strength * 0.09f)
    var y = 0f
    while (y < h) {
        drawLine(
            color = dark,
            start = Offset(0f, y),
            end = Offset(w, y),
            strokeWidth = 0.55f,
            cap = StrokeCap.Round,
        )
        y += step
    }
    var x = 0f
    while (x < w) {
        drawLine(
            color = light,
            start = Offset(x, 0f),
            end = Offset(x, h),
            strokeWidth = 0.5f,
            cap = StrokeCap.Round,
        )
        x += step
    }
}

private fun textOnCollar(baseColor: Color): Color =
    if (baseColor.luminance() > 0.52f) Color(0xFF3E2723) else Color(0xFFFFF8F0)

@Composable
private fun PatternOverlay(pattern: CollarPattern, patternColor: Color) {
    val stroke = patternStroke(patternColor)
    val fill = patternFill(patternColor)
    val outline = patternOutline(patternColor)
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val m = min(w, h).coerceAtLeast(1f)
        val u = (m / 56f).coerceIn(0.78f, 1.45f)
        fun lineStroke(px: Float) = Stroke(
            width = px * u,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        when (pattern) {
            CollarPattern.Solid -> {}
            CollarPattern.Weave -> drawFabricWeave(patternColor, strength = 0.52f, tightness = 1.35f)
            CollarPattern.Stripes -> {
                val step = 12f * u
                val sw = 4.2f * u
                var x = -h
                while (x < w + h) {
                    drawLine(
                        color = stroke,
                        start = Offset(x, h),
                        end = Offset(x + h * 0.35f, 0f),
                        strokeWidth = sw,
                        cap = StrokeCap.Round,
                    )
                    x += step
                }
            }
            CollarPattern.Dots -> {
                val step = 15f * u
                val rOuter = 5f * u
                val rMid = 3.6f * u
                val rCore = 2.2f * u
                var y = step * 0.55f
                while (y < h) {
                    var x = step * 0.55f
                    while (x < w) {
                        val j = patternJitter(x, y, 1.1f * u)
                        val c = Offset(x + j.x, y + j.y)
                        drawCircle(color = outline.copy(alpha = 0.28f), radius = rOuter, center = c)
                        drawCircle(color = fill.copy(alpha = 0.45f), radius = rMid, center = c)
                        drawCircle(
                            color = blendTowardNeutral(fill, 0.2f).copy(alpha = 0.55f),
                            radius = rCore,
                            center = c,
                        )
                        x += step
                    }
                    y += step
                }
            }
            CollarPattern.Hearts -> {
                val step = 26f * u
                var x = 8f * u
                while (x < w) {
                    val j = patternJitter(x, h * 0.5f, 0.8f * u)
                    drawHeart(Offset(x + j.x, h * 0.5f + j.y), 6.5f * u, fill, outline, 1.1f * u)
                    x += step
                }
            }
            CollarPattern.Stars -> {
                val step = 22f * u
                var x = 11f * u
                while (x < w) {
                    val j = patternJitter(x, h * 0.5f, 0.8f * u)
                    drawStar(Offset(x + j.x, h * 0.5f + j.y), 7.2f * u, fill, outline, 1.35f * u)
                    x += step
                }
            }
            CollarPattern.Zigzag -> {
                val path = Path()
                path.moveTo(0f, h * 0.5f)
                var x = 0f
                var up = true
                val seg = 11f * u
                while (x <= w) {
                    val ny = if (up) h * 0.2f else h * 0.8f
                    path.lineTo(x, ny)
                    up = !up
                    x += seg
                }
                path.lineTo(w, h * 0.5f)
                drawPath(path, color = stroke, style = lineStroke(3.8f))
            }
            CollarPattern.Paws -> {
                val step = 26f * u
                var x = 13f * u
                while (x < w) {
                    val j = patternJitter(x, h * 0.55f, 0.9f * u)
                    drawPaw(Offset(x + j.x, h * 0.55f + j.y), 5.5f * u, fill, outline)
                    x += step
                }
            }
            CollarPattern.Waves -> {
                val wave = 20f * u
                val path = Path()
                path.moveTo(0f, h * 0.32f)
                var x = 0f
                while (x <= w) {
                    path.quadraticBezierTo(x + wave * 0.5f, h * 0.06f, x + wave, h * 0.32f)
                    x += wave
                }
                drawPath(path, color = stroke, style = lineStroke(3.2f))
                val path2 = Path()
                path2.moveTo(0f, h * 0.68f)
                x = 0f
                while (x <= w) {
                    path2.quadraticBezierTo(x + wave * 0.5f, h * 0.94f, x + wave, h * 0.68f)
                    x += wave
                }
                drawPath(path2, color = stroke.copy(alpha = 0.48f), style = lineStroke(3f))
            }
            CollarPattern.Camo -> {
                val ox = 19f * u
                val oy = 15f * u
                var i = 0
                var x = -ox * 0.3f
                while (x < w + ox) {
                    var y = -oy * 0.3f
                    while (y < h + oy) {
                        val j = patternJitter(x + y, x * 0.31f + y * 0.17f, 2f * u)
                        val c = when (i++ % 3) {
                            0 -> fill.copy(alpha = 0.42f)
                            1 -> stroke.copy(alpha = 0.38f)
                            else -> darkenColor(patternColor, 0.14f).copy(alpha = 0.4f)
                        }
                        val left = Offset(x + j.x, y + j.y)
                        val cx = left.x + ox * 0.55f
                        val cy = left.y + oy * 0.5f
                        drawOval(
                            brush = Brush.radialGradient(
                                colors = listOf(c, c.copy(alpha = 0f)),
                                center = Offset(cx, cy),
                                radius = max(ox, oy) * 0.95f,
                            ),
                            topLeft = left,
                            size = Size(ox * 1.1f, oy * 1.05f),
                        )
                        y += oy * 0.85f
                    }
                    x += ox * 0.9f
                }
            }
            CollarPattern.Plaid -> {
                val gx = 16f * u
                val gy = 13f * u
                var x = 0f
                while (x < w) {
                    drawLine(
                        color = stroke,
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 2.2f * u,
                        cap = StrokeCap.Round,
                    )
                    x += gx
                }
                var y = 0f
                while (y < h) {
                    drawLine(
                        color = stroke.copy(alpha = 0.55f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 2f * u,
                        cap = StrokeCap.Round,
                    )
                    y += gy
                }
            }
        }
    }
}

private fun DrawScope.drawHeart(
    center: Offset,
    r: Float,
    fill: Color,
    outline: Color,
    outlineWidth: Float,
) {
    val p = Path()
    p.moveTo(center.x, center.y + r)
    p.cubicTo(
        center.x - r * 1.2f, center.y - r * 0.2f,
        center.x - r * 1.2f, center.y - r * 1.2f,
        center.x, center.y - r * 0.8f,
    )
    p.cubicTo(
        center.x + r * 1.2f, center.y - r * 1.2f,
        center.x + r * 1.2f, center.y - r * 0.2f,
        center.x, center.y + r,
    )
    drawPath(p, color = fill)
    drawPath(
        p,
        color = outline,
        style = Stroke(
            width = outlineWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        ),
    )
}

private fun DrawScope.drawStar(
    c: Offset,
    r: Float,
    fill: Color,
    outline: Color,
    outlineWidth: Float,
) {
    val path = Path()
    val n = 5
    for (i in 0 until n * 2) {
        val rad = if (i % 2 == 0) r else r * 0.45f
        val ang = i * PI / n - PI / 2
        val x = c.x + (cos(ang) * rad).toFloat()
        val y = c.y + (sin(ang) * rad).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color = fill)
    drawPath(
        path,
        color = outline,
        style = Stroke(
            width = outlineWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        ),
    )
}

private fun DrawScope.drawPaw(c: Offset, r: Float, fill: Color, outline: Color) {
    fun pad(offset: Offset, rad: Float) {
        drawCircle(outline.copy(alpha = 0.32f), rad + 1.1f, offset)
        drawCircle(fill.copy(alpha = 0.45f), rad * 0.88f, offset)
        drawCircle(blendTowardNeutral(fill, 0.15f).copy(alpha = 0.58f), rad * 0.55f, offset)
    }
    pad(Offset(c.x, c.y), r)
    pad(Offset(c.x - r * 1.1f, c.y - r * 0.4f), r * 0.55f)
    pad(Offset(c.x + r * 1.1f, c.y - r * 0.4f), r * 0.55f)
    pad(Offset(c.x - r * 1.3f, c.y + r * 0.5f), r * 0.5f)
    pad(Offset(c.x + r * 1.3f, c.y + r * 0.5f), r * 0.5f)
}

@Composable
fun CollarSizeRow(
    selected: CollarSize,
    onSelect: (CollarSize) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CollarSize.entries.forEach { s ->
            FilterChip(
                selected = selected == s,
                onClick = { onSelect(s) },
                label = { Text(s.label) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun CollarColorRow(
    choices: List<CollarColorChoice>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        choices.chunked(5).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { choice ->
                    val idx = choices.indexOf(choice)
                    val picked = selectedIndex == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(choice.color)
                            .border(
                                width = if (picked) 3.dp else 1.dp,
                                color = if (picked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                shape = CircleShape,
                            )
                            .clickable { onSelect(idx) },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (picked) {
                            Text("✓", color = if (choice.color.luminance() > 0.55f) Color(0xFF4E342E) else Color.White, fontSize = 14.sp)
                        }
                    }
                }
                repeat(5 - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CollarPatternGrid(
    selected: CollarPattern,
    onSelect: (CollarPattern) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CollarPattern.entries.forEach { p ->
            FilterChip(
                selected = selected == p,
                onClick = { onSelect(p) },
                label = {
                    Text(
                        text = p.label,
                        maxLines = 2,
                        softWrap = true,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
