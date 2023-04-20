package com.example.woor2

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

data class Item4(var location : String, val latitude : Double, val longitude : Double) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?:"",
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(location)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item4> {
        override fun createFromParcel(parcel: Parcel): Item4 {
            return Item4(parcel)
        }

        override fun newArray(size: Int): Array<Item4?> {
            return arrayOfNulls(size)
        }
    }
}
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