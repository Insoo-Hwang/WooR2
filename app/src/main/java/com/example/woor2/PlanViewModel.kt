package com.example.woor2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
data class Item1(val title : String, val date : String)
class PlanViewModel : ViewModel() {
    val itemsListData = MutableLiveData<ArrayList<Item1>>()
    val items = ArrayList<Item1>()

    val itemClickEvent = MutableLiveData<Int>()
    var itemLongClick = -1

    init {
        addItem(Item1("홍대 카페투어", "2023-01-10"))
    }

    fun addItem(item: Item1){
        items.add(item)
        itemsListData.value = items
    }

    fun updateItem(pos : Int, item : Item1){
        items[pos] = item
        itemsListData.value = items
    }

    fun deleteItem(pos : Int){
        items.removeAt(pos)
        itemsListData.value = items
    }
}