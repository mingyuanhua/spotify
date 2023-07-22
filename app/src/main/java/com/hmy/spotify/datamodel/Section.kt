package com.hmy.spotify.datamodel

import com.google.gson.annotations.SerializedName

// SerializedName --> section_title最为JSON的Key
data class Section(
    @SerializedName("section_title")
    val sectionTitle: String,
    val albums: List<Album>
)
