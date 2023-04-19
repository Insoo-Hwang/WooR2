package com.example.woor2

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

data class Item4(var location : String, val latitude : Double, val longitude : Double)
class AddPlanViewModel(application: Application) : AndroidViewModel(application) {
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

    fun getLat(pos : Int): Double {
        return items[pos].latitude
    }

    fun getLon(pos : Int): Double {
        return items[pos].longitude
    }

    fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        getApplication<Application>().startActivity(intent)
    }
}