package com.example.cupcake.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cupcake.data.KorpaViewModel
import com.example.cupcake.data.StavkaKorpe
import com.example.cupcake.R

@Composable
fun KorpaScreen(navController: NavController, korpaViewModel: KorpaViewModel, korisnikJson: String) {
    val stavkeKorpe by korpaViewModel.korpaStavke.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.korpa),
            contentDescription = "Banner za korpu",
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))

        if (stavkeKorpe.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Korpa je prazna",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val ukupnaCenaRacuna = stavkeKorpe.sumOf { it.proizvod.cena * it.kolicina }
            val maxDaniDostave = stavkeKorpe.maxOfOrNull {
                it.proizvod.standardnaDostavaDani
            } ?: 0
            val imaStraniProizvod = stavkeKorpe.any { it.proizvod.zemljaPorekla != "Srbija" }
            val daniDostave = if (imaStraniProizvod) maxDaniDostave + 1 else maxDaniDostave

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(stavkeKorpe.size) { index ->
                    val stavkaKorpe = stavkeKorpe[index]
                    StavkaKorpeKartica(stavkaKorpe) {
                        korpaViewModel.ukloniIzKorpe(context, stavkaKorpe.proizvod.id)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Ukupna cena: %.2f RSD".format(ukupnaCenaRacuna), style = MaterialTheme.typography.titleMedium)
            Text("Broj dana za isporuku: $daniDostave", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate("potvrda_korpe/$korisnikJson")
                },
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
            ) {
                Text("Potvrdi korpu")
            }
        }
    }
}

@Composable
fun StavkaKorpeKartica(stavkaKorpe: StavkaKorpe, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = stavkaKorpe.proizvod.naziv, style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp))
                    Text(text = "Cena: ${stavkaKorpe.proizvod.cena}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp))
                    Text(text = "Količina: ${stavkaKorpe.kolicina}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp))
                    Text(text = "Ukupna cena: %.2f RSD".format(stavkaKorpe.proizvod.cena * stavkaKorpe.kolicina), style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp))
                }
                IconButton(onClick = onRemove) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Obrisi stavku iz korpe")
                }
            }
        }
    }
}
