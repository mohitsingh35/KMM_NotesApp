package com.ncs.notesapp.domain.note

import com.ncs.notesapp.presentation.BabyBlueHex
import com.ncs.notesapp.presentation.LightGreenHex
import com.ncs.notesapp.presentation.RedOrangeHex
import com.ncs.notesapp.presentation.RedPinkHex
import com.ncs.notesapp.presentation.VioletHex
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id:Long?,
    val title:String,
    val content:String,
    val colorHex:Long,
    val created:LocalDateTime
){
    companion object{
        private val colors= listOf(RedOrangeHex, RedPinkHex, LightGreenHex, BabyBlueHex, VioletHex)

        fun generateRandomColor()= colors.random()
    }
}
