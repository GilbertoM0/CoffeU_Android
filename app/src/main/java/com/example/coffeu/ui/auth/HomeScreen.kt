package com.example.coffeu.ui.auth // Ajustado al paquete donde dijiste que va HomeScreen.kt

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
import com.example.coffeu.R // Necesitas tener R.drawable.xxx
import com.example.coffeu.ui.auth.Kitchen
import com.example.coffeu.ui.theme.CoffeUTheme // Asumiendo que este es el nombre de tu tema

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
// Usas ic_launcher_background como placeholder, lo mantengo.
val categories = listOf(
    /*FoodCategory(1, "Pizza", R.drawable.home_pizza),
    FoodCategory(2, "Burgers", R.drawable.home_hamburger),
    FoodCategory(3, "Cookies", R.drawable.home_cookies),*/
    FoodCategory(1, "Pasta", R.drawable.img),
    /*FoodCategory(5, "Sushi", R.drawable.home_sushiroll),*/
)

val kitchens = listOf(
    Kitchen(1, "Classic Cheese Pizza", "10% Off", 4.3, 27, "20.00", "15-30 min", "1.3 km", R.drawable.home_pizza_con_chorizo_jamon_y_queso),
    Kitchen(2, "Pasta", "5% Off", 4.3, 27, "18.50", "15-30 min", "1.3 km", R.drawable.home_plato_pasta),
    Kitchen(3, "Sushi Roll", "20% Off", 4.5, 40, "22.00", "20-40 min", "2.0 km", R.drawable.home_sushi_roll),

    Kitchen(4, "Cafe con galletas", "10% Off", 4.3, 27, "20.00", "15-30 min", "1.3 km", R.drawable.coffee),
    Kitchen(5, "Flauta", "5% Off", 4.3, 27, "18.50", "15-30 min", "1.3 km", R.drawable.home_flautas),
    Kitchen(6, "Torta", "20% Off", 4.5, 40, "22.00", "20-40 min", "2.0 km", R.drawable.home_torta)
)


// =================================================================
// COMPONENTE PRINCIPAL DE LA PANTALLA
// =================================================================
@Composable
fun HomeScreen(
    // **CORRECCIÓN 1:** Se añade el parámetro onLogout para AppNavigation
    onLogout: () -> Unit = {},
    onSearchClicked: () -> Unit = {},
    onNotificationClicked: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            // Podrías pasar el onLogout al HomeBottomBar si el botón de perfil es el de cerrar sesión.
            HomeBottomBar()
        },
        containerColor = Color.White
    ) { paddingValues ->
        // Usamos LazyColumn para que toda la pantalla sea deslizable
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Header (Perfil y Ubicación)
            item {
                // **CORRECCIÓN 2:** Si quieres que el botón de notificación te lleve al Login (por ejemplo, para cerrar sesión temporalmente),
                // podrías usar onLogout aquí, pero lo mantengo separado para mantener la lógica clara.
                HomeHeader(onNotificationClicked = onNotificationClicked)
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

            // 6. Kitchens Near You Row (Horizontal)
            item {
                LazyRow(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(kitchens) { kitchen ->
                        KitchenCard(kitchen = kitchen)
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
    userName: String = "User Name",
    deliveryLocation: String = "44 Street Town",
    notificationCount: Int = 13,
    // Este parámetro es requerido en este sub-Composable
    onNotificationClicked: () -> Unit
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
                    .clickable { /* Acción de perfil */ }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Deliver To",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
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
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Icono de Notificación
        BadgedBox(
            badge = {
                Badge(
                    containerColor = Color(0xFFE53935), // Rojo de notificación
                    modifier = Modifier.offset((-4).dp, 4.dp)
                ) {
                    Text(text = notificationCount.toString(), fontSize = 10.sp, color = Color.White)
                }
            }
        ) {
            IconButton(onClick = onNotificationClicked) {
                Icon(
                    imageVector = Icons.Default.Notifications, // Usando un icono estándar de notificación
                    contentDescription = "Notifications",
                    modifier = Modifier.size(24.dp)
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
            .background(Color(0xFFF5F5F5)) // Gris muy claro
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Search for Food..",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f))
        // Icono de Filtro (los 3 puntos)
        Icon(
            imageVector = Icons.Default.Favorite, // Usando Favorite como placeholder para el icono de filtro (Tune podría ser mejor)
            contentDescription = "Filter",
            tint = Color.Gray,
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
            painter = painterResource(id = R.drawable.home_placeholderpizza_home), // REEMPLAZAR con pizza_banner_image
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
                text = "UP TO 30% OFF",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "ON FIRST ORDER",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Acción de pedido */ },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Order Now",
                    color = MaterialTheme.colorScheme.primary, // Color principal de tu tema
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
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF0F0F0)
    val contentColor = if (isSelected) Color.White else Color.Black

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
            text = "Kitchen near you",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onSeeAllClicked) {
            Text(
                text = "See All",
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
fun KitchenCard(kitchen: Kitchen) {
    Card(
        modifier = Modifier.width(260.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Imagen, Descuento y Favorito
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Image(
                    painter = painterResource(id = kitchen.image), // REEMPLAZAR
                    contentDescription = kitchen.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
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
                        .background(Color(0xFFE53935)) // Rojo de descuento
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
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Tiempo, Distancia, Rating y Precio
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
                            tint = Color(0xFFFFC107), // Amarillo de estrella
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${kitchen.rating} (${kitchen.reviewCount} Reviews)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
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
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${kitchen.distance}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
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
fun HomeBottomBar() {
    NavigationBar(
        containerColor = Color.White,
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
                onClick = { /* Acción de Navegación */ },
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
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}


// =================================================================
// PREVIEW
// =================================================================
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CoffeUTheme {
        // **CORRECCIÓN 3:** Se pasa un placeholder para onLogout
        HomeScreen(onLogout = {})
    }
}