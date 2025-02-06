package edu.victorlamas.actDemo04.ui.superhero

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.victorlamas.actDemo04.R
import edu.victorlamas.actDemo04.data.SupersDataSource
import edu.victorlamas.actDemo04.data.SupersRepository
import edu.victorlamas.actDemo04.databinding.ActivitySupersBinding
import edu.victorlamas.actDemo04.domain.model.SuperHero
import edu.victorlamas.actDemo04.ui.MyRoomApplication
import edu.victorlamas.actDemo04.ui.viewModel.SupersViewModel
import edu.victorlamas.actDemo04.ui.viewModel.SupersViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SupersActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySupersBinding
    private var editorialIdAux = 0

    // Inicializa el ViewModel usando el patrón Factory
    private val vm: SupersViewModel by viewModels {
        val db = (application as MyRoomApplication).supersDatabase
        val dataSource = SupersDataSource(db.supersDao())
        val repository = SupersRepository(dataSource)
        val superIdAux = intent.getIntExtra(SUPER_ID, 0)
        SupersViewModelFactory(repository, superIdAux)
    }

    companion object {
        private const val TAG = "SupersActivity"
        const val SUPER_ID = "super_id"
        // Inicia SupersActivity, opcionalmente con ID de un súper héroe
        fun navigate(activity: AppCompatActivity, superId: Int = 0) {
            val intent = Intent(activity, SupersActivity::class.java).apply {
                putExtra(SUPER_ID, superId)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySupersBinding.inflate(layoutInflater)
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

        // Configura el botón de guardar para crear o actualizar un súper héroe
        binding.btnSave.setOnClickListener {
            if (editorialIdAux > 0) {
                val superHero = SuperHero(
                    superName = binding.tiedSuperName.text.toString().trim(),
                    realName = binding.tiedRealName.text.toString().trim(),
                    favorite = if (binding.cbFavorite.isChecked) 1 else 0,
                    idEditorial = editorialIdAux
                )
                vm.saveSuper(superHero)
                finish()
            } else {
                MaterialAlertDialogBuilder(this@SupersActivity).apply {
                    setTitle(getString(R.string.alert_title_error))
                    setMessage(getString(R.string.alert_description_error))
                }.show()
            }
        }

        lifecycleScope.launch {
            // IMPORTANTE: El repeatOnLifecycle + collect, suspende al finalizar,
            // por lo que aquello que quede por debajo no se ejecutará.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(vm.stateSupers, vm.stateEditorial) { superHero, editorial ->
                    Log.d(TAG, "superHero: $superHero")
                    Log.d(TAG, "editorial: $editorial")

                    binding.tiedSuperName.setText(superHero.superName)
                    binding.tiedRealName.setText(superHero.realName)
                    binding.cbFavorite.isChecked = superHero.favorite == 1

                    editorialIdAux = editorial.idEd
                    binding.tvEditorial.text = editorial.name
                }.collect()
            }
        }

        binding.btnEditorial.setOnClickListener {
            showEditorials()
        }
    }

    // Crea y muestra un diálogo para elegir una editorial de una lista
    private fun showEditorials() {
        lifecycleScope.launch {
            vm.allEditorials.collect { editorialsList ->
                // Configura el título y la lista
                MaterialAlertDialogBuilder(this@SupersActivity).apply {
                    setTitle(getString(R.string.txt_editorial))
                    setItems(
                        editorialsList
                            .map { editorial -> editorial.name }
                            .toTypedArray()
                    ) { dialog, which ->
                        // Al seleccionar una editorial, actualiza su ID y el
                        // nombre en la vista
                        editorialIdAux = editorialsList[which].idEd
                        binding.tvEditorial.text = editorialsList[which].name
                        dialog.dismiss()
                    }
                }.show()
            }
        }
    }
}