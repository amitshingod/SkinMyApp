package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FavoriteEntity
import com.example.data.HistoryEntity
import com.example.ui.*
import com.example.ui.theme.AccentColors
import com.example.ui.theme.AccentNames
import com.example.ui.theme.PureBlack
import com.example.utils.AppInfo
import com.example.utils.IconHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Custom Frosted Glass Card
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    borderWidth: Dp = 1.dp,
    isDark: Boolean = isSystemInDarkTheme(),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val baseSurface = if (isDark) Color(0x0FFFFFFF) else Color(0x99FFFFFF)
    val borderColor = if (isDark) Color(0x1BFFFFFF) else Color(0x33000000)

    val cardModifier = if (onClick != null) {
        modifier.clickable(
            onClick = onClick,
            interactionSource = remember { MutableInteractionSource() },
            indication = LocalIndication.current
        )
    } else {
        modifier
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = baseSurface),
        border = BorderStroke(borderWidth, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(content = content)
    }
}

// Background animated radial blobs
@Composable
fun AnimatedBlobBackground(isDark: Boolean, animationSpeed: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "blobs")
    
    val durationMultiplier = (1.0f / animationSpeed.coerceAtLeast(0.1f))
    
    val xOffset1 by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween((12000 * durationMultiplier).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x1"
    )
    val yOffset1 by infiniteTransition.animateFloat(
        initialValue = 100f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween((16000 * durationMultiplier).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y1"
    )

    val xOffset2 by infiniteTransition.animateFloat(
        initialValue = 800f,
        targetValue = -100f,
        animationSpec = infiniteRepeatable(
            animation = tween((14000 * durationMultiplier).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x2"
    )
    val yOffset2 by infiniteTransition.animateFloat(
        initialValue = 1100f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween((18000 * durationMultiplier).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y2"
    )

    val xOffset3 by infiniteTransition.animateFloat(
        initialValue = 300f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween((15000 * durationMultiplier).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x3"
    )
    val yOffset3 by infiniteTransition.animateFloat(
        initialValue = 1300f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween((17000 * durationMultiplier).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y3"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) PureBlack else Color(0xFFF3F5F8))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (isDark) {
                // Cyber Blue Glow (bg-blue-600/20 style)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x332563EB), Color.Transparent),
                        center = Offset(xOffset1, yOffset1),
                        radius = 800f
                    ),
                    center = Offset(xOffset1, yOffset1),
                    radius = 800f
                )
                // Purple Glow (bg-purple-600/20 style)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x339333EA), Color.Transparent),
                        center = Offset(xOffset2, yOffset2),
                        radius = 900f
                    ),
                    center = Offset(xOffset2, yOffset2),
                    radius = 900f
                )
                // Indigo Glow (bg-indigo-500/10 style)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x1A6366F1), Color.Transparent),
                        center = Offset(xOffset3, yOffset3),
                        radius = 850f
                    ),
                    center = Offset(xOffset3, yOffset3),
                    radius = 850f
                )
            } else {
                // Light Mode soft pink/blue blobs
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x1500E5FF), Color.Transparent),
                        center = Offset(xOffset1, yOffset1),
                        radius = 700f
                    ),
                    center = Offset(xOffset1, yOffset1),
                    radius = 700f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x15E040FB), Color.Transparent),
                        center = Offset(xOffset2, yOffset2),
                        radius = 800f
                    ),
                    center = Offset(xOffset2, yOffset2),
                    radius = 800f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x0F6366F1), Color.Transparent),
                        center = Offset(xOffset3, yOffset3),
                        radius = 750f
                    ),
                    center = Offset(xOffset3, yOffset3),
                    radius = 750f
                )
            }
        }
    }
}

// Preset color selections
val BgSolidPresetColors = listOf(
    0xFF0A0A0A.toInt(), // Pure Black
    0xFF1F1F1F.toInt(), // Dark Gray
    0xFF00E5FF.toInt(), // Cyber Blue
    0xFFE040FB.toInt(), // Electric Purple
    0xFF39FF14.toInt(), // Neon Green
    0xFFFF4E50.toInt(), // Sunset Orange
    0xFFFFEB3B.toInt(), // Yellow
    0xFFFF2A6D.toInt(), // Cyber Pink
    0xFF05D9E8.toInt(), // Electric Cyan
    0xFF01012B.toInt()  // Deep Space
)

