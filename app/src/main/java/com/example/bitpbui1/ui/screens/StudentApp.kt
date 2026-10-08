package com.example.bitpbui1.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bitpbui1.ui.AppScreen
import com.example.bitpbui1.ui.StudentViewModel

@Composable
fun StudentApp(
    viewModel: StudentViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val filteredStudents by viewModel.filteredStudents.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    when (val screen = currentScreen) {
        is AppScreen.ListScreen -> {
            StudentListScreen(
                students = filteredStudents,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                onStudentClick = { id ->
                    viewModel.navigateTo(AppScreen.DetailScreen(id))
                },
                onEditStudentClick = { id ->
                    viewModel.navigateTo(AppScreen.AddEditScreen(id))
                },
                onDeleteStudentClick = { id ->
                    viewModel.deleteStudent(id)
                },
                onAddStudentClick = {
                    viewModel.navigateTo(AppScreen.AddEditScreen(null))
                }
            )
        }

        is AppScreen.DetailScreen -> {
            StudentDetailScreen(
                studentId = screen.studentId,
                onGetStudent = { id -> viewModel.getStudentById(id) },
                onBack = {
                    viewModel.navigateTo(AppScreen.ListScreen)
                },
                onEditStudent = { id ->
                    viewModel.navigateTo(AppScreen.AddEditScreen(id))
                },
                onDeleteStudent = { id ->
                    viewModel.deleteStudent(id)
                }
            )
        }

        is AppScreen.AddEditScreen -> {
            StudentAddEditScreen(
                studentId = screen.studentId,
                onGetStudent = { id -> viewModel.getStudentById(id) },
                onSaveStudent = { id, name, studentId, email, avatarUri ->
                    if (id == null) {
                        viewModel.addStudent(name, studentId, email, avatarUri)
                    } else {
                        viewModel.updateStudent(id, name, studentId, email, avatarUri)
                    }
                },
                onCancel = {
                    viewModel.navigateTo(AppScreen.ListScreen)
                }
            )
        }
    }
}
