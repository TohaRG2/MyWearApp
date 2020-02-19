package ru.tohaman.mywearapp.viewModels

import android.app.Application
import androidx.lifecycle.*
import ru.tohaman.mywearapp.data.MusicItem
import ru.tohaman.mywearapp.dataSource.repository
import ru.tohaman.mywearapp.toMutableLiveData
import ru.tohaman.mywearapp.dataSource.Result

class ItemInfoViewModel(app: Application, id: Int) : AndroidViewModel(app){
    //private val dao = MusicDB.get(app).musicItemDao()

    var curId = id.toMutableLiveData()

    private val _musicItem = curId.switchMap {
        repository.observeMusicItem(it).map {result ->
            getResult(result)
        }
    }

    private fun getResult(taskResult: Result<MusicItem>) :MusicItem? {
        return if (taskResult is Result.Success) {
            taskResult.data
        } else {
            null
        }
    }

    var currentItem  = _musicItem

    companion object {
        class ItemInfoViewModelFactory (val app: Application, val id: Int) : ViewModelProvider.AndroidViewModelFactory(app) {

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ItemInfoViewModel(app, id) as T
            }
        }
    }

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