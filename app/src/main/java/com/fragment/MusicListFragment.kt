package com.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.adapter.MusicAdapter
import com.example.androiddatabaselesson6musicplayer.R
import com.example.androiddatabaselesson6musicplayer.databinding.FragmentMusicListBinding
import com.model.Music

import java.io.File
import kotlin.reflect.KTypeProjection.Companion.STAR

class MusicListFragment : Fragment(R.layout.fragment_music_list) {
    private val binding: FragmentMusicListBinding by viewBinding()
    lateinit var musicAdapter: MusicAdapter

    companion object {
        lateinit var musicList: ArrayList<Music>
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadRV()
    }

    fun loadRV() {
        musicList = getAllMusic()
        musicAdapter = MusicAdapter(requireContext(), musicList)
        binding.musicRV.adapter = musicAdapter
        musicAdapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener {
            @SuppressLint("ResourceType")
            override fun onclick(music: Music, position: Int) {
                val bundleOf = bundleOf("index" to position, "class" to "MusicAdapter")
                findNavController().navigate(R.id.playMusicFragment, bundleOf)
            }

        })
    }

    @SuppressLint("Range")
    private fun getAllMusic(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor =
            requireActivity().contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                MediaStore.Audio.Media.DATE_ADDED + " DESC",
                null)
        if (cursor != null)
            if (cursor.moveToFirst())
                do {
                    val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val title =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val album =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artist =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val duration =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumID =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse("content://media//external/audio/albumart")
                    val uriArt = Uri.withAppendedPath(uri, albumID).toString()
                    val music = Music(id, title, album, artist, duration, path, uriArt)
                    val file = File(music.path)
                    if (file.exists())
                        tempList.add(music)
                } while (cursor.moveToNext())
        cursor?.close()
        return tempList
    }

    override fun onResume() {
        super.onResume()
        loadRV()
    }

}