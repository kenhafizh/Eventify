package com.example.technical_test_indomarco.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.technical_test_indomarco.ui.theme.TechnicalTestIndomarcoTheme

enum class BottomNavTab { ADD_EVENT, LOGOUT }

@Composable
fun BottomNavBar(
    selectedTab: BottomNavTab = BottomNavTab.ADD_EVENT,
    onAddEventClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Column {
        HorizontalDivider(color = Color(0xFFE5E7EB))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem(
                icon = Icons.Filled.Add,
                label = "Add Event",
                selected = selectedTab == BottomNavTab.ADD_EVENT,
                onClick = onAddEventClick
            )
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                label = "Logout",
                selected = selectedTab == BottomNavTab.LOGOUT,
                onClick = onLogoutClick
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) Color(0xFF4F46E5) else Color(0xFF6B7280)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = tint,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavBarPreview() {
    TechnicalTestIndomarcoTheme {
        BottomNavBar(selectedTab = BottomNavTab.ADD_EVENT)
    }
}
