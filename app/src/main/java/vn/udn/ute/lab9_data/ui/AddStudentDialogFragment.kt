package vn.udn.ute.lab9_data.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vn.udn.ute.lab9_data.MainActivity
import vn.udn.ute.lab9_data.R
import vn.udn.ute.lab9_data.databinding.DialogAddStudentBinding
import vn.udn.ute.lab9_data.model.Student
import vn.udn.ute.lab9_data.viewmodel.StudentViewModel

class AddStudentDialogFragment : DialogFragment() {
    private val viewModel: StudentViewModel by activityViewModels {
        (requireActivity() as MainActivity).studentViewModelFactory
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAddStudentBinding.inflate(layoutInflater)
        val alert = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.student_add)
            .setView(binding.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null)
            .create()
        alert.setOnShowListener {
            // Gắn sự kiện sau khi mở để hộp thoại vẫn hiện nếu nhập sai.
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val student = readStudent(binding) ?: return@setOnClickListener
                viewModel.insert(student)
                dismiss()
            }
        }
        return alert
    }

    private fun readStudent(binding: DialogAddStudentBinding): Student? = with(binding) {
        val name = etName.text.toString().trim()
        val code = etCode.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val gpa = etGpa.text.toString().trim().replace(',', '.').toDoubleOrNull()

        if (name.isEmpty()) {
            etName.error = getString(R.string.student_name_required)
            etName.requestFocus()
            return null
        }
        if (code.isEmpty()) {
            etCode.error = getString(R.string.student_code_required)
            etCode.requestFocus()
            return null
        }
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.student_email_invalid)
            etEmail.requestFocus()
            return null
        }
        if (gpa == null || gpa !in 0.0..10.0) {
            etGpa.error = getString(R.string.student_gpa_invalid)
            etGpa.requestFocus()
            return null
        }
        Student(fullName = name, studentCode = code, email = email, gpa = gpa)
    }
}
