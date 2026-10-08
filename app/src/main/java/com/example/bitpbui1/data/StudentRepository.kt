package com.example.bitpbui1.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StudentRepository(private val dbHelper: StudentDatabaseHelper) {

    suspend fun getAllStudents(): List<Student> = withContext(Dispatchers.IO) {
        dbHelper.getAllStudents()
    }

    suspend fun getStudentById(id: Long): Student? = withContext(Dispatchers.IO) {
        dbHelper.getStudentById(id)
    }

    suspend fun insertStudent(student: Student): Long = withContext(Dispatchers.IO) {
        dbHelper.insertStudent(student)
    }

    suspend fun updateStudent(student: Student): Int = withContext(Dispatchers.IO) {
        dbHelper.updateStudent(student)
    }

    suspend fun deleteStudent(id: Long): Int = withContext(Dispatchers.IO) {
        dbHelper.deleteStudent(id)
    }
}
