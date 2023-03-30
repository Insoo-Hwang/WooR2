package com.example.woor2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Item3(val title : String, val id : String)
class SearchViewModel : ViewModel() {
    val items = ArrayList<Item3>()
    val itemsListData = MutableLiveData<ArrayList<Item3>>()

    val itemClickEvent = MutableLiveData<Int>()
    var itemLongClick = -1

    fun addItem(item: Item3){
        items.add(item)
    }

    fun updateItem(pos : Int, item : Item3){
        items[pos] = item
        itemsListData.value = items
    }

    fun deleteItem(pos : Int){
        items.removeAt(pos)
        itemsListData.value = items
    }

    fun deleteAll(){
        items.clear()
    }
}