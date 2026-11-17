package vn.udn.ute.lab9_data.viewmodel

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParseException
import java.io.IOException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import vn.udn.ute.lab9_data.R
import vn.udn.ute.lab9_data.model.Student
import vn.udn.ute.lab9_data.repository.StudentRepository

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {
    val allStudents = repository.allStudents.asLiveData()

    private val messageChannel = Channel<Int>(Channel.BUFFERED)
    val messages = messageChannel.receiveAsFlow()
    private val _importing = MutableLiveData(false)
    val importing: LiveData<Boolean> = _importing

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

    fun importStudents() {
        if (_importing.value == true) return
        _importing.value = true
        viewModelScope.launch {
            try {
                val added = repository.importFromApi()
                messageChannel.send(if (added == 0) R.string.import_no_new else R.string.import_success)
            } catch (_: JsonParseException) {
                messageChannel.send(R.string.import_data_error)
            } catch (_: IllegalArgumentException) {
                messageChannel.send(R.string.import_data_error)
            } catch (_: HttpException) {
                messageChannel.send(R.string.import_server_error)
            } catch (_: IOException) {
                messageChannel.send(R.string.import_connection_error)
            } catch (_: SQLiteException) {
                messageChannel.send(R.string.import_save_error)
            } finally {
                _importing.value = false
            }
        }
    }
}
