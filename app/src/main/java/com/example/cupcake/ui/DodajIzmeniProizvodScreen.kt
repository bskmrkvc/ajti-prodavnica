package com.example.cupcake.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cupcake.data.Proizvod
import com.example.cupcake.utils.azurirajProizvodeUJSON
import com.example.cupcake.utils.procitajProizvodeIzJSONa
import com.example.cupcake.utils.upisiProizvodeUJSON
import com.example.cupcake.R

@Composable
fun DodajIzmeniProizvodScreen(navController: NavHostController, proizvodId: Int? = null, korisnikJson: String) {
    val context = LocalContext.current
    var naziv by remember { mutableStateOf("") }
    var cena by remember { mutableStateOf("") }
    var kolicina by remember { mutableStateOf("") }
    var standardnaDostavaDani by remember { mutableStateOf("") }
    var brzaDostavaDani by remember { mutableStateOf("") }
    var zemljaPorekla by remember { mutableStateOf("") }

    LaunchedEffect(proizvodId) {
        if (proizvodId != null) {
            val proizvodi = procitajProizvodeIzJSONa(context, "proizvodi.json")
            val proizvod = proizvodi.find { it.id == proizvodId }
            proizvod?.let {
                naziv = it.naziv
                cena = it.cena.toString()
                kolicina = it.kolicina.toString()
                brzaDostavaDani = it.brzaDostavaDani.toString()
                standardnaDostavaDani = it.standardnaDostavaDani.toString()
                zemljaPorekla = it.zemljaPorekla
            }
        }
    }
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.dodavanjeizmena),
            contentDescription = "Banner za dodavanje i izmenu",
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = naziv,
            onValueChange = { naziv = it },
            label = { Text("Naziv proizvoda") },
            colors = OutlinedTextFieldDefaults.colors(Color.Gray),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cena,
            onValueChange = { cena = it },
            label = { Text("Cena proizvoda") },
            colors = OutlinedTextFieldDefaults.colors(Color.Gray),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = kolicina,
            onValueChange = { kolicina = it },
            label = { Text("Kolicina") },
            colors = OutlinedTextFieldDefaults.colors(Color.Gray),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = standardnaDostavaDani,
            onValueChange = { standardnaDostavaDani = it },
            label = { Text("Dani potrebni za standardnu dostavu") },
            colors = OutlinedTextFieldDefaults.colors(Color.Gray),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = brzaDostavaDani,
            onValueChange = { brzaDostavaDani = it },
            label = { Text("Dani potrebni za brzu dostavu") },
            colors = OutlinedTextFieldDefaults.colors(Color.Gray),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = zemljaPorekla,
            onValueChange = { zemljaPorekla = it },
            label = { Text("Zemlja porekla proizvoda") },
            colors = OutlinedTextFieldDefaults.colors(Color.Gray),
            modifier = Modifier.fillMaxWidth()
            )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val noviProizvod = Proizvod(
                    id = proizvodId ?: System.currentTimeMillis().toInt(),
                    naziv = naziv,
                    cena = cena.toDoubleOrNull() ?: 0.0,
                    kolicina = kolicina.toIntOrNull() ?: 0,
                    brzaDostavaDani = brzaDostavaDani.toIntOrNull() ?: 0,
                    standardnaDostavaDani = standardnaDostavaDani.toIntOrNull() ?: 0,
                    zemljaPorekla = zemljaPorekla
                )

                if (proizvodId != null) {
                    azurirajProizvodeUJSON(context, "proizvodi.json", noviProizvod)
                } else {
                    val proizvodi = procitajProizvodeIzJSONa(context, "proizvodi.json").toMutableList()
                    proizvodi.add(noviProizvod)
                    upisiProizvodeUJSON(context, "proizvodi.json", proizvodi)
                }

                navController.navigateUp()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
        ) {
            Text("Sacuvaj proizvod")
        }
    }
}