val BgGradientPresets = listOf(
    listOf(0xFFFF4E50.toInt(), 0xFFF9D423.toInt()), // Sunset
    listOf(0xFF00C6FF.toInt(), 0xFF0072FF.toInt()), // Ocean Cyan
    listOf(0xFFE040FB.toInt(), 0xFF00E5FF.toInt()), // Synthwave
    listOf(0xFF39FF14.toInt(), 0xFF00E5FF.toInt()), // Matrix
    listOf(0xFF8A2387.toInt(), 0xFFE94057.toInt(), 0xFFF27121.toInt()), // Cosmic Purple
    listOf(0xFF11998e.toInt(), 0xFF38ef7d.toInt())  // Emerald Aurora
)

val BorderPresetColors = listOf(
    0xFFFFFFFF.toInt(), // Pure White
    0xFF00E5FF.toInt(), // Cyber Blue
    0xFFE040FB.toInt(), // Electric Purple
    0xFF39FF14.toInt(), // Neon Green
    0xFFFF4E50.toInt(), // Sunset Orange
    0xFFFFEB3B.toInt(), // Cyber Yellow
    0x80FFFFFF.toInt()  // Frosted Glass
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val isDarkSettings by viewModel.themeMode.collectAsStateWithLifecycle()
    val accentColorIndex by viewModel.accentColorIndex.collectAsStateWithLifecycle()
    val animationSpeed by viewModel.animationSpeed.collectAsStateWithLifecycle()
    
    val actualAccentColor = AccentColors[accentColorIndex]
    val darkTheme = when (isDarkSettings) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    var currentTab by remember { mutableStateOf("home") }

    // Observe shortcut success/failure
    LaunchedEffect(key1 = true) {
        viewModel.shortcutStatus.collectLatest { status ->
            when (status) {
                "SUCCESS" -> Toast.makeText(context, "Shortcut Created Successfully!", Toast.LENGTH_SHORT).show()
                "FAILURE" -> Toast.makeText(context, "Failed to create shortcut! Make sure launcher permission is enabled.", Toast.LENGTH_LONG).show()
                "RESET_ALL" -> Toast.makeText(context, "All settings & history reset", Toast.LENGTH_SHORT).show()
            }
        }
    }

    MyApplicationTheme(darkTheme = darkTheme, accentColor = actualAccentColor) {
        val customizingApp by viewModel.customizingApp.collectAsStateWithLifecycle()

        Box(modifier = Modifier.fillMaxSize()) {
            // Floating blurred gradients behind
            AnimatedBlobBackground(isDark = darkTheme, animationSpeed = animationSpeed)

            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    if (customizingApp == null) {
                        CustomBottomNavigation(
                            currentTab = currentTab,
                            onTabSelected = { currentTab = it },
                            accentColor = actualAccentColor,
                            isDark = darkTheme
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (customizingApp != null) {
                        CustomizationScreen(
                            viewModel = viewModel,
                            isDark = darkTheme,
                            accentColor = actualAccentColor
                        )
                    } else {
                        when (currentTab) {
                            "home" -> AppGridScreen(
                                viewModel = viewModel,
                                isDark = darkTheme,
                                accentColor = actualAccentColor
                            )
                            "history" -> HistoryScreen(
                                viewModel = viewModel,
                                isDark = darkTheme,
                                accentColor = actualAccentColor
                            )
                            "settings" -> SettingsScreen(
                                viewModel = viewModel,
                                isDark = darkTheme,
                                accentColor = actualAccentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

// Bottom navigation custom premium bar with Glassmorphism
@Composable
fun CustomBottomNavigation(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    accentColor: Color,
    isDark: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            cornerRadius = 32.dp,
            borderWidth = 1.dp,
            isDark = isDark,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(16.dp, RoundedCornerShape(32.dp), ambientColor = accentColor, spotColor = accentColor)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home
                BottomNavItem(
                    label = "Explore",
                    iconSelected = Icons.Default.GridView,
                    iconUnselected = Icons.Default.GridView,
                    isSelected = currentTab == "home",
                    accentColor = accentColor,
                    onClick = { onTabSelected("home") }
                )
                // History
                BottomNavItem(
                    label = "History",
                    iconSelected = Icons.Default.History,
                    iconUnselected = Icons.Default.History,
                    isSelected = currentTab == "history",
                    accentColor = accentColor,
                    onClick = { onTabSelected("history") }
                )
                // Settings
                BottomNavItem(
                    label = "Settings",
                    iconSelected = Icons.Default.Settings,
                    iconUnselected = Icons.Default.Settings,
                    isSelected = currentTab == "settings",
                    accentColor = accentColor,
                    onClick = { onTabSelected("settings") }
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    iconSelected: androidx.compose.ui.graphics.vector.ImageVector,
    iconUnselected: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "item_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSelected) iconSelected else iconUnselected,
            contentDescription = label,
            tint = if (isSelected) accentColor else Color.Gray.copy(alpha = 0.8f),
            modifier = Modifier
                .size(26.dp)
                .scale(scale)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) accentColor else Color.Gray.copy(alpha = 0.8f)
        )
    }
}

// HOME / EXPLORE SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppGridScreen(
    viewModel: MainViewModel,
    isDark: Boolean,
    accentColor: Color
) {
    val isLoading by viewModel.isLoadingApps.collectAsStateWithLifecycle()
    val filteredApps by viewModel.filteredApps.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val activeSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val gridSize by viewModel.gridSize.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        // App Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "OneShot ",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color.White else Color.Black
                    )
                    Text(
                        text = "IconX",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                }
                Text(
                    text = "PRO CUSTOMIZER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) Color(0x0FFFFFFF) else Color(0x0D000000))
                    .border(
                        1.dp,
                        if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar with glass styling
        GlassCard(
            cornerRadius = 22.dp,
            borderWidth = 1.dp,
            isDark = isDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Gray,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                TextField(
                    value = query,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Search installed apps...", color = if (isDark) Color.White.copy(alpha = 0.3f) else Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("search_input"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = if (isDark) Color.White else Color.Black,
                        unfocusedTextColor = if (isDark) Color.White else Color.Black
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                )
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filters scrollbar
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(AppFilter.values()) { filter ->
                val isSelected = filter == activeFilter
                val filterName = when (filter) {
                    AppFilter.ALL -> "All Apps"
                    AppFilter.USER -> "User"
                    AppFilter.SYSTEM -> "System"
                    AppFilter.FAVORITES -> "Favorites"
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (isSelected) {
                                accentColor.copy(alpha = 0.15f)
                            } else {
                                if (isDark) Color(0x09FFFFFF) else Color(0x33FFFFFF)
                            }
                        )
                        .border(
                            1.dp,
                            if (isSelected) {
                                accentColor.copy(alpha = 0.4f)
                            } else {
                                if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f)
                            },
                            CircleShape
                        )
                        .clickable { viewModel.selectedFilter.value = filter }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = filterName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) accentColor else if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sorting toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredApps.size} apps found",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    viewModel.selectedSort.value = if (activeSort == AppSort.ALPHABETICAL) {
                        AppSort.RECENTLY_INSTALLED
                    } else {
                        AppSort.ALPHABETICAL
                    }
                }
            ) {
                Icon(
                    imageVector = if (activeSort == AppSort.ALPHABETICAL) Icons.Default.SortByAlpha else Icons.Default.NewReleases,
                    contentDescription = "Sort",
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (activeSort == AppSort.ALPHABETICAL) "Alphabetical" else "Recently Installed",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid Content
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = accentColor)
            }
        } else if (filteredApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No applications found",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridSize),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredApps) { app ->
                    val isFavorite = favorites.any { it.packageName == app.packageName }
                    
                    // App Card
                    AppCardItem(
                        app = app,
                        isFavorite = isFavorite,
                        isDark = isDark,
                        accentColor = accentColor,
                        onCardClick = { viewModel.startCustomization(app) },
                        onFavoriteToggle = { viewModel.toggleFavorite(app.packageName) }
                    )
                }
            }
        }
    }
}

