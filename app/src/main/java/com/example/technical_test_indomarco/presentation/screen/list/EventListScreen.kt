package com.example.technical_test_indomarco.presentation.screen.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.technical_test_indomarco.di.DatabaseModule
import com.example.technical_test_indomarco.domain.model.Event
import com.example.technical_test_indomarco.presentation.component.EventListWidget
import com.example.technical_test_indomarco.presentation.viewmodel.EventListViewModel
import com.example.technical_test_indomarco.ui.theme.TechnicalTestIndomarcoTheme

@Composable
fun EventListScreen(
    modifier: Modifier = Modifier,
    onEventClick: (Int) -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: EventListViewModel = viewModel(
        factory = EventListViewModel.Factory(
            DatabaseModule.provideGetEventsPagedUseCase(LocalContext.current)
        )
    )
) {
    val search by viewModel.searchQuery.collectAsState()
    val events: LazyPagingItems<Event> =
        viewModel.pagingEvents.collectAsLazyPagingItems()

    Scaffold() { innerPadding ->
        Column(
            modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    "Back Icon",
                    modifier
                        .size(24.dp)
                        .clickable { onBackClick() })
                Spacer(modifier.padding(end = 12.dp))
                Column {
                    Text("Jelajahi Event", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Jelajahi berbagai jenis event di Eventify",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light
                    )
                }

            }
            Spacer(modifier.padding(bottom = 24.dp))
            TextField(
                value = search,
                singleLine = true,
                onValueChange = viewModel::onSearchQueryChange,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color(0xFF111827),
                    unfocusedTextColor = Color(0xFF111827),
                    cursorColor = Color(0xFF4F46E5),
                ),
                trailingIcon = {
                    Icon(
                        Icons.Default.Search, "search", tint = Color(0xFF929292)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "Search")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search, keyboardType = KeyboardType.Text
                ),
            )
            Spacer(modifier.padding(bottom = 20.dp))

            when {
                events.loadState.refresh is LoadState.Loading && events.itemCount == 0 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4F46E5))
                    }
                }

                events.loadState.refresh is LoadState.Error && events.itemCount == 0 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Gagal memuat data. Tarik untuk mencoba lagi.",
                            color = Color(0xFF6B7280),
                            fontSize = 14.sp
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (events.itemCount == 0) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (search.isBlank()) "No events added yet" else "No matching events found",
                                        color = Color(0xFF6B7280),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else {
                            items(
                                count = events.itemCount,
                                key = { index -> events[index]?.id ?: -1 }
                            ) { index ->
                                val item = events[index]
                                if (item != null) {
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
                            // Footer: spinner saat memuat halaman berikutnya (infinite scroll)
                            if (events.loadState.append is LoadState.Loading) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = Color(0xFF4F46E5),
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun EventListScreenPreview() {
    TechnicalTestIndomarcoTheme {
        EventListScreen()
    }
}
