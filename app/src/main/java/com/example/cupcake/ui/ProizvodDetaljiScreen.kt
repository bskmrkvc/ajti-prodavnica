package com.example.cupcake.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import com.example.cupcake.data.Proizvod
import com.example.cupcake.data.KorpaViewModel
import com.example.cupcake.utils.procitajProizvodeIzJSONa
import com.example.cupcake.utils.obrisiProizvodeIzFajla
import com.example.cupcake.utils.upisiProizvodeUJSON

@Composable
fun ProizvodDetaljiScreen(navController: NavController, backStackEntry: NavBackStackEntry, korpaViewModel: KorpaViewModel = viewModel()) {
    val context = LocalContext.current
    val productId = backStackEntry.arguments?.getString("proizvodId")?.toIntOrNull()
    val korisnikJson = backStackEntry.arguments?.getString("korisnikJson") ?: ""
    var proizvod by remember { mutableStateOf<Proizvod?>(null) }
    var prikaziPoruku by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        val products = procitajProizvodeIzJSONa(context, "proizvodi.json")
        korpaViewModel.setProizvodi(products)
        proizvod = products.find { it.id == productId }
    }

    proizvod?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = it.naziv, style = MaterialTheme.typography.headlineLarge)
                Row {
                    IconButton(
                        onClick = {
                            obrisiProizvodeIzFajla(context, "proizvodi.json", it.id)
                            navController.navigateUp()
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Obrisi proizvod")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            navController.navigate("izmeni_proizvod/${it.id}/$korisnikJson")
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Izmeni proizvod")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Cena: ${it.cena}", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Kolicina: ${it.kolicina}", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Dani brzom dostavom: ${it.brzaDostavaDani} dan(a)", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Dani standardnom dostavom: ${it.standardnaDostavaDani} dan(a)", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Zemlja porekla: ${it.zemljaPorekla}", style = MaterialTheme.typography.headlineMedium)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(if (it.kolicina > 0) Color.Green else Color.Red, shape = MaterialTheme.shapes.small)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (it.kolicina > 0) "Na stanju" else "Nema na stanju",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                )
            }

            if (it.kolicina > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        korpaViewModel.dodajUKorpu(it)
                        val updatedProduct = it.copy(kolicina = it.kolicina - 1)
                        val products = korpaViewModel.proizvodi.value.toMutableList()
                        val productIndex = products.indexOfFirst { product -> product.id == it.id }
                        if (productIndex != -1) {
                            products[productIndex] = updatedProduct
                            upisiProizvodeUJSON(context, "proizvodi.json", products)
                            proizvod = updatedProduct
                        }
                        prikaziPoruku = true
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Dodaj u korpu")
                }
            }

            if (prikaziPoruku) {
                Snackbar(
                    action = {
                        Button(onClick = { prikaziPoruku = false }) {
                            Text("OK")
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Proizvod je uspešno dodat u korpu")
                }
            }
        }
    } ?: run {
        Text(text = "Proizvod nije pronadjen!", style = MaterialTheme.typography.headlineMedium)
    }
}
