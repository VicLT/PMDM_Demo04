package edu.victorlamas.actDemo04.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Editorial")
data class Editorial(
    @PrimaryKey(autoGenerate = true)
    val idEd: Int = 0,
    val name: String? = null
)
