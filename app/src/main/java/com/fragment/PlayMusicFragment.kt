package com.fragment

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context.AUDIO_SERVICE
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.androiddatabaselesson6musicplayer.R
import com.example.androiddatabaselesson6musicplayer.databinding.FragmentPlayMusicBinding
import com.model.Music
import com.model.formatDuration


class PlayMusicFragment : Fragment(R.layout.fragment_play_music), MediaPlayer.OnCompletionListener {

    val binding: FragmentPlayMusicBinding by viewBinding()
    lateinit var handler: Handler
    lateinit var audioManager: AudioManager

    companion object {
        lateinit var musicList: ArrayList<Music>
        var songPosition: Int = 0
        var mediaPlayer: MediaPlayer? = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding)
        {
            handler = Handler(Looper.getMainLooper())

            songPosition = arguments?.getInt("index")!!
            when (arguments?.getString("class")) {
                "MusicAdapter" -> {
                    musicList = ArrayList()
                    musicList.addAll(MusicListFragment.musicList)
                    createMediaPlayer()
                }
            }
            playPause.setOnClickListener {
                if (mediaPlayer?.isPlaying!!) {
                    mediaPlayer?.pause()
                    playPause.setImageResource(R.drawable.play)
                } else {
                    mediaPlayer?.start()
                    playPause.setImageResource(R.drawable.pause)
                }
            }

            forward.setOnLongClickListener {
                mediaPlayer?.seekTo(mediaPlayer?.currentPosition?.plus(3000)!!)
                true
            }
            previous.setOnLongClickListener {
                mediaPlayer?.seekTo(mediaPlayer?.currentPosition?.minus(3000)!!)
                true
            }
            setLayout()

            back.setOnClickListener {
                findNavController().popBackStack()
            }
            previous.setOnClickListener {
                prevNextSong(false)
            }
            forward.setOnClickListener {
                prevNextSong(true)
            }
            seekBarMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    if (fromUser) {
                        mediaPlayer?.seekTo(progress)
                    }

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            })
            audioManager = requireActivity().getSystemService(AUDIO_SERVICE) as AudioManager

            var maxVolume: Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            var currentVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            seekBarVolume.max = maxVolume
            seekBarVolume.setProgress(currentVolume)

            seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            })
        }


    }

    private var runnable = object : Runnable {
        override fun run() {
            binding.startTime.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            binding.seekBarMusic.progress = mediaPlayer?.currentPosition!!
            handler?.postDelayed(this, 200)
        }

    }

    private fun createMediaPlayer() {
        try {

            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
                mediaPlayer!!.reset()
                mediaPlayer!!.setDataSource(musicList[songPosition].path)
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
                binding.seekBarMusic.max = mediaPlayer!!.duration
                handler.postDelayed(runnable, 0)
                mediaPlayer!!.setOnCompletionListener(this)
            }
        } catch (e: Exception) {
            return
        }

    }

    private fun releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.release()
                mediaPlayer = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setLayout() {
        val music: Music = musicList[songPosition]
        Glide.with(requireActivity())
            .load(music.artUri)
            .apply(RequestOptions().placeholder(R.drawable.img).centerCrop())
            .into(binding.musicPhoto)
        binding.songTitle.text = music.title
        binding.artistAndAlbum.text = "${music.artist} · ${music.album}"
        binding.endTime.text = formatDuration(music.duration)
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            //handler.removeCallbacks(runnable)
            mediaPlayer?.stop()
            mediaPlayer = null
            binding.startTime.text = "00:00"
            setSongPosition(true)
            setLayout()
            createMediaPlayer()
        } else {
            // handler.removeCallbacks(runnable)
            mediaPlayer?.stop()
            mediaPlayer = null
            binding.startTime.text = "00:00"
            setSongPosition(false)
            setLayout()
            createMediaPlayer()

        }
    }

    private fun setSongPosition(increment: Boolean) {
        if (increment) {
            if (musicList.size - 1 == songPosition) {
                songPosition = 0
            } else {
                ++songPosition
            }
        } else {
            if (songPosition == 0) {
                songPosition = musicList.size - 1
            } else {
                --songPosition
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.startTime.text = "00:00"
        binding.seekBarMusic.progress = 0
        releaseMP()
        handler.removeCallbacks(runnable)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        mediaPlayer?.stop()
        mediaPlayer = null
        binding.startTime.text = "00:00"
        setSongPosition(true)
        setLayout()
        createMediaPlayer()

    }


}