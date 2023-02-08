package com.example.woor2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Item1(val title : String, val date : String)
class PlanViewModel : ViewModel() {
    val itemsListData = MutableLiveData<ArrayList<Item1>>()
    val items = ArrayList<Item1>()

    val itemClickEvent = MutableLiveData<Int>()
    var itemLongClick = -1

    init {
        updateList()
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

    fun updateList(){
        val db = Firebase.firestore
        db.collection("schedules").get().addOnSuccessListener {
            for(doc in it){
                addItem(Item1(doc["title"].toString(), doc["date"].toString()))
            }
        }
    }
}