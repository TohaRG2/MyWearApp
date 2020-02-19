package ru.tohaman.mywearapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.tohaman.mywearapp.R
import ru.tohaman.mywearapp.data.MusicItem
import ru.tohaman.mywearapp.databinding.ActivityMusicInfoBinding
import ru.tohaman.mywearapp.viewModels.ItemInfoViewModel
import timber.log.Timber


class MusicInfoActivity : AppCompatActivity() {

    private lateinit var  viewModel : ItemInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMusicInfoBinding>(this,
            R.layout.activity_music_info
        )

        val bundle :Bundle? = intent.extras
        val id = bundle!!.getInt("id", 0)

        viewModel = ViewModelProvider(this, ItemInfoViewModel.Companion.ItemInfoViewModelFactory(application, id)).get(ItemInfoViewModel::class.java)

        Timber.d("Artist - ${viewModel.curId}")

        val dataMusicItem : LiveData<MusicItem?> = viewModel.currentItem
        dataMusicItem.observe(this, Observer { binding.musicItem = it })

    }
}
