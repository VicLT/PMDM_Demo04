package edu.victorlamas.actDemo04.data

import edu.victorlamas.actDemo04.domain.model.Editorial
import edu.victorlamas.actDemo04.domain.model.SuperHero
import edu.victorlamas.actDemo04.domain.model.SupersWithEditorial
import kotlinx.coroutines.flow.Flow

// Fuente de datos que conecta con el DAO
class SupersDataSource(private val db: SupersDao) {
    val allSuperHeros: Flow<List<SuperHero>> = db.getAllSuperHeros()
    val allSupersWithEditorials: Flow<List<SupersWithEditorial>> =
        db.getSuperHerosWithEditorials()
    val allEditorials: Flow<List<Editorial>> = db.getAllEditorials()
    val numEditorials: Flow<Int> = db.getNumEditorials()

    // Funciones CRUD
    suspend fun insertEditorial(editorial: Editorial) {
        db.insertEditorial(editorial)
    }

    suspend fun insertSuperHero(superHero: SuperHero) {
        db.insertSuperHero(superHero)
    }

    suspend fun getSuperById(idSuper: Int): SupersWithEditorial? {
        return db.getSuperById(idSuper)
    }

    suspend fun getEditorialById(editorialId: Int) =
        db.getEditorialById(editorialId)

    suspend fun deleteSuperHero(superHero: SuperHero) {
        db.deleteSuperHero(superHero)
    }
}