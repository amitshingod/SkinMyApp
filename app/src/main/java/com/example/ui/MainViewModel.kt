package com.example.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FavoriteEntity
import com.example.data.HistoryEntity
import com.example.utils.AppInfo
import com.example.utils.IconHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AppFilter {
    ALL, USER, SYSTEM, FAVORITES
}

enum class AppSort {
    ALPHABETICAL, RECENTLY_INSTALLED
}

enum class ThemeMode {
    DARK, LIGHT, SYSTEM
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.appDao()
    private val prefs: SharedPreferences = application.getSharedPreferences("oneshot_iconx_prefs", Context.MODE_PRIVATE)

    // Installed apps
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _isLoadingApps = MutableStateFlow(true)
    val isLoadingApps: StateFlow<Boolean> = _isLoadingApps.asStateFlow()

    // Favorites and History
    val favorites: StateFlow<List<FavoriteEntity>> = dao.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<HistoryEntity>> = dao.getHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI filters
    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow(AppFilter.ALL)
    val selectedSort = MutableStateFlow(AppSort.ALPHABETICAL)

    // Settings
    private val _themeMode = MutableStateFlow(getSavedThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _gridSize = MutableStateFlow(prefs.getInt("grid_size", 4))
    val gridSize: StateFlow<Int> = _gridSize.asStateFlow()

    private val _accentColorIndex = MutableStateFlow(prefs.getInt("accent_color_index", 0))
    val accentColorIndex: StateFlow<Int> = _accentColorIndex.asStateFlow()

    private val _animationSpeed = MutableStateFlow(prefs.getFloat("animation_speed", 1.0f))
    val animationSpeed: StateFlow<Float> = _animationSpeed.asStateFlow()

    // Combined filtered apps flow
    val filteredApps: StateFlow<List<AppInfo>> = combine(
        _installedApps,
        favorites,
        searchQuery,
        selectedFilter,
        selectedSort
    ) { apps, favs, query, filter, sort ->
        var result = apps

        // Search Filter
        if (query.isNotEmpty()) {
            result = result.filter {
                it.label.contains(query, ignoreCase = true) ||
                it.packageName.contains(query, ignoreCase = true)
            }
        }

        // Category Filter
        result = when (filter) {
            AppFilter.ALL -> result
            AppFilter.USER -> result.filter { !it.isSystemApp }
            AppFilter.SYSTEM -> result.filter { it.isSystemApp }
            AppFilter.FAVORITES -> {
                val favPackages = favs.map { it.packageName }.toSet()
                result.filter { favPackages.contains(it.packageName) }
            }
        }

        // Sort
        result = when (sort) {
            AppSort.ALPHABETICAL -> result.sortedBy { it.label.lowercase() }
            AppSort.RECENTLY_INSTALLED -> result.sortedByDescending { it.firstInstallTime }
        }

        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Customization Screen states
    private val _customizingApp = MutableStateFlow<AppInfo?>(null)
    val customizingApp: StateFlow<AppInfo?> = _customizingApp.asStateFlow()

    val customName = MutableStateFlow("")
    val rotation = MutableStateFlow(0f)
    val zoom = MutableStateFlow(1f)
    val brightness = MutableStateFlow(0f)
    val contrast = MutableStateFlow(1f)
    val saturation = MutableStateFlow(1f)
    val opacity = MutableStateFlow(1f)
    val hue = MutableStateFlow(0f)

    // Shapes: "none", "circle", "rounded"
    val shapeType = MutableStateFlow("rounded")
    val cornerRadiusPercent = MutableStateFlow(25f)

    // Border
    val borderWidthPercent = MutableStateFlow(0f)
    val borderColorValue = MutableStateFlow(0xFFFFFFFF.toInt())

    // Custom Backgrounds
    val bgSolidColor = MutableStateFlow<Int?>(null)
    val bgGradientColors = MutableStateFlow<List<Int>?>(null)

    // External image
    private val _galleryImageUri = MutableStateFlow<Uri?>(null)
    val galleryImageUri: StateFlow<Uri?> = _galleryImageUri.asStateFlow()

    private val _galleryBitmap = MutableStateFlow<Bitmap?>(null)
    val galleryBitmap: StateFlow<Bitmap?> = _galleryBitmap.asStateFlow()

    // Success / Error feedback states
    private val _shortcutStatus = MutableSharedFlow<String>()
    val shortcutStatus: SharedFlow<String> = _shortcutStatus.asSharedFlow()

    init {
        loadInstalledApps()
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoadingApps.value = true
            val apps = withContext(Dispatchers.IO) {
                IconHelper.getInstalledApps(getApplication())
            }
            _installedApps.value = apps
            _isLoadingApps.value = false
        }
    }

    // Favorites
    fun toggleFavorite(packageName: String) {
        viewModelScope.launch {
            val isFav = favorites.value.any { it.packageName == packageName }
            if (isFav) {
                dao.deleteFavoriteByPackage(packageName)
            } else {
                dao.insertFavorite(FavoriteEntity(packageName))
            }
        }
    }

    // Customization lifecycle
    fun startCustomization(app: AppInfo) {
        _customizingApp.value = app
        customName.value = app.label
        resetEditorStates()
    }

    fun resetEditorStates() {
        rotation.value = 0f
        zoom.value = 1f
        brightness.value = 0f
        contrast.value = 1f
        saturation.value = 1f
        opacity.value = 1f
        hue.value = 0f
        shapeType.value = "rounded"
        cornerRadiusPercent.value = 25f
        borderWidthPercent.value = 0f
        borderColorValue.value = 0xFFFFFFFF.toInt()
        bgSolidColor.value = null
        bgGradientColors.value = null
        _galleryImageUri.value = null
        _galleryBitmap.value = null
    }

    fun loadGalleryImage(uri: Uri) {
        viewModelScope.launch {
            _galleryImageUri.value = uri
            val bitmap = withContext(Dispatchers.IO) {
                IconHelper.loadBitmapFromUri(getApplication(), uri)
            }
            _galleryBitmap.value = bitmap
        }
    }

    fun clearGalleryImage() {
        _galleryImageUri.value = null
        _galleryBitmap.value = null
    }

    fun getAppIconDrawable(packageName: String): Drawable? {
        return IconHelper.getAppIcon(getApplication(), packageName)
    }

    // Compute preview bitmap for current parameters
    suspend fun generateConfiguredBitmap(baseBitmap: Bitmap): Bitmap {
        return withContext(Dispatchers.Default) {
            IconHelper.applyFiltersToBitmap(
                src = baseBitmap,
                brightness = brightness.value,
                contrast = contrast.value,
                saturation = saturation.value,
                opacity = opacity.value,
                hue = hue.value,
                rotation = rotation.value,
                zoom = zoom.value,
                shapeType = shapeType.value,
                cornerRadiusPercent = cornerRadiusPercent.value,
                backgroundColor = bgSolidColor.value,
                gradientColors = bgGradientColors.value,
                borderWidthPercent = borderWidthPercent.value,
                borderColor = if (borderWidthPercent.value > 0) borderColorValue.value else null
            )
        }
    }

    fun applyShortcut(context: Context, originalIconDrawable: Drawable) {
        viewModelScope.launch {
            val app = _customizingApp.value ?: return@launch
            
            // 1. Prepare base bitmap
            val baseBitmap = _galleryBitmap.value ?: IconHelper.drawableToBitmap(originalIconDrawable)
            
            // 2. Apply editor filters
            val finalBitmap = generateConfiguredBitmap(baseBitmap)
            
            // 3. Pin shortcut
            val success = IconHelper.pinShortcut(
                context = context,
                customName = customName.value,
                packageName = app.packageName,
                launcherActivityClassName = app.launcherActivityClassName,
                iconBitmap = finalBitmap
            )

            if (success) {
                // 4. Save to history
                val base64Str = IconHelper.bitmapToBase64(finalBitmap)
                dao.insertHistory(
                    HistoryEntity(
                        packageName = app.packageName,
                        originalLabel = app.label,
                        customName = customName.value,
                        base64Icon = base64Str
                    )
                )
                _shortcutStatus.emit("SUCCESS")
            } else {
                _shortcutStatus.emit("FAILURE")
            }
        }
    }

    // Settings
    private fun getSavedThemeMode(): ThemeMode {
        val name = prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return try {
            ThemeMode.valueOf(name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun setGridSize(columns: Int) {
        _gridSize.value = columns
        prefs.edit().putInt("grid_size", columns).apply()
    }

    fun setAccentColorIndex(index: Int) {
        _accentColorIndex.value = index
        prefs.edit().putInt("accent_color_index", index).apply()
    }

    fun setAnimationSpeed(speed: Float) {
        _animationSpeed.value = speed
        prefs.edit().putFloat("animation_speed", speed).apply()
    }

    fun resetAllSettings() {
        viewModelScope.launch {
            prefs.edit().clear().apply()
            _themeMode.value = ThemeMode.SYSTEM
            _gridSize.value = 4
            _accentColorIndex.value = 0
            _animationSpeed.value = 1.0f
            dao.clearHistory()
            _shortcutStatus.emit("RESET_ALL")
        }
    }

    fun deleteHistoryItem(id: Int) {
        viewModelScope.launch {
            dao.deleteHistoryById(id)
        }
    }
}
