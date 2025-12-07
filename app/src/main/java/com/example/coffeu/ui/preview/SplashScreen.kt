package com.example.coffeu.ui.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeu.R
import com.example.coffeu.ui.theme.CoffeUTheme
import com.example.coffeu.ui.theme.LabelPrimary
import com.example.coffeu.ui.theme.PrimaryLight

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryLight),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.coffeeu_logo),
            contentDescription = "CoffeeU Logo",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "CoffeeU",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = LabelPrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    CoffeUTheme {
        SplashScreen()
    }
}