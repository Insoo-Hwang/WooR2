package com.example.woor2

import androidx.lifecycle.ViewModel

data class Item4(val location : String)
class AddPlanViewModel : ViewModel() {
    val items = ArrayList<Item4>()

    init {
        addItem(Item4(""))
    }

    fun addItem(item: Item4){
        items.add(item)
    }
}