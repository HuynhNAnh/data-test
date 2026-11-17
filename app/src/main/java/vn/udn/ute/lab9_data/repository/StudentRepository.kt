package vn.udn.ute.lab9_data.repository

import vn.udn.ute.lab9_data.data.StudentDao
import vn.udn.ute.lab9_data.model.Student

class StudentRepository(private val studentDao: StudentDao) {
    val allStudents = studentDao.getAllStudents()

    suspend fun insert(student: Student): Boolean = studentDao.insertStudent(student) != -1L

    suspend fun delete(student: Student) = studentDao.deleteStudent(student)
}
