package com.example.woor2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Item4(var location : String, val latitude : Double, val longitude : Double)
class AddPlanViewModel : ViewModel() {
    val itemsListData = MutableLiveData<ArrayList<Item4>>()
    val items = ArrayList<Item4>()

    val itemClickEvent = MutableLiveData<Int>()
    var itemLongClick = -1

    fun addItem(item: Item4){
        items.add(item)
        itemsListData.value = items
    }

    fun updateItem(pos : Int, item : Item4){
        items[pos] = item
        itemsListData.value = items
    }

    fun deleteItem(pos : Int){
        items.removeAt(pos)
        itemsListData.value = items
    }
}