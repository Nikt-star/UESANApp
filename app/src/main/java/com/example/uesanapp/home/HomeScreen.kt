package com.example.uesanapp.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

val mockCountries = listOf(
    CountryModel(name = "Argentina", ranking = 1, continent = "América", flagUrl = "https://flagcdn.com/w320/ar.png"),
    CountryModel(name = "Francia", ranking = 2, continent = "Europa", flagUrl = "https://flagcdn.com/w320/fr.png"),
    CountryModel(name = "España", ranking = 3, continent = "Europa", flagUrl = "https://flagcdn.com/w320/es.png"),
    CountryModel(name = "Inglaterra", ranking = 4, continent = "Europa", flagUrl = "https://flagcdn.com/w320/gb.png"),
    CountryModel(name = "Brasil", ranking = 5, continent = "América", flagUrl = "https://flagcdn.com/w320/br.png"),
    CountryModel(name = "Bélgica", ranking = 6, continent = "Europa", flagUrl = "https://flagcdn.com/w320/be.png"),
    CountryModel(name = "Países Bajos", ranking = 7, continent = "Europa", flagUrl = "https://flagcdn.com/w320/nl.png"),
    CountryModel(name = "Portugal", ranking = 8, continent = "Europa", flagUrl = "https://flagcdn.com/w320/pt.png"),
    CountryModel(name = "Colombia", ranking = 9, continent = "América", flagUrl = "https://flagcdn.com/w320/co.png"),
    CountryModel(name = "Italia", ranking = 10, continent = "Europa", flagUrl = "https://flagcdn.com/w320/it.png"),
    CountryModel(name = "Japón", ranking = 15, continent = "Asia", flagUrl = "https://flagcdn.com/w320/jp.png"),
    CountryModel(name = "Marruecos", ranking = 14, continent = "África", flagUrl = "https://flagcdn.com/w320/ma.png"),
    CountryModel(name = "Perú", ranking = 42, continent = "América", flagUrl = "https://flagcdn.com/w320/pe.png")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val continents = listOf("Todos") + mockCountries.map { it.continent }.distinct()
    var selectedContinent by remember { mutableStateOf("Todos") }

    val filteredCountries = if (selectedContinent == "Todos") {
        mockCountries
    } else {
        mockCountries.filter { it.continent == selectedContinent }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Ranking FIFA 2026",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Explorar por continente",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            LazyRow(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(continents) { continent ->
                    FilterChip(
                        selected = selectedContinent == continent,
                        onClick = { selectedContinent = continent },
                        label = { Text(continent) },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredCountries) { country ->
                    CountryCard(country)
                }
            }
        }
    }
}

@Composable
fun CountryCard(country: CountryModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de la bandera usando Coil
            AsyncImage(
                model = country.flagUrl,
                contentDescription = "Bandera de ${country.name}",
                modifier = Modifier
                    .size(80.dp, 50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = country.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = country.continent,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "#${country.ranking}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Posición",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
