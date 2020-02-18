package ru.tohaman.mywearapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import ru.tohaman.mywearapp.R
import ru.tohaman.mywearapp.databinding.ActivityMusicInfoBinding
import ru.tohaman.mywearapp.viewModels.ItemInfoViewModel
import timber.log.Timber


class MusicInfoActivity : AppCompatActivity() {

    private val viewModel by viewModels<ItemInfoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMusicInfoBinding>(this,
            R.layout.activity_music_info
        )

        val bundle :Bundle? = intent.extras
        val id = bundle!!.getInt("id", 10)
//        viewModel.curId = id.toLong()
//
//        Timber.d("Artist - ${viewModel.curId}")
        //Log.d("MWA", "Artist - ${viewModel.currentItem.value}")

//        binding.viewModel = viewModel.currentItem

        //binding.viewMusicItem = (MusicItem(0,"SOME ARTIST","TITLE", 4))

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
//        viewModel.getMusicItem().observe(this, Observer {
//            it?.let {
//                // Update the UI, in this case, a TextView.
//                binding.musicItem = it
//            }
//        })

        //viewModel.setMusicItemById(id.toLong())

    }
}
