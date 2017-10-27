/*
 * Copyright (C) 2015 Antonio Leiva
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.antonioleiva.bandhookkotlin.ui.presenter

import com.antonioleiva.bandhookkotlin.domain.interactor.GetArtistDetailInteractor
import com.antonioleiva.bandhookkotlin.domain.interactor.GetTopAlbumsInteractor
import com.antonioleiva.bandhookkotlin.domain.interactor.base.InteractorExecutor
import com.antonioleiva.bandhookkotlin.domain.interactor.event.ArtistDetailEvent
import com.antonioleiva.bandhookkotlin.domain.interactor.event.TopAlbumsEvent
import com.antonioleiva.bandhookkotlin.ui.entity.ImageTitle
import com.antonioleiva.bandhookkotlin.ui.entity.mapper.ArtistDetailDataMapper
import com.antonioleiva.bandhookkotlin.ui.entity.mapper.ImageTitleDataMapper
import com.antonioleiva.bandhookkotlin.ui.view.ArtistView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

open class ArtistPresenter(
        private val view: ArtistView,
        private val artistDetailInteractor: GetArtistDetailInteractor,
        private val topAlbumsInteractor: GetTopAlbumsInteractor,
        private val artistDetailMapper: ArtistDetailDataMapper,
        private val albumsMapper: ImageTitleDataMapper,
        private val interactorExecutor: InteractorExecutor) : AlbumsPresenter {

    open fun init(artistId: String) {
        val artistDetailInteractor = artistDetailInteractor
        artistDetailInteractor.id = artistId
        val resultArtist = interactorExecutor.execute(artistDetailInteractor.getFun())
        launch(UI) { onEvent(resultArtist.await() as ArtistDetailEvent) }

        val topAlbumsInteractor = topAlbumsInteractor
        topAlbumsInteractor.artistId = artistId
        val resultAlbum = interactorExecutor.execute(this.topAlbumsInteractor.getFun())
        launch(UI) { onEvent(resultAlbum.await() as TopAlbumsEvent) }
    }

    fun onEvent(event: ArtistDetailEvent) {
        view.showArtist(artistDetailMapper.transform(event.artist))
    }

    fun onEvent(event: TopAlbumsEvent) {
        view.showAlbums(albumsMapper.transformAlbums(event.topAlbums))
    }

    override fun onAlbumClicked(item: ImageTitle) {
        view.navigateToAlbum(item.id)
    }
}