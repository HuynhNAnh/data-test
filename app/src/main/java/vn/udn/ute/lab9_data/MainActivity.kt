package vn.udn.ute.lab9_data

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import vn.udn.ute.lab9_data.data.AppDatabase
import vn.udn.ute.lab9_data.databinding.ActivityMainBinding
import vn.udn.ute.lab9_data.repository.StudentRepository
import vn.udn.ute.lab9_data.ui.StudentListFragment
import vn.udn.ute.lab9_data.viewmodel.StudentViewModelFactory

class MainActivity : AppCompatActivity() {
    internal val studentViewModelFactory by lazy {
        val dao = AppDatabase.getDatabase(this).studentDao()
        StudentViewModelFactory(StudentRepository(dao))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
            )
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.fragment_container_view, StudentListFragment())
            }
        }
    }
}
