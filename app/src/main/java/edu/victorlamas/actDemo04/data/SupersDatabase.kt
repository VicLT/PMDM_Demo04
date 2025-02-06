package edu.victorlamas.actDemo04.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import edu.victorlamas.actDemo04.domain.model.Editorial
import edu.victorlamas.actDemo04.domain.model.SuperHero
import edu.victorlamas.actDemo04.domain.model.SupersWithEditorial
import kotlinx.coroutines.flow.Flow

// Define una base de datos con las tablas SuperHero y Editorial
@Database(entities = [SuperHero::class, Editorial::class], version = 1)
abstract class SupersDatabase : RoomDatabase() {
    abstract fun supersDao(): SupersDao
}

// Data Access Object (DAO) para operaciones en la BD
@Dao
interface SupersDao {
    // Inserta una editorial; reemplaza si ya existe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEditorial(editorial: Editorial)

    // Cruce de tablas SuperHero y Editorial
    @Transaction
    @Query("SELECT * FROM SuperHero ORDER BY superName")
    fun getSuperHerosWithEditorials(): Flow<List<SupersWithEditorial>>

    // Inserta un súper héroe; reemplaza si ya existe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuperHero(superHero: SuperHero)

    // Obtiene un súper héroe con su Editorial por ID
    @Query("SELECT * FROM SuperHero WHERE idSuper = :idSuper")
    suspend fun getSuperById(idSuper: Int): SupersWithEditorial?

    // Obtiene todos los súper héroes como flujo de datos
    @Query("SELECT * FROM SuperHero")
    fun getAllSuperHeros(): Flow<List<SuperHero>>

    // Obtiene todas las editoriales como flujo de datos
    @Query("SELECT * FROM Editorial")
    fun getAllEditorials(): Flow<List<Editorial>>

    // Cuenta el número de editoriales
    @Query("SELECT count(idEd) FROM Editorial")
    fun getNumEditorials(): Flow<Int>

    // Obtiene una editorial por su ID
    @Query("SELECT * FROM Editorial WHERE idEd = :editorialId")
    suspend fun getEditorialById(editorialId: Int): Editorial?

    // Elimina un súper héroe específico
    @Delete
    suspend fun deleteSuperHero(superHero: SuperHero)
}