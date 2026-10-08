package com.example.bitpbui1.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitpbui1.data.Student
import com.example.bitpbui1.data.StudentDatabaseHelper
import com.example.bitpbui1.data.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AppScreen {
    object ListScreen : AppScreen()
    data class DetailScreen(val studentId: Long) : AppScreen()
    data class AddEditScreen(val studentId: Long? = null) : AppScreen()
}

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudentRepository

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.ListScreen)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _students = MutableStateFlow<List<Student>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredStudents: StateFlow<List<Student>> = combine(_students, _searchQuery) { students, query ->
        if (query.isBlank()) {
            students
        } else {
            val trimmedQuery = query.trim().lowercase()
            students.filter { student ->
                student.name.lowercase().contains(trimmedQuery) ||
                        student.studentId.lowercase().contains(trimmedQuery) ||
                        student.email.lowercase().contains(trimmedQuery)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        val dbHelper = StudentDatabaseHelper(application)
        repository = StudentRepository(dbHelper)
        loadStudents()
    }

    fun loadStudents() {
        viewModelScope.launch {
            _students.value = repository.getAllStudents()
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun addStudent(name: String, studentId: String, email: String, avatarUri: String) {
        viewModelScope.launch {
            val newStudent = Student(
                name = name,
                studentId = studentId,
                email = email,
                avatarUri = avatarUri
            )
            repository.insertStudent(newStudent)
            loadStudents()
            _currentScreen.value = AppScreen.ListScreen
        }
    }

    fun updateStudent(id: Long, name: String, studentId: String, email: String, avatarUri: String) {
        viewModelScope.launch {
            val updatedStudent = Student(
                id = id,
                name = name,
                studentId = studentId,
                email = email,
                avatarUri = avatarUri
            )
            repository.updateStudent(updatedStudent)
            loadStudents()
            _currentScreen.value = AppScreen.ListScreen
        }
    }

    fun deleteStudent(id: Long) {
        viewModelScope.launch {
            repository.deleteStudent(id)
            loadStudents()
            if (_currentScreen.value is AppScreen.DetailScreen) {
                _currentScreen.value = AppScreen.ListScreen
            }
        }
    }

    suspend fun getStudentById(id: Long): Student? {
        return repository.getStudentById(id)
    }
}
