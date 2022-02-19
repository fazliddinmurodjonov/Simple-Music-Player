package com.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.androiddatabaselesson6musicplayer.R
import com.example.androiddatabaselesson6musicplayer.databinding.ItemMusicBinding
import com.model.Music
import com.model.formatDuration

class MusicAdapter(var context: Context, var musicList: ArrayList<Music>) :
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {
    lateinit var adapterListener: OnItemClickListener

    interface OnItemClickListener {
        fun onclick(music: Music, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        adapterListener = listener
    }

    inner class MusicViewHolder(val binding: ItemMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(music: Music, position: Int) {
            binding.musicArtist.text = music.artist
            binding.musicName.text = music.title
           // binding.duration.text = formatDuration(musicList[position].duration)
            binding.root.setOnClickListener {
                adapterListener.onclick(music, position)
            }
            Glide.with(context)
                .load(musicList[position].artUri)
                .apply(RequestOptions().placeholder(R.drawable.img).centerCrop())
                .into(binding.musicImage)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(ItemMusicBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val music = musicList[position]
        holder.onBind(music, position)
    }

    override fun getItemCount(): Int = musicList.size
}