package com.example.coffeu.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.coffeu.data.model.Kitchen
import com.example.coffeu.ui.theme.CoffeUTheme
import com.example.coffeu.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    kitchen: Kitchen,
    onBackClicked: () -> Unit, // ✅ Acción para volver atrás
    authViewModel: AuthViewModel = viewModel()
) {
    val isFavorite = authViewModel.isFavorite(kitchen)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                item {
                    AsyncImage(
                        model = kitchen.imageUrl,
                        contentDescription = kitchen.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                item {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = kitchen.name,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "$${kitchen.price}",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InfoChip("Envio Gratis")
                            InfoChip(kitchen.deliveryTime)
                            InfoChip(
                                text = kitchen.rating.toString(),
                                icon = { Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Descripcion",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = kitchen.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Top Bar overlay
            TopAppBar(
                title = { Text("Menu Detail", color = Color.White) },
                navigationIcon = {
                    Surface(
                        shape = CircleShape,
                        shadowElevation = 4.dp,
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        IconButton(onClick = onBackClicked) { // ✅ Conectado
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                },
                actions = {
                    Surface(
                        shape = CircleShape,
                        shadowElevation = 4.dp,
                        modifier = Modifier.padding(end = 8.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        IconButton(onClick = {
                            authViewModel.toggleFavorite(kitchen)
                            scope.launch {
                                val message = if (authViewModel.isFavorite(kitchen)) {
                                    "${kitchen.name} ha sido añadido a favoritos"
                                } else {
                                    "${kitchen.name} ha sido eliminado de favoritos"
                                }
                                snackbarHostState.showSnackbar(message)
                            }
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            // Bottom action bar
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                var quantity by rememberSaveable { mutableIntStateOf(1) }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { if (quantity > 1) quantity-- },
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("-", fontSize = 20.sp)
                    }
                    Text(text = quantity.toString(), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
                    OutlinedButton(
                        onClick = { quantity++ },
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("+", fontSize = 20.sp)
                    }
                }
                Button(
                    onClick = { /* TODO: Add to cart */ },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Agregar al carrito")
                }
            }
        }
    }
}

@Composable
fun InfoChip(text: String, icon: @Composable (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon?.invoke()
        Text(text = text, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold)
    }
}


@Preview(showBackground = true, name = "Light Mode")
@Composable
fun ProductDetailScreenPreview() {
    CoffeUTheme {
        val sampleKitchen = Kitchen(
            id = 1,
            name = "Classic Cheese Pizza with extra cheese and pepperoni",
            description = "Burger With Meat is a typical food from our restaurant that is much in demand by many people, this is very recommended for you",
            stock = 10,
            imageUrl = "",
            price = "20.00",
            rating = 4.5,
            reviewCount = 120,
            deliveryTime = "20-30 min",
            distance = "1.2km",
            discount = "10%"
        )
        ProductDetailScreen(kitchen = sampleKitchen, onBackClicked = {}) // ✅ Preview actualizado
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun ProductDetailScreenDarkPreview() {
    CoffeUTheme(darkTheme = true) {
        val sampleKitchen = Kitchen(
            id = 1,
            name = "Classic Cheese Pizza with extra cheese and pepperoni",
            description = "Burger With Meat is a typical food from our restaurant that is much in demand by many people, this is very recommended for you",
            stock = 10,
            imageUrl = "",
            price = "20.00",
            rating = 4.5,
            reviewCount = 120,
            deliveryTime = "20-30 min",
            distance = "1.2km",
            discount = "10%"
        )
        ProductDetailScreen(kitchen = sampleKitchen, onBackClicked = {}) // ✅ Preview actualizado
    }
}
