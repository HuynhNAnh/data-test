package vn.udn.ute.lab9_data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import vn.udn.ute.lab9_data.data.StudentDao
import vn.udn.ute.lab9_data.model.Student
import vn.udn.ute.lab9_data.network.StudentApiService

class StudentRepository(
    private val studentDao: StudentDao,
    private val apiService: () -> StudentApiService,
) {
    val allStudents = studentDao.getAllStudents()

    suspend fun insert(student: Student): Boolean = studentDao.insertStudent(student) != -1L

    suspend fun delete(student: Student) = studentDao.deleteStudent(student)

    suspend fun importFromApi(): Int = withContext(Dispatchers.IO) {
        val response = apiService().getStudents()
        if (!response.isSuccessful) throw HttpException(response)
        val students = requireNotNull(response.body()).map { requireNotNull(it).toStudent() }
        // Bỏ qua mã số đã tồn tại để giữ dữ liệu người dùng đang lưu trong Room.
        studentDao.insertStudents(students).count { it != -1L }
    }
}