@Composable
fun AppCardItem(
    app: AppInfo,
    isFavorite: Boolean,
    isDark: Boolean,
    accentColor: Color,
    onCardClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    val context = LocalContext.current
    
    // Dynamically load the icon only when required in Grid
    val drawable = remember(app.packageName) { IconHelper.getAppIcon(context, app.packageName) }
    val bitmap = remember(drawable) { drawable?.let { IconHelper.drawableToBitmap(it) } }

    GlassCard(
        cornerRadius = 28.dp,
        borderWidth = 1.dp,
        isDark = isDark,
        onClick = onCardClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_item_${app.packageName}")
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Favorite Button on Card
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(36.dp)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) accentColor else Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App Launcher Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isDark) Color(0x26FFFFFF) else Color(0x14000000))
                        .border(
                            1.dp,
                            if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f),
                            RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = app.label,
                            modifier = Modifier.size(44.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Name
                Text(
                    text = app.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Packagename
                Text(
                    text = app.packageName.split(".").lastOrNull() ?: "",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// HISTORY SCREEN
@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    isDark: Boolean,
    accentColor: Color
) {
    val history by viewModel.history.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Customization History",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color.White else Color.Black
                )
                Text(
                    text = "Restore or trigger previous creations",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            if (history.isNotEmpty()) {
                IconButton(onClick = { viewModel.resetAllSettings() }) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear History",
                        tint = Color.Red.copy(alpha = 0.8f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your creation history is empty",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Customize icons on the explore screen first!",
                        color = Color.Gray.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history) { item ->
                    HistoryItemCard(
                        item = item,
                        isDark = isDark,
                        accentColor = accentColor,
                        onRecreate = {
                            val bitmap = IconHelper.base64ToBitmap(item.base64Icon)
                            if (bitmap != null) {
                                val launchIntent = context.packageManager.getLaunchIntentForPackage(item.packageName)
                                val resolvedActivity = launchIntent?.resolveActivity(context.packageManager)
                                val activityClass = resolvedActivity?.className ?: ""
                                
                                val success = IconHelper.pinShortcut(
                                    context = context,
                                    customName = item.customName,
                                    packageName = item.packageName,
                                    launcherActivityClassName = activityClass,
                                    iconBitmap = bitmap
                                )
                                if (success) {
                                    Toast.makeText(context, "Shortcut Pin Requested!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Request failed!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onDelete = { viewModel.deleteHistoryItem(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    item: HistoryEntity,
    isDark: Boolean,
    accentColor: Color,
    onRecreate: () -> Unit,
    onDelete: () -> Unit
) {
    val bitmap = remember(item.base64Icon) { IconHelper.base64ToBitmap(item.base64Icon) }

    GlassCard(
        cornerRadius = 20.dp,
        borderWidth = 1.dp,
        isDark = isDark,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Created custom icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0x1AFFFFFF) else Color(0x0A000000)),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = item.customName,
                        modifier = Modifier.size(44.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.customName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black
                )
                Text(
                    text = "Original: ${item.originalLabel}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.packageName,
                    fontSize = 9.sp,
                    color = accentColor.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Quick Recreate Pin button
            IconButton(onClick = onRecreate) {
                Icon(
                    imageVector = Icons.Default.AddBox,
                    contentDescription = "Pin Shortcut again",
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Delete History button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete from history",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// SETTINGS SCREEN
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    isDark: Boolean,
    accentColor: Color
) {
    val activeTheme by viewModel.themeMode.collectAsStateWithLifecycle()
    val columnsSize by viewModel.gridSize.collectAsStateWithLifecycle()
    val activeColorIndex by viewModel.accentColorIndex.collectAsStateWithLifecycle()
    val animationSpeed by viewModel.animationSpeed.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Settings & Style",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color.White else Color.Black
                )
                Text(
                    text = "Tweak customization preferences",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        // Section: Visual Identity
        item {
            Text(
                text = "VISUAL IDENTITY & THEME",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                letterSpacing = 1.sp
            )
        }

        item {
            GlassCard(isDark = isDark, cornerRadius = 24.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Accent Palette",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Grid of colors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AccentColors.forEachIndexed { index, color ->
                            val isSelected = index == activeColorIndex
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) (if (isDark) Color.White else Color.Black) else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.setAccentColorIndex(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (color == Color.Yellow) Color.Black else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Current: ${AccentNames[activeColorIndex]}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        item {
            GlassCard(isDark = isDark, cornerRadius = 24.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "System Theme Mode",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeMode.values().forEach { mode ->
                            val isSelected = mode == activeTheme
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) accentColor else if (isDark) Color(0x11FFFFFF) else Color(0xCCFFFFFF))
                                    .border(
                                        1.dp,
                                        if (isSelected) accentColor else if (isDark) Color(0x15FFFFFF) else Color(0x4DFFFFFF),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setThemeMode(mode) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mode.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else if (isDark) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Grid Layout Density
        item {
            Text(
                text = "APP GRID SIZE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                letterSpacing = 1.sp
            )
        }

        item {
            GlassCard(isDark = isDark, cornerRadius = 24.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Columns Count",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(3, 4, 5).forEach { columns ->
                            val isSelected = columns == columnsSize
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) accentColor else if (isDark) Color(0x11FFFFFF) else Color(0xCCFFFFFF))
                                    .border(
                                        1.dp,
                                        if (isSelected) accentColor else if (isDark) Color(0x15FFFFFF) else Color(0x4DFFFFFF),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setGridSize(columns) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$columns Columns",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else if (isDark) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Animation Speed
        item {
            Text(
                text = "MOTION GRAPHICS SPEED",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                letterSpacing = 1.sp
            )
        }

        item {
            GlassCard(isDark = isDark, cornerRadius = 24.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Background Blobs Speed",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black
                        )
                        Text(
                            text = "${"%.1f".format(animationSpeed)}x",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Slider(
                        value = animationSpeed,
                        onValueChange = { viewModel.setAnimationSpeed(it) },
                        valueRange = 0.2f..3.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = accentColor,
                            activeTrackColor = accentColor
                        )
                    )
                }
            }
        }

        // Reset All
        item {
            Button(
                onClick = { viewModel.resetAllSettings() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Reset All Settings & History", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // About Box
        item {
            GlassCard(isDark = isDark, cornerRadius = 24.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "OneShot IconX v1.0",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color.White else Color.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Developed natively for Android 8.0+ and Android 16",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This app enables customized app icons by creating system launcher shortcuts via the ShortcutManager API. All customized icons are stored and managed fully offline.",
                        fontSize = 12.sp,
                        color = Color.Gray.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// CUSTOMIZATION / ICON EDITOR SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationScreen(
    viewModel: MainViewModel,
    isDark: Boolean,
    accentColor: Color
) {
    val app by viewModel.customizingApp.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (app == null) return

    val originalName = app?.label ?: ""
    val name by viewModel.customName.collectAsStateWithLifecycle()
    
    // Sliders
    val angle by viewModel.rotation.collectAsStateWithLifecycle()
    val zoomFactor by viewModel.zoom.collectAsStateWithLifecycle()
    val brightVal by viewModel.brightness.collectAsStateWithLifecycle()
    val contrastVal by viewModel.contrast.collectAsStateWithLifecycle()
    val satVal by viewModel.saturation.collectAsStateWithLifecycle()
    val opacVal by viewModel.opacity.collectAsStateWithLifecycle()
    val hueVal by viewModel.hue.collectAsStateWithLifecycle()

    // Shape / Borders
    val currentShape by viewModel.shapeType.collectAsStateWithLifecycle()
    val radiusPercent by viewModel.cornerRadiusPercent.collectAsStateWithLifecycle()
    val borderWidthPercent by viewModel.borderWidthPercent.collectAsStateWithLifecycle()
    val borderColorValue by viewModel.borderColorValue.collectAsStateWithLifecycle()

    // BG
    val solidColor by viewModel.bgSolidColor.collectAsStateWithLifecycle()
    val gradientColors by viewModel.bgGradientColors.collectAsStateWithLifecycle()

    // Gallery
    val galleryBitmap by viewModel.galleryBitmap.collectAsStateWithLifecycle()

    // Load original icon
    val originalDrawable = remember(app?.packageName) { app?.let { IconHelper.getAppIcon(context, it.packageName) } }

    // Computed preview bitmap (throttled/calculated on demand)
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val scope = rememberCoroutineScope()

    // Whenever any editor parameter changes, regenerate the preview bitmap
    LaunchedEffect(
        app, angle, zoomFactor, brightVal, contrastVal,
        satVal, opacVal, hueVal, currentShape, radiusPercent,
        borderWidthPercent, borderColorValue, solidColor, gradientColors,
        galleryBitmap, originalDrawable
    ) {
        val baseDrawable = originalDrawable ?: return@LaunchedEffect
        val baseBitmap = galleryBitmap ?: IconHelper.drawableToBitmap(baseDrawable)
        
        val output = viewModel.generateConfiguredBitmap(baseBitmap)
        previewBitmap = output
    }

    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.loadGalleryImage(uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.resetEditorStates(); viewModel.loadInstalledApps() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isDark) Color.White else Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Customize Icon",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.White else Color.Black
            )
        }

        // Live Preview Panel styled as a gorgeous glass mockup from the design HTML
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(if (isDark) Color(0x11FFFFFF) else Color(0x99FFFFFF))
                .border(
                    width = 1.dp,
                    color = if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(32.dp)
                )
        ) {
            // Reflection effect gradient
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isDark) 0.08f else 0.35f),
                                Color.Transparent
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(400f, 400f)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Custom Icon Frame: size 72.dp with border and inner container
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isDark) Color(0x15FFFFFF) else Color(0x0F000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (previewBitmap != null) {
                                Image(
                                    bitmap = previewBitmap!!.asImageBitmap(),
                                    contentDescription = "Icon Preview",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                CircularProgressIndicator(color = accentColor, strokeWidth = 2.dp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Labels and Badges
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NEW SHORTCUT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = accentColor
                            )
                            Text(
                                text = originalName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.5f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Label",
                                tint = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.5f),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Package: ${app?.packageName}",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive text field to change shortcut name
                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.customName.value = it },
                    label = { Text("App Display Label", fontSize = 11.sp) },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.2f),
                        focusedLabelColor = accentColor,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = if (isDark) Color.White else Color.Black,
                        unfocusedTextColor = if (isDark) Color.White else Color.Black,
                        focusedContainerColor = if (isDark) Color(0x08FFFFFF) else Color(0x05000000),
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (name != originalName) {
                            IconButton(onClick = { viewModel.customName.value = originalName }) {
                                Icon(imageVector = Icons.Default.Undo, contentDescription = "Reset Name", tint = accentColor)
                            }
                        }
                    }
                )
            }
        }

        // Source Picker Card
        GlassCard(isDark = isDark, cornerRadius = 24.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Icon Source",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { galleryPickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gallery", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    if (galleryBitmap != null) {
                        OutlinedButton(
                            onClick = { viewModel.clearGalleryImage() },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.Red),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = Color.Red)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Card: Preset Shapes & Corner Masking
        GlassCard(isDark = isDark, cornerRadius = 24.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Shape & Layout Mask",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("none" to "Standard", "circle" to "Circle", "rounded" to "Rounded").forEach { (type, title) ->
                        val isSelected = type == currentShape
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) accentColor else if (isDark) Color(0x11FFFFFF) else Color(0xCCFFFFFF))
                                .clickable { viewModel.shapeType.value = type }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else if (isDark) Color.White else Color.Black
                            )
                        }
                    }
                }

                if (currentShape == "rounded") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Corner Radius", fontSize = 13.sp, color = if (isDark) Color.White else Color.Black)
                        Text("${radiusPercent.toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accentColor)
                    }
                    Slider(
                        value = radiusPercent,
                        onValueChange = { viewModel.cornerRadiusPercent.value = it },
                        valueRange = 5f..50f,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )
                }
            }
        }

        // Card: Custom Background Color & Gradients
        GlassCard(isDark = isDark, cornerRadius = 24.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Background Fill",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Solid background options
                Text("Solid Color presets:", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0x11FFFFFF) else Color(0x22000000))
                                .border(1.dp, Color.Gray, CircleShape)
                                .clickable { viewModel.bgSolidColor.value = null; viewModel.bgGradientColors.value = null },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Block, contentDescription = "None", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }

                    items(BgSolidPresetColors) { colorValue ->
                        val isSelected = solidColor == colorValue
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(colorValue))
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isDark) Color.White else Color.Black,
                                    shape = CircleShape
                                )
                                .clickable {
                                    viewModel.bgSolidColor.value = colorValue
                                    viewModel.bgGradientColors.value = null
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Gradients background options
                Text("Gradient blends:", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(BgGradientPresets) { gradientList ->
                        val isSelected = gradientColors == gradientList
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(gradientList.map { Color(it) })
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isDark) Color.White else Color.Black,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    viewModel.bgGradientColors.value = gradientList
                                    viewModel.bgSolidColor.value = null
                                }
                        )
                    }
                }
            }
        }

        // Card: Outline Borders
        GlassCard(isDark = isDark, cornerRadius = 24.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Borders & Outlines",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Border Thickness", fontSize = 13.sp, color = if (isDark) Color.White else Color.Black)
                    Text("${borderWidthPercent.toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accentColor)
                }
                Slider(
                    value = borderWidthPercent,
                    onValueChange = { viewModel.borderWidthPercent.value = it },
                    valueRange = 0f..20f,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )

                if (borderWidthPercent > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Border Color", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(BorderPresetColors) { colValue ->
                            val isSelected = borderColorValue == colValue
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(colValue))
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isDark) Color.White else Color.Black,
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.borderColorValue.value = colValue }
                            )
                        }
                    }
                }
            }
        }

        // Card: Canvas Manipulations (Zoom/Rotation)
        GlassCard(isDark = isDark, cornerRadius = 24.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Canvas Geometry",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Rotation Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Rotate Image", fontSize = 13.sp, color = if (isDark) Color.White else Color.Black)
                    Text("${angle.toInt()}°", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accentColor)
                }
                Slider(
                    value = angle,
                    onValueChange = { viewModel.rotation.value = it },
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Zoom Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Zoom Scale", fontSize = 13.sp, color = if (isDark) Color.White else Color.Black)
                    Text("${"%.1f".format(zoomFactor)}x", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accentColor)
                }
                Slider(
                    value = zoomFactor,
                    onValueChange = { viewModel.zoom.value = it },
                    valueRange = 1.0f..3.0f,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )
            }
        }

        // Card: Advanced Image Color Adjustment Filters
        GlassCard(isDark = isDark, cornerRadius = 24.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Color FX Filters",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Brightness
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Brightness", fontSize = 13.sp, color = if (isDark) Color.White else Color.Black)
                    Text("${brightVal.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accentColor)
                }
                Slider(
                    value = brightVal,
                    onValueChange = { viewModel.brightness.value = it },
                    valueRange = -100f..100f,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )

                // Contrast
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Contrast", fontSize = 13.sp, color = if (isDark) Color.White else Color.Black)
                    Text("${"%.1f".format(contrastVal)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accentColor)
                }
                Slider(
                    value = contrastVal,
                    onValueChange = { viewModel.contrast.value = it },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )

                // Saturation
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Saturation", fontSize = 13.sp, color = if (isDark) Color.White else Color.Black)
                    Text("${"%.1f".format(satVal)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accentColor)
                }
                Slider(
                    value = satVal,
                    onValueChange = { viewModel.saturation.value = it },
                    valueRange = 0.0f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )

                // Opacity
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Opacity", fontSize = 13.sp, color = if (isDark) Color.White else Color.Black)
                    Text("${(opacVal * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accentColor)
                }
                Slider(
                    value = opacVal,
                    onValueChange = { viewModel.opacity.value = it },
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )

                // Hue Rotation
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Hue Rotation", fontSize = 13.sp, color = if (isDark) Color.White else Color.Black)
                    Text("${hueVal.toInt()}°", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accentColor)
                }
                Slider(
                    value = hueVal,
                    onValueChange = { viewModel.hue.value = it },
                    valueRange = -180f..180f,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )
            }
        }

        // Apply / Cancel Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.resetEditorStates() },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.5f)),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text(
                    text = "Cancel",
                    color = if (isDark) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    if (originalDrawable != null) {
                        viewModel.applyShortcut(context, originalDrawable)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("apply_customization")
            ) {
                Icon(imageVector = Icons.Default.Launch, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Apply Icon",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
