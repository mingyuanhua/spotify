package com.hmy.spotify.ui.playlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hmy.spotify.datamodel.Album
import com.hmy.spotify.datamodel.Song
import com.hmy.spotify.repository.FavoriteAlbumRepository
import com.hmy.spotify.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// PlaylistViewModel能够存在的生命周期跟PlaylistFragment一样长
// 所以每次离开Fragment导致Favorite状态消失的做法是什么？有没有办法扩展lifecycle？
// 我们把lifecycle更改成和Activity一样长就行
// private val playerViewModel: PlayerViewModel by activityViewModels()
// 但是这个行为保留下来有个bug，playerViewModel是和Album绑定的，现在相当于是Global的Album
// 第一个专辑Favorite了，第二个专辑还是Favorite，这样就错了
// 虽然实现了状态的表留，但是Favorite了一个其他section的歌永远也是Favorite，状态变成global的了

// 还有问题，APP离开了数据也没有了，所以这是断电数据未保存，数据在内存上
// 因为我们需要把数据保留下来，一般两种选择：写到local数据库；通过restful api把数据送到data center的后端
// 我们用Room library，背后是SQLite这样的
// 好处：提供了离线模式，更好的用户体验不需要网络，更快，便宜

// 一个view可以对应几个viewmodel？因为页面的状态可以由多个viewmodel来提供


@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val favoriteAlbumRepository: FavoriteAlbumRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        PlaylistUiState(
            Album.empty()
        )
    )
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    fun fetchPlaylist(album: Album) {
        _uiState.value = _uiState.value.copy(album = album)

        viewModelScope.launch {
            val playlist = playlistRepository.getPlaylist(album.id)
            _uiState.value = _uiState.value.copy(playlist = playlist.songs)
            Log.d("PlaylistViewModel", _uiState.value.toString())
        }
        viewModelScope.launch {
            favoriteAlbumRepository.isFavoriteAlbum(album.id).collect{
                _uiState.value = _uiState.value.copy(
                    isFavorite = it
                )
            }
        }
    }

    fun toggleFavorite(isFavorite: Boolean) {
        val album = _uiState.value.album
        viewModelScope.launch {
            if (isFavorite) {
                favoriteAlbumRepository.favoriteAlbum(album)
            } else {
                favoriteAlbumRepository.unFavoriteAlbum(album)
            }
        }
    }

}

data class PlaylistUiState(
    val album: Album,
    val isFavorite: Boolean = false,
    val playlist: List<Song> = emptyList()
)
