package com.example.woor2

import androidx.lifecycle.ViewModel

data class Item2(val name : String, val phone : String)
class BoardViewModel : ViewModel() {
    val items = ArrayList<Item2>()

    init {
        addItem(Item2("hello", "1234"))
    }

    fun addItem(item: Item2){
        items.add(item)
    }
}