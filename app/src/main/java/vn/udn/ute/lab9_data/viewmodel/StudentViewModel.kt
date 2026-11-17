package vn.udn.ute.lab9_data.viewmodel

import android.database.sqlite.SQLiteException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.udn.ute.lab9_data.R
import vn.udn.ute.lab9_data.model.Student
import vn.udn.ute.lab9_data.repository.StudentRepository

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {
    val allStudents = repository.allStudents.asLiveData()

    private val messageChannel = Channel<Int>(Channel.BUFFERED)
    val messages = messageChannel.receiveAsFlow()

    fun insert(student: Student) {
        viewModelScope.launch {
            try {
                if (!repository.insert(student)) messageChannel.send(R.string.student_duplicate_code)
            } catch (_: SQLiteException) {
                messageChannel.send(R.string.student_save_error)
            }
        }
    }

    fun delete(student: Student) {
        viewModelScope.launch {
            try {
                repository.delete(student)
            } catch (_: SQLiteException) {
                messageChannel.send(R.string.student_delete_error)
            }
        }
    }
}
