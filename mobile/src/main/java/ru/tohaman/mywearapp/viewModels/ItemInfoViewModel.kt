package ru.tohaman.mywearapp.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import ru.tohaman.mywearapp.data.MusicDB
import androidx.paging.Config
import androidx.paging.toLiveData
import ru.tohaman.mywearapp.data.MusicItem
import ru.tohaman.mywearapp.dataSource.repository
import ru.tohaman.mywearapp.ioThread

class ItemInfoViewModel(app: Application) : AndroidViewModel(app){
    //private val dao = MusicDB.get(app).musicItemDao()

    var curId : Int = 0

    private var currentItem  = repository.observeMusicItem(curId)

//    init {
//        currentItem =
//    }
//
//    fun setMusicItemById (id: Long) {
//         currentItem.value = dao.getById(id).value
//    }

    fun getMusicItem() = currentItem

}

/**
 *     private val _petDetails = _petId.switchMap {
petsRepository.observePet(it).map { result ->
getResult(result)
}
}

val petDetails = _petDetails

private fun getResult(taskResult: Result<PetEntity>): PetEntity? {
return if (taskResult is Result.Success) {
taskResult.data
} else {
showSnackbarMessage(R.string.loading_pet_error)
null
}
}
 */