package com.example.woor2

import androidx.lifecycle.ViewModel

data class Item(val name : String, val phone : String)
class PlanViewModel : ViewModel() {
    val items = ArrayList<Item>()

    init {
        addItem(Item("hello", "1234"))
    }

    fun addItem(item: Item){
        items.add(item)
    }
}