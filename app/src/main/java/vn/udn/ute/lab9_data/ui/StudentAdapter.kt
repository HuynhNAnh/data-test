package vn.udn.ute.lab9_data.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.udn.ute.lab9_data.R
import vn.udn.ute.lab9_data.databinding.ItemStudentRoomBinding
import vn.udn.ute.lab9_data.model.Student

class StudentAdapter(
    private val onDelete: (Student) -> Unit,
) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(StudentDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StudentViewHolder(
        private val binding: ItemStudentRoomBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(student: Student) = with(binding) {
            val context = root.context
            tvName.text = student.fullName
            tvCode.text = context.getString(R.string.student_code_value, student.studentCode)
            tvEmail.text = student.email.ifEmpty { context.getString(R.string.student_no_email) }
            tvGpa.text = context.getString(R.string.student_gpa_value, student.gpa)
            btnDelete.contentDescription = context.getString(R.string.student_delete_name, student.fullName)
            btnDelete.setOnClickListener { onDelete(student) }
        }
    }

    private object StudentDiff : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean =
            oldItem == newItem
    }
}
