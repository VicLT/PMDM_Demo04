package edu.victorlamas.actDemo04.domain

import edu.victorlamas.actDemo04.data.SupersRepository
import edu.victorlamas.actDemo04.domain.model.SuperHero

// Caso de uso para eliminar un superhéroe
class DeleteSuperUseCase(private val supersRepository: SupersRepository) {

    // Sobrecarga del operador invoke para ejecutar la eliminación
    suspend operator fun invoke(superHero: SuperHero) {
        supersRepository.deleteSuperHero(superHero)
    }
}