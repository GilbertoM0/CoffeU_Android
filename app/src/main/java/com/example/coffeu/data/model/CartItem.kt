package com.example.coffeu.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

data class CartItem(
    val kitchen: Kitchen,
    private var initialQuantity: Int = 1
) {
    var quantity: Int by mutableIntStateOf(initialQuantity)
}
