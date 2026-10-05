package com.example.technical_test_indomarco.presentation.screen.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.technical_test_indomarco.R
import com.example.technical_test_indomarco.di.DatabaseModule
import com.example.technical_test_indomarco.presentation.viewmodel.DetailUiState
import com.example.technical_test_indomarco.presentation.viewmodel.DetailViewModel
import com.example.technical_test_indomarco.ui.theme.TechnicalTestIndomarcoTheme
import com.example.technical_test_indomarco.utils.FileUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    eventId: Int = 0,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    viewModel: DetailViewModel = viewModel(
        factory = DetailViewModel.Factory(
            DatabaseModule.provideGetEventByIdUseCase(LocalContext.current)
        )
    )
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(eventId) {
        if (eventId > 0) {
            viewModel.loadEvent(eventId)
        }
    }

    Scaffold(containerColor = Color.White) { innerPadding ->
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4F46E5))
                }
            }
            is DetailUiState.Error -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = state.message,
                            color = Color(0xFF6B7280),
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF4F46E5)
                            )
                        }
                    }
                }
            }
            is DetailUiState.Success -> {
                val event = state.event
                val bannerBitmap = remember(event.bannerPath) {
                    FileUtils.loadBitmapFromPath(context, event.bannerPath)
                }
                val scrollState = rememberScrollState()

                Column(
                    modifier = modifier
                        .padding(innerPadding)
                        .verticalScroll(scrollState)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        if (bannerBitmap != null) {
                            Image(
                                bitmap = bannerBitmap.asImageBitmap(),
                                contentDescription = "Event banner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.resource_default),
                                contentDescription = "Event thumbnail",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .size(36.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.Black.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back Icon",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = event.eventName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val dateDisplay = if (event.startDate == event.endDate || event.endDate.isBlank()) {
                            event.startDate
                        } else {
                            "${event.startDate} – ${event.endDate}"
                        }
                        val timeDisplay = if (event.startTime.isNotBlank() && event.endTime.isNotBlank()) {
                            "${event.startTime} – ${event.endTime}"
                        } else {
                            event.startTime.ifBlank { event.endTime }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        InfoCard {
                            EventInfoRow(
                                icon = Icons.Filled.DateRange,
                                iconTint = Color(0xFF4F46E5),
                                title = dateDisplay.ifBlank { "Date not specified" },
                                subtitle = timeDisplay.ifBlank { "Time not specified" },
                                caption = "Event schedule"
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoCard {
                            EventInfoRow(
                                icon = Icons.Filled.LocationOn,
                                iconTint = Color(0xFF4F46E5),
                                title = event.location.ifBlank { "Location not specified" },
                                subtitle = "Event Location",
                                caption = "Venue / Online"
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoCard {
                            EventInfoRow(
                                icon = Icons.Filled.Business,
                                iconTint = Color(0xFF4F46E5),
                                title = event.organizer.ifBlank { "Event Organizer" },
                                subtitle = "Official Organizer",
                                caption = "Organizer of ${event.eventName}"
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "About this Event",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = event.description.ifBlank { "No description available for this event." },
                            fontSize = 13.sp,
                            color = Color(0xFF374151),
                            lineHeight = 21.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF9FAFB))
            .border(
                width = 1.dp,
                color = Color(0xFFE5E7EB),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        content()
    }
}

@Composable
private fun EventInfoRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    caption: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEEF2FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )
            Text(
                text = caption,
                fontSize = 11.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    TechnicalTestIndomarcoTheme {
        DetailScreen()
    }
}