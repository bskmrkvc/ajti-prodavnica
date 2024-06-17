package com.example.cupcake.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cupcake.R
import com.example.cupcake.data.Korisnik
import com.google.gson.Gson

@Composable
fun LogInScreen(navController: NavController) {
    var ime by remember { mutableStateOf(TextFieldValue()) }
    var prezime by remember { mutableStateOf(TextFieldValue()) }
    var email by remember { mutableStateOf(TextFieldValue()) }
    var adresa by remember { mutableStateOf(TextFieldValue()) }
    var zemlja by remember { mutableStateOf(TextFieldValue()) }

    var validnoIme by remember { mutableStateOf(true) }
    var validnoPrezime by remember { mutableStateOf(true) }
    var validanEmail by remember { mutableStateOf(true) }
    var validnaAdresa by remember { mutableStateOf(true) }
    var validnaZemlja by remember { mutableStateOf(true) }

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validateFields() {
        validnoIme = ime.text.isNotBlank()
        validnoPrezime = prezime.text.isNotBlank()
        validanEmail = email.text.isNotBlank() && validateEmail(email.text)
        validnaAdresa = adresa.text.isNotBlank()
        validnaZemlja = zemlja.text.isNotBlank()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(200.dp)
            )

            OutlinedTextField(
                value = ime,
                onValueChange = { ime = it },
                label = { Text("Ime") },
                isError = !validnoIme,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (validnoIme) Color.Gray else Color.Red,
                    unfocusedBorderColor = if (validnoIme) Color.Gray else Color.Red,
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (!validnoIme) {
                Text("Polje ne sme biti prazno", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = prezime,
                onValueChange = { prezime = it },
                label = { Text("Prezime") },
                isError = !validnoPrezime,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (validnoPrezime) Color.Gray else Color.Red,
                    unfocusedBorderColor = if (validnoPrezime) Color.Gray else Color.Red,
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (!validnoPrezime) {
                Text("Polje ne sme biti prazno", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                isError = !validanEmail,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (validanEmail) Color.Gray else Color.Red,
                    unfocusedBorderColor = if (validanEmail) Color.Gray else Color.Red,
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (!validanEmail) {
                Text("Unesite validan email", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = adresa,
                onValueChange = { adresa = it },
                label = { Text("Ulica i broj") },
                isError = !validnaAdresa,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (validnaAdresa) Color.Gray else Color.Red,
                    unfocusedBorderColor = if (validnaAdresa) Color.Gray else Color.Red,
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (!validnaAdresa) {
                Text("Polje ne sme biti prazno", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = zemlja,
                onValueChange = { zemlja = it },
                label = { Text("Država") },
                isError = !validnaZemlja,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (validnaZemlja) Color.Gray else Color.Red,
                    unfocusedBorderColor = if (validnaZemlja) Color.Gray else Color.Red,
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (!validnaZemlja) {
                Text("Polje ne sme biti prazno", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    validateFields()
                    if (validnoIme && validnoPrezime && validanEmail && validnaAdresa && validnaZemlja) {
                        val korisnik = Korisnik(
                            ime = ime.text,
                            prezime = prezime.text,
                            email = email.text,
                            adresa = adresa.text,
                            zemlja = zemlja.text
                        )
                        val korisnikJson = Gson().toJson(korisnik)
                        navController.navigate("lista_proizvoda/$korisnikJson")
                    }
                }
            ) {
                Text("Uloguj se")
            }
        }
    }
}
