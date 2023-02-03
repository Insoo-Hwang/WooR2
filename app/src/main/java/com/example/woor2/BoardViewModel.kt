package com.example.woor2

import androidx.lifecycle.ViewModel

data class Item2(val title : String, val writer : String, val date : String)
class BoardViewModel : ViewModel() {
    val items = ArrayList<Item2>()

    init {
        addItem(Item2("카페투어 추천", "안뇽하세요", "2023-01-10"))
    }

    fun addItem(item: Item2){
        items.add(item)
    }
}