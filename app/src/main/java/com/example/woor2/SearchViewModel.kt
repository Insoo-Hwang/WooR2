package com.example.woor2

import androidx.lifecycle.ViewModel

data class Item3(val title : String, val location : String)
class SearchViewModel : ViewModel() {
    val items = ArrayList<Item3>()

    init {
        addItem(Item3("을지로 카페투어", "을지로"))
    }

    fun addItem(item: Item3){
        items.add(item)
    }
}