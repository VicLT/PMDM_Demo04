package edu.victorlamas.actDemo04.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.victorlamas.actDemo04.data.SupersRepository
import edu.victorlamas.actDemo04.domain.DeleteSuperUseCase
import edu.victorlamas.actDemo04.domain.model.Editorial
import edu.victorlamas.actDemo04.domain.model.SupersWithEditorial
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// ViewModel principal que gestiona las operaciones y el estado de los datos en la UI
class MainViewModel(
    private val supersRepository: SupersRepository,
    private var deleteSuperUseCase: DeleteSuperUseCase
) : ViewModel() {

    val numEditorials: Flow<Int> = supersRepository.numEditorials
    val currentSuperHero: Flow<List<SupersWithEditorial>> =
        supersRepository.allSupersWithEditorials

    // Inicializa el caso de uso para eliminar un súper héroe usando el repositorio
    init {
        deleteSuperUseCase = DeleteSuperUseCase(supersRepository)
    }

    // Guarda una nueva editorial
    fun saveEditorial(name: String) {
        val editorial = Editorial(0, name)
        viewModelScope.launch {
            supersRepository.insertEditorial(editorial)
        }
    }

    // Actualiza el estado de favorito de un súper héroe
    fun updateFavorite(supersWithEditorial: SupersWithEditorial) {
        viewModelScope.launch {
            val superAux = supersWithEditorial.superHero.copy(
                favorite = if (supersWithEditorial.superHero.favorite == 1) 0 else 1
            )
            supersRepository.insertSuperHero(superAux)
        }
    }

    // Elimina un súper héroe usando el caso de uso
    fun deleteSuper(supersWithEditorial: SupersWithEditorial) {
        viewModelScope.launch {
            deleteSuperUseCase.invoke(superHero = supersWithEditorial.superHero)
        }
    }
}

// Factory para crear instancias de MainViewModel con los parámetros necesarios
@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val supersRepository: SupersRepository,
    private val deleteSuperUseCase: DeleteSuperUseCase
) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(supersRepository, deleteSuperUseCase) as T
        }
    }