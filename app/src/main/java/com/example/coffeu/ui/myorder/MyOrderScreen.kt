package com.example.coffeu.ui.myorder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeu.data.model.CartItem
import com.example.coffeu.ui.viewmodel.AuthViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrderScreen(authViewModel: AuthViewModel = viewModel()) {
    val cartItems = authViewModel.cartItems
    val total = cartItems.sumOf { (it.kitchen.price.toDoubleOrNull() ?: 0.0) * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Order") })
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Your cart is empty.", style = MaterialTheme.typography.headlineSmall)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(item = item, authViewModel = authViewModel)
                    }
                }
                OrderSummary(total = total)
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, authViewModel: AuthViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.kitchen.name, fontWeight = FontWeight.Bold)
                Text("$${item.kitchen.price}", color = MaterialTheme.colorScheme.primary)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { authViewModel.decreaseCartItemQuantity(item) },
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("-", fontSize = 16.sp)
                }
                Text(text = item.quantity.toString(), style = MaterialTheme.typography.bodyLarge)
                OutlinedButton(
                    onClick = { authViewModel.increaseCartItemQuantity(item) },
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("+", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun OrderSummary(total: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total:", style = MaterialTheme.typography.headlineSmall)
            val format = NumberFormat.getCurrencyInstance(Locale.US)
            Text(
                format.format(total),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* TODO: Checkout logic */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Checkout")
        }
    }
}
