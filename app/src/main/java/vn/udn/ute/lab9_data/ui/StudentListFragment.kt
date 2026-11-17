package vn.udn.ute.lab9_data.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import vn.udn.ute.lab9_data.BuildConfig
import vn.udn.ute.lab9_data.MainActivity
import vn.udn.ute.lab9_data.R
import vn.udn.ute.lab9_data.databinding.FragmentStudentListBinding
import vn.udn.ute.lab9_data.model.Student
import vn.udn.ute.lab9_data.viewmodel.StudentViewModel

class StudentListFragment : Fragment() {
    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val viewModel: StudentViewModel by activityViewModels {
        (requireActivity() as MainActivity).studentViewModelFactory
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentStudentListBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = StudentAdapter(::confirmDelete)
        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.adapter = adapter
        binding.btnImport.isVisible = BuildConfig.DEBUG
        binding.btnImport.setOnClickListener { viewModel.importStudents() }
        viewModel.importing.observe(viewLifecycleOwner) { importing ->
            binding.btnImport.isEnabled = !importing
            binding.btnImport.setText(if (importing) R.string.import_loading else R.string.import_students)
        }

        viewModel.allStudents.observe(viewLifecycleOwner) { students ->
            adapter.submitList(students)
            binding.emptyLayout.isVisible = students.isEmpty()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.messages.collect { message ->
                    Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                        .setAnchorView(binding.fabAdd)
                        .show()
                }
            }
        }
        binding.fabAdd.setOnClickListener {
            if (childFragmentManager.findFragmentByTag(ADD_STUDENT_TAG) == null) {
                AddStudentDialogFragment().showNow(childFragmentManager, ADD_STUDENT_TAG)
            }
        }
    }

    private fun confirmDelete(student: Student) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.student_delete_title)
            .setMessage(getString(R.string.student_delete_confirm, student.fullName))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.student_delete) { _, _ -> viewModel.delete(student) }
            .show()
    }

    override fun onDestroyView() {
        binding.rvStudents.adapter = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val ADD_STUDENT_TAG = "ADD_STUDENT"
    }
}
