package com.example.cupcake.utils


import android.content.Context
import com.example.cupcake.data.Proizvod
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException
import java.io.InputStream


fun procitajProizvodeIzJSONa(context: Context, fileName: String): List<Proizvod> {
    val file = File(context.filesDir, fileName)
    if (!file.exists()) {
        return emptyList()
    }
    val json = file.readText()
    val type = object : TypeToken<List<Proizvod>>() {}.type
    return Gson().fromJson(json, type)
}

fun upisiProizvodeUJSON(context: Context, fileName: String, products: List<Proizvod>) {
    val file = File(context.filesDir, fileName)
    val json = Gson().toJson(products)
    file.writeText(json)
}

fun obrisiProizvodeIzFajla(context: Context, fileName: String, productId: Int) {
    val products = procitajProizvodeIzJSONa(context, fileName).toMutableList()
    val productToRemove = products.find { it.id == productId }
    if (productToRemove != null) {
        products.remove(productToRemove)
        upisiProizvodeUJSON(context, fileName, products)
    }
}
fun azurirajProizvodeUJSON(context: Context, fileName: String, updatedProizvod: Proizvod) {
    val products = procitajProizvodeIzJSONa(context, fileName).toMutableList()
    val productIndex = products.indexOfFirst { it.id == updatedProizvod.id }
    if (productIndex != -1) {
        products[productIndex] = updatedProizvod
        upisiProizvodeUJSON(context, fileName, products)
    }
}
fun kopirajJSONizAssets(context: Context, fileName: String) {
    val file = File(context.filesDir, fileName)
    if (file.exists()) return

    try {
        val inputStream: InputStream = context.assets.open(fileName)
        val outputStream = file.outputStream()

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

