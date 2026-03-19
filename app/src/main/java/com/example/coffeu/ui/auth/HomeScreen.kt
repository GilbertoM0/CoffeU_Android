package com.example.coffeu.ui.auth

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.coffeu.R
import com.example.coffeu.data.model.Kitchen
import com.example.coffeu.ui.theme.CoffeUTheme
import com.example.coffeu.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    username: String,
    onLogout: () -> Unit = {},
    onSearchClicked: () -> Unit = {},
    onNotificationClicked: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToFavorites: () -> Unit,
    onNavigateToMyOrder: () -> Unit,
    onNavigateToProductDetail: (Int) -> Unit,
    onNavigateToAllProducts: () -> Unit,
    onNavigateToAddProduct: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {

    LaunchedEffect(Unit) {
        authViewModel.loadKitchens()
    }

    val kitchens = authViewModel.kitchenList
    val isLoadingList = authViewModel.isLoading
    val error = authViewModel.kitchenListError
    val unreadCount = authViewModel.unreadNotificationsCount

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
    val context = LocalContext.current

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
            item {
                HomeHeader(
                    userName = username,
                    notificationCount = unreadCount,
                    onNotificationClicked = onNotificationClicked,
                    onProfileImageClicked = onNavigateToProfile
                )
            }

            item {
                SearchBar(onClick = onSearchClicked)
            }

            item {
                PromoBanner()
            }

            item {
                KitchensHeader(title = stringResource(id = R.string.home_kitchens_near_you), onSeeAllClicked = onNavigateToAllProducts)
            }

            item {
                when {
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
                    error != null -> {
                        Text(
                            text = stringResource(id = R.string.home_load_error, error),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    else -> {
                        LazyRow(
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(randomKitchens) { kitchen ->
                                KitchenCard(
                                    kitchen = kitchen,
                                    authViewModel = authViewModel,
                                    onCardClick = { onNavigateToProductDetail(kitchen.id) },
                                    onToggleFavorite = {
                                        authViewModel.toggleFavorite(kitchen)
                                        scope.launch {
                                            val message = context.getString(R.string.home_added_to_favorites, kitchen.name ?: "Producto")
                                            snackbarHostState.showSnackbar(message)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (secondRandomKitchens.isNotEmpty()) {
                item {
                    KitchensHeader(title = stringResource(id = R.string.home_more_to_discover), onSeeAllClicked = onNavigateToAllProducts)
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
                                        val message = context.getString(R.string.home_added_to_favorites, kitchen.name ?: "Producto")
                                        snackbarHostState.showSnackbar(message)
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
                    text = stringResource(id = R.string.home_greeting, userName),
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

        BadgedBox(
            badge = {
                if (notificationCount > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.offset((-4).dp, 4.dp)
                    ) {
                        Text(text = notificationCount.toString(), fontSize = 10.sp, color = MaterialTheme.colorScheme.onError)
                    }
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
            text = stringResource(id = R.string.home_search_placeholder),
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
                text = stringResource(id = R.string.promo_discount_title),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = stringResource(id = R.string.promo_discount_subtitle),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
            ) {
                Text(
                    text = stringResource(id = R.string.promo_order_now),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

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
                text = stringResource(id = R.string.home_see_all),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

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
                    text = kitchen.discount ?: "",
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
                    text = kitchen.name ?: "",
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
                            text = "${kitchen.rating ?: 0.0} - ${kitchen.category ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "$${kitchen.price ?: "0.00"}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${kitchen.deliveryTime ?: ""} • ${kitchen.size ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${kitchen.distance ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun HomeBottomBar(onProfileClicked: () -> Unit, onFavoritesClicked: () -> Unit, onMyOrderClicked: () -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            Triple(stringResource(id = R.string.nav_home), Icons.Filled.Home, "Home"),
            Triple(stringResource(id = R.string.nav_my_order), Icons.Filled.ShoppingCart, "My Order"),
            Triple(stringResource(id = R.string.nav_favorites), Icons.Filled.Favorite, "Favorites"),
            Triple(stringResource(id = R.string.nav_profile), Icons.Filled.Person, "Profile")
        )
        val selectedItem = "Home"

        navItems.forEach { (label, icon, key) ->
            val isSelected = key == selectedItem
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    when (key) {
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
            onNavigateToMyOrder = {},
            onNavigateToAddProduct = {}
        )
    }
}
