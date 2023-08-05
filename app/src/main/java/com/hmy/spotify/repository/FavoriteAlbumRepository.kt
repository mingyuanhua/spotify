package com.hmy.spotify.repository

import com.hmy.spotify.database.DatabaseDao
import com.hmy.spotify.datamodel.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteAlbumRepository @Inject constructor(private val databaseDao: DatabaseDao) {

    // Flow是一个长的流的数据 不断 受限于coroutine lifecycle是viewmodel scope
    // 同一个view model里就不会断 代表了对于这个database 但凡数据库有任何变化 都会有个输出

    // 这里因为是flow，只有我们真正获取流的时候才需要挂起。创建flow的时候不需要被挂起，只有执行get的时候才需要被挂起
    // 哪一个点开始真正开始收集数据？favoriteAlbumRepository.isFavoriteAlbum(album.id).collect
    // .collect函数真正开始收集数据 里面就要suspend
    fun isFavoriteAlbum(id: Int): Flow<Boolean> =
        databaseDao.isFavoriteAlbum(id).flowOn(Dispatchers.IO)

    // suspend代表这部分操作要被挂起 挂到其他thread上
    suspend fun favoriteAlbum(album: Album) = withContext(Dispatchers.IO) {
        databaseDao.favoriteAlbum(album)
    }

    suspend fun unFavoriteAlbum(album: Album) = withContext(Dispatchers.IO) {
        databaseDao.unFavoriteAlbum(album)
    }

    fun fetchFavoriteAlbums(): Flow<List<Album>> =
        databaseDao.fetchFavoriteAlbums().flowOn(Dispatchers.IO)
}