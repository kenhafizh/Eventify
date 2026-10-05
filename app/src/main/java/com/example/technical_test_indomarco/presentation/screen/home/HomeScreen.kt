package com.example.technical_test_indomarco.presentation.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.asImageBitmap
import com.example.technical_test_indomarco.R
import com.example.technical_test_indomarco.di.DatabaseModule
import com.example.technical_test_indomarco.domain.model.Event
import com.example.technical_test_indomarco.presentation.component.BottomNavBar
import com.example.technical_test_indomarco.presentation.component.BottomNavTab
import com.example.technical_test_indomarco.presentation.component.EventListWidget
import com.example.technical_test_indomarco.presentation.component.LogoutBottomSheet
import com.example.technical_test_indomarco.presentation.viewmodel.HomeViewModel
import com.example.technical_test_indomarco.ui.theme.TechnicalTestIndomarcoTheme
import com.example.technical_test_indomarco.utils.FileUtils

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    loginSuccessMessage: String? = null,
    onLoginSuccessMessageShown: () -> Unit = {},
    onEventClick: (Int) -> Unit = {},
    onSeeAllClick: () -> Unit = {},
    onAddEventClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(
            DatabaseModule.provideGetAllEventsUseCase(LocalContext.current)
        )
    )
) {
    val sections by viewModel.sections.collectAsState()
    val upcomingEvent = sections.upcoming
    val otherEvents = sections.others
    var showLogoutSheet by remember { mutableStateOf(false) }
    val logoutSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginSuccessMessage) {
        if (!loginSuccessMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(loginSuccessMessage)
            onLoginSuccessMessageShown()
        }
    }

    Scaffold(
        containerColor = Color(0xFFEEF2FF),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            ) { data ->
                Snackbar(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = data.visuals.message,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = BottomNavTab.ADD_EVENT,
                onAddEventClick = onAddEventClick,
                onLogoutClick = { showLogoutSheet = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.app_logo),
                    contentDescription = "App logo",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )
                Text("Eventify", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.padding(bottom = 24.dp))
            Text("Upcoming Event", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.padding(bottom = 8.dp))
            if (upcomingEvent == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No upcoming events",
                        color = Color(0xFF6B7280),
                        fontSize = 14.sp
                    )
                }
            } else {
                UpcomingCard(
                    event = upcomingEvent,
                    onClick = { onEventClick(upcomingEvent.id) }
                )
            }
            Spacer(modifier = Modifier.padding(bottom = 24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Other Event", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "See All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4F46E5), modifier = Modifier.clickable { onSeeAllClick() }
                )
                Spacer(modifier = Modifier.padding(end = 2.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Other Event",
                    modifier = Modifier
                        .size(12.dp)
                        .clickable { onSeeAllClick() },
                    tint = Color(0xFF4F46E5)
                )

            }
            Spacer(modifier = Modifier.padding(bottom = 8.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (otherEvents.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No events added yet",
                                color = Color(0xFF6B7280),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(otherEvents.size) { index ->
                        val item = otherEvents[index]
                        EventListWidget(
                            title = item.eventName,
                            dateTime = "${item.startDate}, ${item.startTime}",
                            organizer = item.organizer.ifBlank { "Event Organizer" },
                            location = item.location.ifBlank { "Online / Venue" },
                            bannerPath = item.bannerPath,
                            onClick = { onEventClick(item.id) }
                        )
                    }
                }
            }
        }
    }

    if (showLogoutSheet) {
        LogoutBottomSheet(
            sheetState = logoutSheetState,
            onDismiss = { showLogoutSheet = false },
            onConfirmLogout = {
                showLogoutSheet = false
                onLogoutClick()
            }
        )
    }
}

@Composable
fun UpcomingCard(
    event: Event,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val bannerBitmap = remember(event.bannerPath) {
        FileUtils.loadBitmapFromPath(context, event.bannerPath)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        if (bannerBitmap != null) {
            Image(
                bitmap = bannerBitmap.asImageBitmap(),
                contentDescription = "Upcoming Event Closest Thumbnail",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(R.drawable.resource_default),
                contentDescription = "Upcoming Event Closest Thumbnail",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = event.eventName,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Event date",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${event.startDate}, ${event.startTime} - ${event.endDate}, ${event.endTime}",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Event location",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${event.organizer.ifBlank { "Organizer not specified" }} - ${event.location.ifBlank { "Location not specified" }}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
            Text(
                text = event.description.ifBlank { "No event description available." },
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onClick() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "View Details",
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(
                    modifier = Modifier.padding(
                        end = 4.dp
                    )
                )
                Icon(
                    Icons.Filled.KeyboardDoubleArrowRight,
                    contentDescription = "Arrow",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    TechnicalTestIndomarcoTheme {
        HomeScreen()
    }
}