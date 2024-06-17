package com.example.cupcake.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cupcake.data.Proizvod
import com.example.cupcake.data.Korisnik
import com.example.cupcake.R
import com.example.cupcake.utils.procitajProizvodeIzJSONa
import com.google.gson.Gson

@Composable
fun ListaProizvodaScreen(navController: NavController, korisnikJson: String) {
    val korisnik = Gson().fromJson(korisnikJson, Korisnik::class.java)
    val context = LocalContext.current
    var proizvodi by remember { mutableStateOf<List<Proizvod>>(emptyList()) }

    LaunchedEffect(Unit) {
        proizvodi = procitajProizvodeIzJSONa(context, "proizvodi.json")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.sviproizvodi),
            contentDescription = "Banner za proizvode",
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(proizvodi.size) { index ->
                val proizvod = proizvodi[index]
                ListaProizvodaElement(proizvod, onClick = {
                    navController.navigate("proizvod_detalji/${proizvod.id}/${korisnikJson}")
                })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ListaProizvodaElement(proizvod: Proizvod, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (proizvod.kolicina > 0) Color.LightGray else Color.DarkGray
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = proizvod.naziv,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        color = if (proizvod.kolicina > 0) Color.Black else Color.White
                    )
                )
                Text(
                    text = "Cena: ${proizvod.cena}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = if (proizvod.kolicina > 0) Color.Black else Color.White
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            if (proizvod.kolicina > 0) Color.Green else Color.Red,
                            shape = MaterialTheme.shapes.small
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (proizvod.kolicina > 0) "Na stanju" else "Nema na stanju",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = if (proizvod.kolicina > 0) Color.Black else Color.White
                    )
                )
            }
        }
    }
}

