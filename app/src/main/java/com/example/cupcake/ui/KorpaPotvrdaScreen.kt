package com.example.cupcake.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import com.example.cupcake.R
import com.example.cupcake.data.KorpaViewModel
import com.example.cupcake.data.Korisnik
import com.google.gson.Gson

@Composable
fun KorpaPotvrdaScreen(navController: NavController, korpaViewModel: KorpaViewModel, backStackEntry: NavBackStackEntry) {
    val context = LocalContext.current
    val stavkeKorpe by korpaViewModel.korpaStavke.collectAsState()

    val korisnikJson = backStackEntry.arguments?.getString("korisnikJson")
    val korisnik = Gson().fromJson(korisnikJson, Korisnik::class.java)

    var adresa by remember { mutableStateOf(korisnik.adresa) }
    var koristiBrzuIsporuku by remember { mutableStateOf(false) }
    var prikaziConfirmationDialog by remember { mutableStateOf(false) }
    var prikaziUspesnuNarudzbinuDialog by remember { mutableStateOf(false) }
    var prikaziAdresuPraznuDialog by remember { mutableStateOf(false) }

    val pocetnaUkupnaCena = stavkeKorpe.sumOf { it.proizvod.cena * it.kolicina }
    val ukupnaCena = if (koristiBrzuIsporuku) pocetnaUkupnaCena * 1.2 else pocetnaUkupnaCena * 0.9
    val maxDaniIsporuke = stavkeKorpe.maxOfOrNull {
        if (koristiBrzuIsporuku) it.proizvod.brzaDostavaDani else it.proizvod.standardnaDostavaDani
    } ?: 0
    val imaStraniProizvod = stavkeKorpe.any { it.proizvod.zemljaPorekla != "Srbija" }
    val daniIsporuke = if (imaStraniProizvod) maxDaniIsporuke + 1 else maxDaniIsporuke

    val listState = rememberLazyListState()
    val imageSize by animateDpAsState(
        if (listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0) 75.dp else 200.dp
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageSize),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.potvrdakorpe),
                contentDescription = "Baner za potvrdu korpe"
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = adresa,
            onValueChange = { adresa = it },
            label = { Text("Adresa za dostavu") },
            colors = OutlinedTextFieldDefaults.colors(Color.Gray),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Brza isporuka", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = koristiBrzuIsporuku,
                onCheckedChange = { koristiBrzuIsporuku = it }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f)
        ) {
            items(stavkeKorpe.size) { index ->
                val stavkaKorpe = stavkeKorpe[index]
                StavkaKorpeKartica(stavkaKorpe, onRemove = {
                    korpaViewModel.ukloniIzKorpe(context, stavkaKorpe.proizvod.id)
                })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Ukupna cena: %.2f RSD".format(ukupnaCena), style = MaterialTheme.typography.titleMedium)
        Text("Broj dana za isporuku: $daniIsporuke", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (adresa.isBlank()) {
                    prikaziAdresuPraznuDialog = true
                } else {
                    prikaziConfirmationDialog = true
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
            enabled = stavkeKorpe.isNotEmpty()
        ) {
            Text("Potvrdi porudzbinu")
        }
        if (prikaziAdresuPraznuDialog) {
            AlertDialog(
                onDismissRequest = { prikaziAdresuPraznuDialog = false },
                confirmButton = {
                    Button(
                        onClick = { prikaziAdresuPraznuDialog = false }
                    ) {
                        Text("OK")
                    }
                },
                title = { Text("Greška") },
                text = { Text("Adresa za dostavu ne sme biti prazna.") }
            )
        }
        if (prikaziConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { prikaziConfirmationDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            prikaziConfirmationDialog = false
                            prikaziUspesnuNarudzbinuDialog = true
                        }
                    ) {
                        Text("Da")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { prikaziConfirmationDialog = false }
                    ) {
                        Text("Ne")
                    }
                },
                title = { Text("Potvrda porudžbine") },
                text = { Text("Da li ste sigurni da želite da potvrdite porudžbinu?") }
            )
        }
        if (prikaziUspesnuNarudzbinuDialog) {
            AlertDialog(
                onDismissRequest = { prikaziUspesnuNarudzbinuDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            prikaziUspesnuNarudzbinuDialog = false
                            korpaViewModel.ocistiKorpu()
                            navController.navigate("lista_proizvoda/$korisnikJson")
                        }
                    ) {
                        Text("OK")
                    }
                },
                title = { Text("Porudžbina uspešno potvrđena") },
                text = {
                    Column {
                        Text("Ukupna cena: %.2f RSD".format(ukupnaCena))
                        Text("Broj dana za isporuku: $daniIsporuke")
                        Text("Ime: ${korisnik.ime}")
                        Text("Prezime: ${korisnik.prezime}")
                        Text("Email: ${korisnik.email}")
                        Text("Adresa za dostavu: $adresa")
                        Text("Zemlja: ${korisnik.zemlja}")
                    }
                }
            )
        }
    }
}
