package vn.udn.ute.lab9_data.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import vn.udn.ute.lab9_data.model.Student

data class StudentDto(
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("student_code") val studentCode: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("gpa") val gpa: Double?,
) {
    fun toStudent(): Student {
        val name = requireNotNull(fullName).trim()
        val code = requireNotNull(studentCode).trim()
        val grade = requireNotNull(gpa)
        require(name.isNotEmpty() && code.isNotEmpty() && grade in 0.0..10.0)
        return Student(fullName = name, studentCode = code, email = email.orEmpty(), gpa = grade)
    }
}

interface StudentApiService {
    @GET("api/v1/students")
    suspend fun getStudents(): Response<List<StudentDto?>>
}

object RetrofitClient {
    // adb reverse chuyển cổng 8765 từ thiết bị sang máy đang chạy API mẫu.
    private const val BASE_URL = "http://127.0.0.1:8765/"

    val apiService: StudentApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StudentApiService::class.java)
    }
}
