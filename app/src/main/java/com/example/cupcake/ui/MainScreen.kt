package com.example.cupcake

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cupcake.ui.ProizvodDetaljiScreen
import com.example.cupcake.ui.ListaProizvodaScreen
import com.example.cupcake.ui.LogInScreen
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import com.example.cupcake.data.KorpaViewModel
import com.example.cupcake.ui.DodajIzmeniProizvodScreen
import com.example.cupcake.ui.KorpaScreen
import com.example.cupcake.ui.KorpaPotvrdaScreen

@Composable
fun AppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    navigateToProductList: (String) -> Unit,
    navigateToAddProduct: () -> Unit,
    navigateToCart: () -> Unit,
    korisnikJson: String,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color.White,
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button),
                        tint = Color.Black
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { navigateToProductList(korisnikJson) }) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = stringResource(R.string.product_list_button),
                    tint = Color.Black
                )
            }
            IconButton(onClick = navigateToAddProduct) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_product_button),
                    tint = Color.Black
                )
            }
            IconButton(onClick = navigateToCart) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = stringResource(R.string.cart_button),
                    tint = Color.Black
                )
            }
        }
    )
}

@Composable
fun App(
    korpaViewModel: KorpaViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val korisnikJson = navBackStackEntry?.arguments?.getString("korisnikJson") ?: ""

    val canNavigateBack = currentRoute != "login" && currentRoute != "lista_proizvoda"

    Scaffold(
        topBar = {
            if (canNavigateBack) {
                AppBar(
                    canNavigateBack = canNavigateBack,
                    navigateUp = { navController.navigateUp() },
                    navigateToProductList = { korisnikJson -> navController.navigate("lista_proizvoda/$korisnikJson") },
                    navigateToAddProduct = { navController.navigate("dodaj_proizvod/$korisnikJson") },
                    navigateToCart = { navController.navigate("korpa/$korisnikJson") },
                    korisnikJson = korisnikJson
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LogInScreen(navController)
            }
            composable("lista_proizvoda/{korisnikJson}") { backStackEntry ->
                val korisnikJson = backStackEntry.arguments?.getString("korisnikJson") ?: ""
                ListaProizvodaScreen(navController, korisnikJson)
            }
            composable("izmeni_proizvod/{proizvodId}/{korisnikJson}") { backStackEntry ->
                val proizvodId = backStackEntry.arguments?.getString("proizvodId")?.toIntOrNull()
                val korisnikJson = backStackEntry.arguments?.getString("korisnikJson") ?: ""
                DodajIzmeniProizvodScreen(navController, proizvodId, korisnikJson)
            }
            composable("dodaj_proizvod/{korisnikJson}") { backStackEntry ->
                val korisnikJson = backStackEntry.arguments?.getString("korisnikJson") ?: ""
                DodajIzmeniProizvodScreen(navController, korisnikJson = korisnikJson)
            }
            composable("korpa/{korisnikJson}") { backStackEntry ->
                val korisnikJson = backStackEntry.arguments?.getString("korisnikJson") ?: ""
                KorpaScreen(navController, korpaViewModel, korisnikJson)
            }
            composable("proizvod_detalji/{proizvodId}/{korisnikJson}") { backStackEntry ->
                val korisnikJson = backStackEntry.arguments?.getString("korisnikJson") ?: ""
                ProizvodDetaljiScreen(navController, backStackEntry, korpaViewModel)
            }
            composable("potvrda_korpe/{korisnikJson}") { backStackEntry ->
                val korisnikJson = backStackEntry.arguments?.getString("korisnikJson") ?: ""
                KorpaPotvrdaScreen(navController, korpaViewModel, backStackEntry)
            }
        }
    }
}

