package com.example.workcost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Строительные", "Земляные", "Электрика", "Кровля")
    val categories = listOf("Строительные", "Земляные", "Электрика", "Кровля")

    // Состояние: выбранные работы (id), объёмы и цены
    val selectedWorks = remember { mutableStateListOf<Int>() }
    val volumes = remember { mutableStateMapOf<Int, String>() }
    val prices = remember { mutableStateMapOf<Int, String>() }
    var showEstimate by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Калькулятор работ") })
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Поиск работ...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    singleLine = true
                )
            }
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(title) },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showEstimate = true }) {
                Text("Смета")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            val currentCategory = categories[selectedTab]
            val filteredWorks = WorkRepository.allWorks.filter {
                it.category == currentCategory &&
                (searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredWorks) { work ->
                    WorkRow(
                        work = work,
                        isSelected = selectedWorks.contains(work.id),
                        volume = volumes[work.id] ?: "",
                        price = prices[work.id] ?: work.price.toString(),
                        onToggle = {
                            if (selectedWorks.contains(work.id)) selectedWorks.remove(work.id)
                            else selectedWorks.add(work.id)
                        },
                        onVolumeChange = { volumes[work.id] = it },
                        onPriceChange = { prices[work.id] = it }
                    )
                }
            }
        }
    }

    if (showEstimate) {
        val selectedItems = WorkRepository.allWorks.filter { selectedWorks.contains(it.id) }
        AlertDialog(
            onDismissRequest = { showEstimate = false },
            title = { Text("Смета для заказчика") },
            text = {
                Column {
                    if (selectedItems.isEmpty()) {
                        Text("Ничего не выбрано")
                    } else {
                        selectedItems.forEach { work ->
                            val vol = volumes[work.id]?.toDoubleOrNull() ?: 0.0
                            val price = prices[work.id]?.toDoubleOrNull() ?: work.price
                            val sum = vol * price
                            Text("${work.name}: $vol ${work.unit} × $price = ${"%.2f".format(sum)} ₽")
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showEstimate = false }) {
                    Text("Закрыть")
                }
            }
        )
    }
}

@Composable
fun WorkRow(
    work: WorkItem,
    isSelected: Boolean,
    volume: String,
    price: String,
    onToggle: () -> Unit,
    onVolumeChange: (String) -> Unit,
    onPriceChange: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isSelected, onCheckedChange = { onToggle() })
                Column(modifier = Modifier.weight(1f)) {
                    Text(work.name, fontWeight = FontWeight.Bold)
                    Text("Ед. изм.: ${work.unit}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = volume,
                    onValueChange = onVolumeChange,
                    label = { Text("Объём") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = onPriceChange,
                    label = { Text("Цена") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }
    }
}
