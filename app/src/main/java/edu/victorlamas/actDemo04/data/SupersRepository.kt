package edu.victorlamas.actDemo04.data

import edu.victorlamas.actDemo04.domain.model.Editorial
import edu.victorlamas.actDemo04.domain.model.SuperHero
import edu.victorlamas.actDemo04.domain.model.SupersWithEditorial

// Repositorio que actúa como intermediario con la fuente de datos
class SupersRepository(private val dataSource: SupersDataSource) {
    val allSuperHeros = dataSource.allSuperHeros
    val allSupersWithEditorials = dataSource.allSupersWithEditorials
    val allEditorials = dataSource.allEditorials
    val numEditorials = dataSource.numEditorials

    // Funciones CRUD
    suspend fun insertEditorial(editorial: Editorial) {
        dataSource.insertEditorial(editorial)
    }

    suspend fun insertSuperHero(superHero: SuperHero) {
        dataSource.insertSuperHero(superHero)
    }

    suspend fun getSuperById(idSuper: Int): SupersWithEditorial? {
        return dataSource.getSuperById(idSuper)
    }

    suspend fun getEditorialById(editorialId: Int) =
        dataSource.getEditorialById(editorialId)

    suspend fun deleteSuperHero(superHero: SuperHero) {
        dataSource.deleteSuperHero(superHero)
    }
}