package com.example.coffeu.ui.auth

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.coffeu.R // Necesitas tener R.drawable.xxx
// Nota: La importación com.example.coffeu.ui.auth.Kitchen no es necesaria si Kitchen está en este archivo
import com.example.coffeu.ui.theme.CoffeUTheme
import com.example.coffeu.ui.viewmodel.AuthViewModel
import com.example.coffeu.data.model.Kitchen

// =================================================================
// ESTRUCTURA DE DATOS (MODELOS)
// =================================================================
data class FoodCategory(val id: Int, val name: String, val icon: Int)
data class Kitchen(
    val id: Int,
    val name: String,
    val discount: String,
    val rating: Double,
    val reviewCount: Int,
    val price: String,
    val deliveryTime: String,
    val distance: String,
    val image: Int
)

// --- Simulación de Datos (PLACEHOLDERS) ---
val categories = listOf(
    FoodCategory(1, "Pizza", R.drawable.img),
    FoodCategory(2, "Burgers", R.drawable.img),
    FoodCategory(3, "Cookies", R.drawable.img),
    FoodCategory(4, "Pasta", R.drawable.img),
    FoodCategory(5, "Sushi", R.drawable.img),
)


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

    Scaffold(
        bottomBar = {
            HomeBottomBar(onProfileClicked = onNavigateToProfile)
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
            item {
                FoodCategoriesRow(categories = categories)
            }

            // 5. Kitchens Near You Header
            item {
                KitchensHeader()
            }

            // 6. Kitchens Near You Row (Mostrar datos dinámicos)
            item {
                when {
                    // Muestra carga si está ocupado y la lista está vacía (primera carga)
                    isLoadingList && kitchens.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
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
                        ) {
                            items(kitchens) { kitchen ->
                                // Pasa el callback de navegación al hacer clic
                                KitchenCard(
                                    kitchen = kitchen
                                )
                            }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Foto de Perfil y Ubicación
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Placeholder para la Foto de Perfil
            Image(
                painter = painterResource(id = R.drawable.home_perfil), // Reemplaza con tu drawable
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
                    // Mostramos el nombre de usuario en la primera línea
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
                        // Y la ubicación en la segunda
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
        // Icono de Filtro (los 3 puntos)
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
        // Imagen de Fondo del Banner
        Image(
            painter = painterResource(id = R.drawable.home_placeholderpizza_home), // REEMPLAZAR con tu drawable
            contentDescription = "Offer Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // Contenido del Banner
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
            // Simula que "Pizza" está seleccionado
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
        // Icono de Comida
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(contentColor.copy(alpha = 0.2f))
        ) {
            // Image(painter = painterResource(id = category.icon), contentDescription = category.name)
        }
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
fun KitchensHeader(onSeeAllClicked: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Cocina cerca de ti",
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
// Archivo: HomeScreen.kt (Función KitchenCard)

@Composable
fun KitchenCard(kitchen: Kitchen) { // ✅ Ya no acepta onCardClick
    Card(
        modifier = Modifier
            .width(260.dp)
            // ✅ CORRECCIÓN: La tarjeta sigue siendo clickable, pero la acción no hace nada.
            .clickable { /* Acción deshabilitada temporalmente */ },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Imagen, Descuento y Favorito
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                // ✅ REEMPLAZO DEL PLACEHOLDER CON COIL (AsyncImage)
                AsyncImage(
                    // La URL viene de Django
                    model = kitchen.imageUrl,
                    contentDescription = kitchen.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                    // Puedes añadir un placeholder o indicador de carga aquí
                   // placeholder = painterResource(R.drawable.img) // Usa un drawable local como placeholder
                )

                // Descuento
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
                // Corazón de Favorito
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(4.dp)
                        .clickable { /* Acción de Favorito */ }
                )
            }

            // Detalles
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
                // Tiempo, Distancia, Rating y Precio (Mantener el resto de la lógica)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107), // Keep yellow for rating
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${kitchen.rating} (${kitchen.reviewCount} Reviews)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Precio
                    Text(
                        text = "$${kitchen.price}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Tiempo de entrega y Distancia
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
fun HomeBottomBar(onProfileClicked: () -> Unit) {
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
        // 'Home' está seleccionado en la imagen
        val selectedItem = navItems.first().first

        navItems.forEach { (label, icon) ->
            val isSelected = label == selectedItem
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (label == "Profile") {
                        onProfileClicked()
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
        // ✅ CORRECCIÓN: Ahora pasamos el valor 'username' (ej: "Stefanie")
        HomeScreen(
            username = "Stefanie", // <--- ¡Añadir este parámetro!
            onLogout = {},
            onNavigateToProfile = {}
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
            onNavigateToProfile = {}
        )
    }
}
