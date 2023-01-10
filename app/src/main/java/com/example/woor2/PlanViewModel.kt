package com.example.woor2

import androidx.lifecycle.ViewModel

data class Item1(val title : String, val location : String, val date : String)
class PlanViewModel : ViewModel() {
    val items = ArrayList<Item1>()

    init {
        addItem(Item1("홍대 카페투어", "홍대", "2023-01-10"))
    }

    fun addItem(item: Item1){
        items.add(item)
    }
}