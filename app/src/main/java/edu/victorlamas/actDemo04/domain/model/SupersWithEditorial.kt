package edu.victorlamas.actDemo04.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class SupersWithEditorial(
    @Embedded val superHero: SuperHero,
    @Relation(
        parentColumn = "idEditorial",
        entityColumn = "idEd"
    ) val editorial: Editorial
)