package com.example.fifa2026poolpredictions.ui.selections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CountdownTimer(deadline: Long) {
    var timeLeft by remember { mutableLongStateOf(deadline - System.currentTimeMillis()) }
    
    LaunchedEffect(key1 = deadline) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft = deadline - System.currentTimeMillis()
        }
    }

    if (timeLeft > 0) {
        val days = timeLeft / (1000 * 60 * 60 * 24)
        val hours = (timeLeft % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)), // green-50
            border = BorderStroke(1.dp, Color(0xFFDCFCE7)) // green-100
        ) {
            Text(
                text = "you have still $days days $hours hours to cast your selections",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF166534), // green-800
                fontWeight = FontWeight.Medium
            )
        }
    }
}
