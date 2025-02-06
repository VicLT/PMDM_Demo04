package edu.victorlamas.actDemo04.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import edu.victorlamas.actDemo04.R
import edu.victorlamas.actDemo04.adapters.SupersAdapter
import edu.victorlamas.actDemo04.data.SupersDataSource
import edu.victorlamas.actDemo04.data.SupersRepository
import edu.victorlamas.actDemo04.databinding.ActivityMainBinding
import edu.victorlamas.actDemo04.databinding.LayoutEditorialBinding
import edu.victorlamas.actDemo04.domain.DeleteSuperUseCase
import edu.victorlamas.actDemo04.domain.model.SupersWithEditorial
import edu.victorlamas.actDemo04.ui.MyRoomApplication
import edu.victorlamas.actDemo04.ui.superhero.SupersActivity
import edu.victorlamas.actDemo04.ui.viewModel.MainViewModel
import edu.victorlamas.actDemo04.ui.viewModel.MainViewModelFactory
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // ViewModel con los datos de la BD y el useCase para eliminar un súper héroe
    private val vm: MainViewModel by viewModels {
        val db = (application as MyRoomApplication).supersDatabase
        val dataSource = SupersDataSource(db.supersDao())
        val repository = SupersRepository(dataSource)
        val deleteSuperUseCase = DeleteSuperUseCase(repository)
        MainViewModelFactory(repository, deleteSuperUseCase)
    }

    // Crea el adaptador con tres eventos: cambiar de actividad, marcar como
    // favorito y eliminar súper héroe
    private val adapter = SupersAdapter(
        onClickSuperHero = { supersWithEditorial ->
            SupersActivity.navigate(
                this,
                supersWithEditorial.superHero.idSuper
            )
        },
        onClickFavorite = { supersWithEditorial ->
            vm.updateFavorite(supersWithEditorial)
        },
        onClickDelete = { supersWithEditorial ->
            confirmDeleteWithSnackbar(supersWithEditorial)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Configura la toolbar y su menú
        binding.mToolbar.inflateMenu(R.menu.menu)

        // Asigna el adaptador al RecyclerView
        binding.recyclerView.adapter = adapter

        // Observa y actualiza la lista de súper héroes cuando onStart se completa
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.currentSuperHero.collect { superHeroes ->
                    adapter.submitList(superHeroes)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Configura los clics del menú de la toolbar
        binding.mToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                // Clic en opción para añadir una editorial
                R.id.opt_add_editorial -> {
                    addEditorial()
                    true
                }

                // Clic en opción para añadir un súper héroe
                R.id.opt_add_superhero -> {
                    lifecycleScope.launch {
                        vm.numEditorials.collect { numEditorials ->
                            Log.i("MainActivity", "Number of Editorials: $numEditorials")

                            // Navega a SupersActivity si existen editoriales
                            if (numEditorials > 0) {
                                SupersActivity.navigate(this@MainActivity)
                            } else {
                                // Muestra un toast si no hay editoriales
                                Toast.makeText(
                                    this@MainActivity,
                                    "No hay editoriales creadas",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            this.cancel()
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun addEditorial() {
        val bindDialog = LayoutEditorialBinding.inflate(layoutInflater)

        // Crea un diálogo de alerta para añadir editorial
        val dialog = MaterialAlertDialogBuilder(this).apply {
            setTitle(R.string.txt_opt_add_editorial)
            setView(bindDialog.root)
            setPositiveButton(android.R.string.ok, null)
            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.create()

        // Configura el botón OK del diálogo para validar entrada
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = bindDialog.tietEditorialName.text.toString()

                if (name.isEmpty()) {
                    bindDialog.tilEditorialName.error =
                        getString(R.string.txt_empty_field)
                } else {
                    Log.i("Dialog Editorial", "Name: $name")
                    vm.saveEditorial(name.trim())
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    // Muestra un snackbar de confirmación para borrar
    private fun confirmDeleteWithSnackbar(supersWithEditorial: SupersWithEditorial) {
        val snackbar = movedSnackbar(
            String.format(getString(
                R.string.txt_delete),
                supersWithEditorial.superHero.superName)
        ).setAction(R.string.txt_doit) {
            vm.deleteSuper(supersWithEditorial)
        }
        snackbar.show()
    }

    // Ajusta todos los snackbars a la parte baja de pantalla
    private fun movedSnackbar(textToShow: String): Snackbar {
        val snackbar = Snackbar.make(
            binding.root,
            textToShow,
            Snackbar.LENGTH_LONG
        )

        // Configura la posición en pantalla del snackbar
        val params = CoordinatorLayout.LayoutParams(snackbar.view.layoutParams)
        params.gravity = Gravity.BOTTOM
        params.setMargins(0, 0, 0, -binding.root.paddingBottom)
        snackbar.view.layoutParams = params
        return snackbar
    }
}