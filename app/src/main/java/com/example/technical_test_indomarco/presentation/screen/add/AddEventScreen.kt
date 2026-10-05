package com.example.technical_test_indomarco.presentation.screen.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.technical_test_indomarco.di.DatabaseModule
import com.example.technical_test_indomarco.presentation.component.BottomNavBar
import com.example.technical_test_indomarco.presentation.component.BottomNavTab
import com.example.technical_test_indomarco.presentation.component.LogoutBottomSheet
import com.example.technical_test_indomarco.presentation.viewmodel.AddEventViewModel
import com.example.technical_test_indomarco.presentation.viewmodel.SaveEventState
import com.example.technical_test_indomarco.ui.theme.TechnicalTestIndomarcoTheme
import com.example.technical_test_indomarco.utils.FileUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.technical_test_indomarco.utils.Helper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onEventSaved: () -> Unit = onBackClick,
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: AddEventViewModel = viewModel(
        factory = AddEventViewModel.Factory(
            DatabaseModule.provideSaveEventUseCase(context)
        )
    )
    val saveState by viewModel.saveState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var eventName by rememberSaveable { mutableStateOf("") }
    var bannerUri by remember { mutableStateOf<Uri?>(null) }
    var startDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var organizer by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showLogoutSheet by remember { mutableStateOf(false) }
    val logoutSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var eventNameError by remember { mutableStateOf(false) }
    var startDateError by remember { mutableStateOf(false) }
    var startTimeError by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf(false) }
    var organizerError by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        bannerUri = uri
    }

    val bannerBitmap = remember(bannerUri) {
        Helper.loadBitmapFromUri(context, bannerUri)
    }

    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is SaveEventState.Success -> {
                launch {
                    snackbarHostState.showSnackbar("Event saved successfully!")
                }
                delay(1200)
                onEventSaved()
            }
            is SaveEventState.Error -> {
                snackbarHostState.showSnackbar(state.message)
            }
            else -> Unit
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FF))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF111827)
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Add Event",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                }
                Spacer(modifier = Modifier.size(48.dp))
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Event Name",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )
                    Text(
                        " *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = eventName,
                    onValueChange = {
                        eventName = it
                        if (eventNameError && it.isNotBlank()) eventNameError = false
                    },
                    isError = eventNameError,
                    placeholder = {
                        Text(
                            "e.g. Annual Tech Innovators Summit 2025",
                            fontSize = 13.sp,
                            color = Color(0xFFADB5BD)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.EditNote,
                            contentDescription = null,
                            tint = if (eventNameError) Color(0xFFDC2626) else Color(0xFFADB5BD),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        errorContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (eventNameError) 1.5.dp else 1.dp,
                            color = if (eventNameError) Color(0xFFDC2626) else Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(10.dp)
                        )
                )
                if (eventNameError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Event name is required",
                        fontSize = 11.sp,
                        color = Color(0xFFDC2626),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "Description",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = {
                        Text(
                            "Event description...",
                            fontSize = 13.sp,
                            color = Color(0xFFADB5BD),
                            lineHeight = 20.sp
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                )

                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Location",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )
                    Text(
                        " *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = location,
                    onValueChange = {
                        location = it
                        if (locationError && it.isNotBlank()) locationError = false
                    },
                    isError = locationError,
                    placeholder = {
                        Text(
                            "Venue name, street address or online link",
                            fontSize = 13.sp,
                            color = Color(0xFFADB5BD)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = if (locationError) Color(0xFFDC2626) else Color(0xFFADB5BD),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        errorContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (locationError) 1.5.dp else 1.dp,
                            color = if (locationError) Color(0xFFDC2626) else Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(10.dp)
                        )
                )
                if (locationError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Location is required",
                        fontSize = 11.sp,
                        color = Color(0xFFDC2626),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFF4F46E5),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Schedule & Duration",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Start Date",
                                fontSize = 11.sp,
                                color = if (startDateError) Color(0xFFDC2626) else Color(0xFF6B7280)
                            )
                            Text(
                                " *",
                                fontSize = 14.sp,
                                color = Color(0xFFDC2626)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        DateTimeField(
                            value = startDate,
                            placeholder = "Select Date",
                            icon = Icons.Filled.DateRange,
                            isError = startDateError,
                            errorMessage = if (startDateError) "Start date is required" else null,
                            onClick = {
                                Helper.showStartDatePicker(
                                    context = context,
                                    startDate = startDate,
                                    endDate = endDate,
                                    onStartDateSelected = {
                                        startDate = it
                                        startDateError = false
                                    },
                                    onResetEndDate = { endDate = "" }
                                )
                            }
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Start Time",
                                fontSize = 11.sp,
                                color = if (startTimeError) Color(0xFFDC2626) else Color(0xFF6B7280)
                            )
                            Text(
                                " *",
                                fontSize = 11.sp,
                                color = Color(0xFFDC2626)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        DateTimeField(
                            value = startTime,
                            placeholder = "Select Time",
                            icon = Icons.Filled.Schedule,
                            isError = startTimeError,
                            errorMessage = if (startTimeError) "Start time is required" else null,
                            onClick = {
                                Helper.showTimePicker(context, startTime) {
                                    startTime = it
                                    startTimeError = false
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("End Date", fontSize = 11.sp, color = Color(0xFF6B7280))
                        Spacer(modifier = Modifier.height(4.dp))
                        DateTimeField(
                            value = endDate,
                            placeholder = "Select Date",
                            icon = Icons.Filled.DateRange,
                            onClick = {
                                Helper.showEndDatePicker(context, startDate, endDate) {
                                    endDate = it
                                }
                            }
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("End Time", fontSize = 11.sp, color = Color(0xFF6B7280))
                        Spacer(modifier = Modifier.height(4.dp))
                        DateTimeField(
                            value = endTime,
                            placeholder = "Select Time",
                            icon = Icons.Filled.Schedule,
                            onClick = {
                                Helper.showTimePicker(context, endTime) {
                                    endTime = it
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Organizer Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )
                    Text(
                        " *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = organizer,
                    onValueChange = {
                        organizer = it
                        if (organizerError && it.isNotBlank()) organizerError = false
                    },
                    isError = organizerError,
                    placeholder = {
                        Text(
                            "Event Organizer...",
                            fontSize = 13.sp,
                            color = Color(0xFFADB5BD)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Group,
                            contentDescription = null,
                            tint = if (organizerError) Color(0xFFDC2626) else Color(0xFFADB5BD),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        errorContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (organizerError) 1.5.dp else 1.dp,
                            color = if (organizerError) Color(0xFFDC2626) else Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(10.dp)
                        )
                )
                if (organizerError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Organizer is required",
                        fontSize = 11.sp,
                        color = Color(0xFFDC2626),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Upload Thumbnail",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )
                    Text(
                        if (bannerBitmap != null) "Change Photo" else "PNG, JPG up to 10MB",
                        fontSize = 11.sp,
                        color = if (bannerBitmap != null) Color(0xFF4F46E5) else Color(0xFF6B7280),
                        fontWeight = if (bannerBitmap != null) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEEF2FF))
                        .border(
                            width = 1.5.dp,
                            color = Color(0xFF4F46E5).copy(alpha = 0.25f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (bannerBitmap != null) {
                        Image(
                            bitmap = bannerBitmap.asImageBitmap(),
                            contentDescription = "Event Banner Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CameraAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF4F46E5),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Tap to upload thumbnail",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF111827)
                            )
                            Text(
                                "High resolution 16:9 banner recommended",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        var hasError = false
                        if (eventName.isBlank()) {
                            eventNameError = true
                            hasError = true
                        }
                        if (startDate.isBlank()) {
                            startDateError = true
                            hasError = true
                        }
                        if (startTime.isBlank()) {
                            startTimeError = true
                            hasError = true
                        }
                        if (location.isBlank()) {
                            locationError = true
                            hasError = true
                        }
                        if (organizer.isBlank()) {
                            organizerError = true
                            hasError = true
                        }

                        if (!hasError) {
                            val savedBannerPath = bannerUri?.let { uri ->
                                FileUtils.saveImageToInternalStorage(context, uri)
                            } ?: ""
                            viewModel.publishEvent(
                                eventName   = eventName,
                                bannerPath  = savedBannerPath,
                                startDate   = startDate,
                                startTime   = startTime,
                                endDate     = endDate,
                                endTime     = endTime,
                                location    = location,
                                organizer   = organizer,
                                description = description
                            )
                        }
                    },
                    enabled = saveState !is SaveEventState.Loading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (saveState is SaveEventState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            "Publish Event",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        BottomNavBar(
            selectedTab = BottomNavTab.ADD_EVENT,
            onAddEventClick = {},
            onLogoutClick = { showLogoutSheet = true }
        )
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) { snackbarData ->
        val isSuccess = snackbarData.visuals.message.contains("success", ignoreCase = true)
        Snackbar(
            snackbarData = snackbarData,
            containerColor = if (isSuccess) Color(0xFF16A34A) else Color(0xFFDC2626),
            contentColor = Color.White,
            shape = RoundedCornerShape(12.dp)
        )
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
private fun DateTimeField(
    value: String,
    placeholder: String,
    icon: ImageVector,
    isError: Boolean = false,
    errorMessage: String? = null,
    onClick: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(
                    width = if (isError) 1.5.dp else 1.dp,
                    color = if (isError) Color(0xFFDC2626) else Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isError) Color(0xFFDC2626) else Color(0xFF4F46E5),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value.ifBlank { placeholder },
                fontSize = 13.sp,
                color = if (value.isNotBlank()) Color(0xFF111827) else Color(0xFFADB5BD),
                fontWeight = if (value.isNotBlank()) FontWeight.Medium else FontWeight.Normal
            )
        }
        if (isError && !errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                fontSize = 11.sp,
                color = Color(0xFFDC2626),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddEventScreenPreview() {
    TechnicalTestIndomarcoTheme {
        AddEventScreen()
    }
}