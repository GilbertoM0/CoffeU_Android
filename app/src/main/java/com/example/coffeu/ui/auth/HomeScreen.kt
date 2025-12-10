package com.example.coffeu.ui.auth

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.coffeu.R // Necesitas tener R.drawable.xxx
import com.example.coffeu.data.model.Kitchen
import com.example.coffeu.ui.theme.CoffeUTheme
import com.example.coffeu.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

// =================================================================
// ESTRUCTURA DE DATOS (MODELOS)
// =================================================================
data class FoodCategory(val id: Int, val name: String, val icon: Int)

// --- Simulación de Datos (PLACEHOLDERS) ---
/*val categories = listOf(
    FoodCategory(1, "Pizza", R.drawable.img),
    FoodCategory(2, "Burgers", R.drawable.img),
    FoodCategory(3, "Cookies", R.drawable.img),
    FoodCategory(4, "Pasta", R.drawable.img),
    FoodCategory(5, "Sushi", R.drawable.img),
)*/


// =================================================================
// COMPONENTE PRINCIPAL DE LA PANTALLA
// =================================================================
@Composable
fun HomeScreen(
    username: String,
    onLogout: () -> Unit = {},
    onSearchClicked: () -> Unit = {},
    onNotificationClicked: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToFavorites: () -> Unit, // Callback para navegar a favoritos
    onNavigateToMyOrder: () -> Unit,
    onNavigateToProductDetail: (Int) -> Unit, // Callback para navegar
    onNavigateToAllProducts: () -> Unit, // Callback para ver todos
    authViewModel: AuthViewModel = viewModel() // <--- Obtener ViewModel
) {

    // 1. Cargar datos al inicio
    LaunchedEffect(Unit) {
        authViewModel.loadKitchens()
    }

    // 2. Observar el estado del ViewModel
    val kitchens = authViewModel.kitchenList // Lista real (observable)
    val isLoadingList = authViewModel.isLoading
    val error = authViewModel.kitchenListError

    // ✅ Seleccionar 20 cocinas aleatorias y recordarlas en dos listas
    val shuffledKitchens = remember(kitchens) {
        kitchens.shuffled()
    }
    val randomKitchens = remember(shuffledKitchens) {
        shuffledKitchens.take(10)
    }
    val secondRandomKitchens = remember(shuffledKitchens) {
        shuffledKitchens.drop(10).take(10)
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            HomeBottomBar(
                onProfileClicked = onNavigateToProfile,
                onFavoritesClicked = onNavigateToFavorites,
                onMyOrderClicked = onNavigateToMyOrder
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Header (Perfil y Ubicación)
            item {
                HomeHeader(
                    userName = username,
                    onNotificationClicked = onNotificationClicked,
                    onProfileImageClicked = onNavigateToProfile
                )
            }

            // 2. Search Bar
            item {
                SearchBar(onClick = onSearchClicked)
            }

            // 3. Promo Banner (30% OFF)
            item {
                PromoBanner()
            }

            // 4. Categories Row
           /* item {
                FoodCategoriesRow(categories = categories)
            }*/

            // 5. Kitchens Near You Header
            item {
                KitchensHeader(title = "Cocina cerca de ti", onSeeAllClicked = onNavigateToAllProducts)
            }

            // 6. Kitchens Near You Row (Mostrar datos dinámicos)
            item {
                when {
                    // Muestra carga si está ocupado y la lista está vacía (primera carga)
                    isLoadingList && kitchens.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    // Muestra error si falló la carga
                    error != null -> {
                        Text(
                            text = "Error de carga: $error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    // Muestra la lista si hay datos
                    else -> {
                        LazyRow(
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {                            items(randomKitchens) { kitchen -> // ✅ Usar la lista aleatoria
                                KitchenCard(
                                    kitchen = kitchen,
                                    authViewModel = authViewModel,
                                    onCardClick = { onNavigateToProductDetail(kitchen.id) },
                                    onToggleFavorite = {
                                        authViewModel.toggleFavorite(kitchen)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("${kitchen.name} ha sido añadido a favoritos")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 7. Second Carousel
            if (secondRandomKitchens.isNotEmpty()) {
                item {
                    KitchensHeader(title = "Más para descubrir", onSeeAllClicked = onNavigateToAllProducts)
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(secondRandomKitchens) { kitchen ->
                            KitchenCard(
                                kitchen = kitchen,
                                authViewModel = authViewModel,
                                onCardClick = { onNavigateToProductDetail(kitchen.id) },
                                onToggleFavorite = {
                                    authViewModel.toggleFavorite(kitchen)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("${kitchen.name} ha sido añadido a favoritos")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// =================================================================
// 1. HOME HEADER (Perfil y Ubicación)
// =================================================================
@Composable
fun HomeHeader(
    userName: String,
    deliveryLocation: String = "Moctezuma #100",
    notificationCount: Int = 13,
    onNotificationClicked: () -> Unit,
    onProfileImageClicked: () -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)
    }
    val imageUri = sharedPreferences.getString("image_uri", null)?.let { Uri.parse(it) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Foto de Perfil y Ubicación
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = imageUri ?: R.drawable.home_perfil,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onProfileImageClicked)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Hola, ${userName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = deliveryLocation,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Icono de Notificación
        BadgedBox(
            badge = {
                Badge(
                    containerColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.offset((-4).dp, 4.dp)
                ) {
                    Text(text = notificationCount.toString(), fontSize = 10.sp, color = MaterialTheme.colorScheme.onError)
                }
            }
        ) {
            IconButton(onClick = onNotificationClicked) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// =================================================================
// 2. SEARCH BAR
// =================================================================
@Composable
fun SearchBar(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Buscar comida..",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Filter",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}

// =================================================================
// 3. PROMO BANNER
// =================================================================
@Composable
fun PromoBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.home_placeholderpizza_home),
            contentDescription = "Offer Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "HASTA 30% DE DESCUENTO",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "EN EL PRIMER PEDIDO",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Acción de pedido */ },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
            ) {
                Text(
                    text = "Ordenar Ahora",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// =================================================================
// 4. CATEGORIES ROW
// =================================================================
@Composable
fun FoodCategoriesRow(categories: List<FoodCategory>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            CategoryItem(category = category, isSelected = category.name == "Pizza")
        }
    }
}

@Composable
fun CategoryItem(category: FoodCategory, isSelected: Boolean) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { /* Acción de selección */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(contentColor.copy(alpha = 0.2f))
        ) {}
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = category.name,
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

// =================================================================
// 5. KITCHENS HEADER
// =================================================================
@Composable
fun KitchensHeader(title: String, onSeeAllClicked: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(onClick = onSeeAllClicked) {
            Text(
                text = "Mostrar todo",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// =================================================================
// 6. KITCHEN CARD
// =================================================================
@Composable
fun KitchenCard(
    kitchen: Kitchen,
    authViewModel: AuthViewModel,
    onCardClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val isFavorite = authViewModel.isFavorite(kitchen)

    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                AsyncImage(
                    model = kitchen.imageUrl,
                    contentDescription = kitchen.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
                Text(
                    text = kitchen.discount,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.error)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(4.dp)
                        .clickable(onClick = onToggleFavorite)
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = kitchen.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${kitchen.rating} (${kitchen.reviewCount} Reviews)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "$${kitchen.price}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${kitchen.deliveryTime}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${kitchen.distance}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// =================================================================
// 7. BOTTOM NAVIGATION BAR
// =================================================================
@Composable
fun HomeBottomBar(onProfileClicked: () -> Unit, onFavoritesClicked: () -> Unit, onMyOrderClicked: () -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            Pair("Home", Icons.Filled.Home),
            Pair("My Order", Icons.Filled.ShoppingCart),
            Pair("Favorites", Icons.Filled.Favorite),
            Pair("Profile", Icons.Filled.Person)
        )
        val selectedItem = navItems.first().first

        navItems.forEach { (label, icon) ->
            val isSelected = label == selectedItem
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    when (label) {
                        "Profile" -> onProfileClicked()
                        "Favorites" -> onFavoritesClicked()
                        "My Order" -> onMyOrderClicked()
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}


// =================================================================
// PREVIEW
// =================================================================
@Preview(showBackground = true, name = "Light Mode")
@Composable
fun HomeScreenPreview() {
    CoffeUTheme {
        HomeScreen(
            username = "Stefanie",
            onLogout = {},
            onNavigateToProfile = {},
            onNavigateToProductDetail = {},
            onNavigateToAllProducts = {},
            onNavigateToFavorites = {},
            onNavigateToMyOrder = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun HomeScreenDarkPreview() {
    CoffeUTheme(darkTheme = true) {
        HomeScreen(
            username = "Stefanie",
            onLogout = {},
            onNavigateToProfile = {},
            onNavigateToProductDetail = {},
            onNavigateToAllProducts = {},
            onNavigateToFavorites = {},
            onNavigateToMyOrder = {}
        )
    }
}
