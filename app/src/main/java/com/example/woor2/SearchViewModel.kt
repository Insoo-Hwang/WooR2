package com.example.woor2

import androidx.lifecycle.ViewModel

data class Item3(val name : String, val phone : String)
class SearchViewModel : ViewModel() {
    val items = ArrayList<Item3>()

    init {
        addItem(Item3("hello", "1234"))
    }

    fun addItem(item: Item3){
        items.add(item)
    }
}