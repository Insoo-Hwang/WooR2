package com.example.woor2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Item2(val title : String, val writer : String, val id : String)
class BoardViewModel : ViewModel() {
    val itemsListData = MutableLiveData<ArrayList<Item2>>()
    val items = ArrayList<Item2>()

    val itemClickEvent = MutableLiveData<Int>()
    var itemLongClick = -1

    init {
        updateList()
    }

    fun addItem(item: Item2){
        items.add(item)
    }

    fun updateItem(pos : Int, item : Item2){
        items[pos] = item
        itemsListData.value = items
    }

    fun deleteItem(pos : Int){
        items.removeAt(pos)
        itemsListData.value = items
    }

    fun updateList(){
        val db = Firebase.firestore
        db.collection("boards").get().addOnSuccessListener {
            for(doc in it){
                addItem(Item2(doc["title"].toString(), doc["user"].toString(), doc.id))
            }
        }
    }
}