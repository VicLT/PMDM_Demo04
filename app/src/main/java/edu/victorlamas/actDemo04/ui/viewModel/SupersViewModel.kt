package edu.victorlamas.actDemo04.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.victorlamas.actDemo04.data.SupersRepository
import edu.victorlamas.actDemo04.domain.model.Editorial
import edu.victorlamas.actDemo04.domain.model.SuperHero
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel encargado de gestionar el estado de un súper héroe y su editorial
class SupersViewModel(
    private val supersRepository: SupersRepository,
    private val superId: Int
) : ViewModel() {
    private val _stateSupers = MutableStateFlow(SuperHero())
    val stateSupers: StateFlow<SuperHero> = _stateSupers.asStateFlow()

    private val _stateEditorial = MutableStateFlow(Editorial())
    val stateEditorial: StateFlow<Editorial> = _stateEditorial.asStateFlow()

    val allEditorials = supersRepository.allEditorials

    // Se intenta recuperar el superhéroe que se pasa por ID
    init {
        viewModelScope.launch {
            val superAux = supersRepository.getSuperById(superId)
            if (superAux != null) {
                _stateSupers.value = superAux.superHero
                _stateEditorial.value = superAux.editorial
            }
        }
    }

    // Guarda un súper héroe en la base de datos
    fun saveSuper(superHero: SuperHero) {
        viewModelScope.launch {
            // Copia los datos del súper héroe y guarda los cambios
            val superAux = _stateSupers.value.copy(
                superName = superHero.superName,
                realName = superHero.realName,
                favorite = superHero.favorite,
                idEditorial = superHero.idEditorial
            )
            supersRepository.insertSuperHero(superAux)
        }
    }
}

// Factory para crear instancias de SupersViewModel, requiriendo el repositorio
// y el ID del súper héroe
@Suppress("UNCHECKED_CAST")
class SupersViewModelFactory(
    private val supersRepository: SupersRepository,
    private val superId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SupersViewModel(supersRepository, superId) as T
    }
}