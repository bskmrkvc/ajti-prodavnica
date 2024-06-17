package com.example.cupcake.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cupcake.utils.upisiProizvodeUJSON
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class StavkaKorpe(val proizvod: Proizvod, val kolicina: Int)

class KorpaViewModel : ViewModel() {
    private val _korpaStavke = MutableStateFlow<List<StavkaKorpe>>(emptyList())
    val korpaStavke: StateFlow<List<StavkaKorpe>> get() = _korpaStavke

    private val _proizvodi = MutableStateFlow<List<Proizvod>>(emptyList())
    val proizvodi: StateFlow<List<Proizvod>> get() = _proizvodi

    fun setProizvodi(proizvodi: List<Proizvod>) {
        _proizvodi.value = proizvodi
    }

    fun dodajUKorpu(proizvod: Proizvod) {
        viewModelScope.launch {
            val azuriranaKorpa = _korpaStavke.value.toMutableList()
            val postojecaStavka = azuriranaKorpa.find { it.proizvod.id == proizvod.id }
            if (postojecaStavka != null) {
                val azuriranaStavka = postojecaStavka.copy(kolicina = postojecaStavka.kolicina + 1)
                azuriranaKorpa[azuriranaKorpa.indexOf(postojecaStavka)] = azuriranaStavka
            } else {
                azuriranaKorpa.add(StavkaKorpe(proizvod, 1))
            }
            _korpaStavke.value = azuriranaKorpa

            val azuriraniProizvodi = _proizvodi.value.toMutableList()
            val proizvodIndex = azuriraniProizvodi.indexOfFirst { it.id == proizvod.id }
            if (proizvodIndex != -1) {
                val azuriraniProizvod = azuriraniProizvodi[proizvodIndex].copy(kolicina = proizvod.kolicina - 1)
                azuriraniProizvodi[proizvodIndex] = azuriraniProizvod
                _proizvodi.value = azuriraniProizvodi
            }
        }
    }

    fun ukloniIzKorpe(context: Context, proizvodId: Int) {
        viewModelScope.launch {
            val azuriranaKorpa = _korpaStavke.value.toMutableList()
            val stavkaZaUkloniti = azuriranaKorpa.find { it.proizvod.id == proizvodId }
            if (stavkaZaUkloniti != null) {
                azuriranaKorpa.remove(stavkaZaUkloniti)
                _korpaStavke.value = azuriranaKorpa

                val azuriraniProizvodi = _proizvodi.value.toMutableList()
                val proizvodIndex = azuriraniProizvodi.indexOfFirst { it.id == proizvodId }
                if (proizvodIndex != -1) {
                    val azuriraniProizvod = azuriraniProizvodi[proizvodIndex].copy(kolicina = azuriraniProizvodi[proizvodIndex].kolicina + stavkaZaUkloniti.kolicina)
                    azuriraniProizvodi[proizvodIndex] = azuriraniProizvod
                    _proizvodi.value = azuriraniProizvodi

                    upisiProizvodeUJSON(context, "proizvodi.json", azuriraniProizvodi)
                }
            }
        }
    }

    fun ocistiKorpu() {
        _korpaStavke.value = emptyList()
    }
}
