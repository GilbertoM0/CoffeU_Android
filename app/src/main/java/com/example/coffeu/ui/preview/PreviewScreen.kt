package com.example.coffeu.ui.preview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeu.R
import com.example.coffeu.ui.theme.CoffeUTheme
import com.example.coffeu.ui.theme.PrimaryNormal
import com.example.coffeu.ui.theme.LabelSecondary
import com.example.coffeu.ui.theme.LabelTertiary
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String
)

private val onboardingPages = listOf(
    OnboardingPage(
        imageRes = R.drawable.preview_screen1,
        title = "Descubre Delicias a Cualquier Hora, en Cualquier Lugar",
        description = "Explora un sinfín de opciones, ordena en segundos y disfruta de una entrega rápida en tu puerta."
    ),
    OnboardingPage(
        imageRes = R.drawable.preview_screen2,
        title = "Ordena con Facilidad, Cuando Quieras",
        description = "Descubre nuevos sabores, personaliza tus comidas y sigue tu pedido en tiempo real con facilidad."
    ),
    OnboardingPage(
        imageRes = R.drawable.preview_screen3,
        title = "Sigue y Disfruta Cada Bocado del Viaje",
        description = "Desde el desayuno hasta la cena, encuentra tus platos favoritos y recíbelos rápido y frescos."
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreviewScreen(
    onNavigateToLogin: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == onboardingPages.size - 1

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                OnboardingPageContent(page = onboardingPages[pageIndex])
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.padding(bottom = 40.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(onboardingPages.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) PrimaryNormal else Color.LightGray.copy(alpha = 0.5f)
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(8.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (isLastPage) {
                            onNavigateToLogin()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNormal)
                ) {
                    Text(
                        text = if (isLastPage) "Comenzar" else "Siguiente",
                        fontWeight = FontWeight.SemiBold,
                        color = LabelTertiary,
                        fontSize = 16.sp
                    )
                }

                if (isLastPage) {
                    Row(
                        modifier = Modifier.padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("¿Ya tienes una cuenta? ", color = LabelSecondary)
                        TextButton(onClick = onNavigateToLogin) {
                           Text("Iniciar Sesión", color = PrimaryNormal, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Gradient overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f),
                            Color.Black
                        ),
                        startY = 800f
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 220.dp), // Make space for buttons and indicators
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = page.title,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = page.description,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    CoffeUTheme(darkTheme = true) {
        PreviewScreen(onNavigateToLogin = {})
    }
}
