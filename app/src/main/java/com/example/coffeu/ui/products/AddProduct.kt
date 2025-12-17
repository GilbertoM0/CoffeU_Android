package com.example.coffeu.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeu.ui.theme.CoffeUTheme
import com.example.coffeu.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onProductAdded: () -> Unit,
    onBackClicked: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var reviewCount by remember { mutableStateOf("") }
    var deliveryTime by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("") }

    val isLoading = authViewModel.isLoading
    val errorMessage = authViewModel.errorMessage
    val addProductSuccess = authViewModel.addProductSuccess

    val isFormValid by remember(name, description, stock, imageUrl, price, rating, reviewCount, deliveryTime, distance, discount) {
        derivedStateOf {
            name.isNotBlank() &&
            description.isNotBlank() &&
            stock.isNotBlank() &&
            imageUrl.isNotBlank() &&
            price.isNotBlank() &&
            rating.isNotBlank() &&
            reviewCount.isNotBlank() &&
            deliveryTime.isNotBlank() &&
            distance.isNotBlank() &&
            discount.isNotBlank()
        }
    }

    LaunchedEffect(addProductSuccess) {
        if (addProductSuccess) {
            authViewModel.resetAddProductState()
            onProductAdded()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Product") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item { TextField(label = "Product Name", value = name, onValueChange = { name = it }) }
            item { TextField(label = "Description", value = description, onValueChange = { description = it }) }
            item { TextField(label = "Stock", value = stock, onValueChange = { stock = it }, keyboardType = KeyboardType.Number) }
            item { TextField(label = "Image URL", value = imageUrl, onValueChange = { imageUrl = it }) }
            item { TextField(label = "Price", value = price, onValueChange = { price = it }, keyboardType = KeyboardType.Decimal) }
            item { TextField(label = "Rating", value = rating, onValueChange = { rating = it }, keyboardType = KeyboardType.Decimal) }
            item { TextField(label = "Review Count", value = reviewCount, onValueChange = { reviewCount = it }, keyboardType = KeyboardType.Number) }
            item { TextField(label = "Delivery Time", value = deliveryTime, onValueChange = { deliveryTime = it }) }
            item { TextField(label = "Distance", value = distance, onValueChange = { distance = it }) }
            item { TextField(label = "Discount", value = discount, onValueChange = { discount = it }) }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            if (errorMessage != null && !isLoading) {
                item {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        authViewModel.attemptAddProduct(
                            name = name,
                            description = description,
                            stock = stock,
                            imageUrl = imageUrl,
                            price = price,
                            rating = rating,
                            reviewCount = reviewCount,
                            deliveryTime = deliveryTime,
                            distance = distance,
                            discount = discount
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Add Product", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun TextField(label: String, value: String, onValueChange: (String) -> Unit, keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AddProductScreenPreview() {
    CoffeUTheme {
        AddProductScreen(onProductAdded = {}, onBackClicked = {})
    }
}
