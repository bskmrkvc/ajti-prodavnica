package com.example.cupcake.data

data class Proizvod(
    val id: Int,
    val naziv: String,
    val cena: Double,
    val kolicina: Int,
    val brzaDostavaDani: Int,
    val standardnaDostavaDani: Int,
    val zemljaPorekla: String
)
