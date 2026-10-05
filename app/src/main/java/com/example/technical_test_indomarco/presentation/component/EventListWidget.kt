package com.example.technical_test_indomarco.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.technical_test_indomarco.R
import com.example.technical_test_indomarco.ui.theme.TechnicalTestIndomarcoTheme
import com.example.technical_test_indomarco.utils.FileUtils

@Composable
fun EventListWidget(
    modifier: Modifier = Modifier,
    title: String = "Mobile Dev Meetup 2026",
    dateTime: String = "12 September 2025, 09.00 WIB",
    organizer: String = "Mobile Developer Jakarta",
    location: String = "Jakarta Convention Center",
    bannerPath: String = "",
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val bannerBitmap = remember(bannerPath) {
        FileUtils.loadBitmapFromPath(context, bannerPath)
    }

    Box(
        modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable { onClick() }
    ) {
        Row(
            modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            if (bannerBitmap != null) {
                Image(
                    bitmap = bannerBitmap.asImageBitmap(),
                    contentDescription = "Event banner",
                    modifier = Modifier
                        .size(height = 96.dp, width = 84.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(
                        R.drawable.resource_default,
                    ),
                    contentDescription = "Other Event thumbnail",
                    modifier = Modifier
                        .size(height = 96.dp, width = 84.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier.padding(end = 8.dp))
            Column {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier.padding(bottom = 8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Event date",
                        modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier.width(4.dp))
                    Text(
                        dateTime,
                        fontSize = 12.sp,
                        lineHeight = 4.sp,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Event organizer",
                        modifier.size(16.dp), tint = Color.Gray
                    )
                    Spacer(modifier.width(4.dp))
                    Text(
                        organizer,
                        fontSize = 12.sp,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Event location",
                        modifier.size(16.dp), tint = Color.Gray
                    )
                    Spacer(modifier.width(4.dp))
                    Text(
                        location,
                        fontSize = 12.sp,
                        lineHeight = 4.sp,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun EventListWidgetPreview() {
    TechnicalTestIndomarcoTheme {
        EventListWidget()
    }

}