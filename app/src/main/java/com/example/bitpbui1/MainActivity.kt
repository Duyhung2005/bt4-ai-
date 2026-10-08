package com.example.bitpbui1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bitpbui1.ui.theme.BàiTậpBuổi1Theme

sealed class Screen {
    object Screen1 : Screen()
    data class Screen2(val name: String, val studentId: String) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BàiTậpBuổi1Theme {
                com.example.bitpbui1.ui.screens.StudentApp()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Screen1) }

    when (val screen = currentScreen) {
        is Screen.Screen1 -> Screen1(
            onNavigateToScreen2 = { name, studentId ->
                currentScreen = Screen.Screen2(name, studentId)
            }
        )
        is Screen.Screen2 -> Screen2(
            name = screen.name,
            studentId = screen.studentId,
            onBack = {
                currentScreen = Screen.Screen1
            }
        )
    }
}

@Composable
fun Screen1(
    onNavigateToScreen2: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var nameInput by remember { mutableStateOf("") }
    var studentIdInput by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var studentIdError by remember { mutableStateOf<String?>(null) }

    fun validateAndSubmit() {
        var isValid = true
        if (nameInput.trim().isEmpty()) {
            nameError = "Vui lòng nhập họ và tên"
            isValid = false
        } else {
            nameError = null
        }

        // Pattern: B + 2 chữ cái (a-z, A-Z) + số từ 22-30 + 4 chữ số (0-9)
        val studentIdRegex = Regex("^[bB][a-zA-Z]{2}(2[2-9]|30)\\d{4}$")

        if (studentIdInput.trim().isEmpty()) {
            studentIdError = "Vui lòng nhập MSSV"
            isValid = false
        } else if (!studentIdRegex.matches(studentIdInput.trim())) {
            studentIdError = "MSSV phải đúng định dạng B**22**** (Ví dụ: BIT240001)"
            isValid = false
        } else {
            studentIdError = null
        }

        if (isValid) {
            onNavigateToScreen2(nameInput.trim(), studentIdInput.trim())
        }
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Hàng 1: Ô 1 (Xanh dương) - Chiếm toàn bộ chiều rộng
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
            ) {
                GridBox(
                    number = "1",
                    backgroundColor = Color(0xFF2196F3),
                    textColor = Color.White,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            // Hàng 2: Ô 2 (Đỏ) - Chiếm toàn bộ chiều rộng
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
            ) {
                GridBox(
                    number = "2",
                    backgroundColor = Color(0xFFFF3B30),
                    textColor = Color.White,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            // Hàng 3: Ô 3 (Vàng), Ô 4 (Xanh lá), Ô 5 (Tím), Khoảng trống (Tỷ lệ 1:1:1:1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                GridBox(
                    number = "3",
                    backgroundColor = Color(0xFFFFCC00),
                    textColor = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                )
                GridBox(
                    number = "4",
                    backgroundColor = Color(0xFF27AE60),
                    textColor = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                )
                GridBox(
                    number = "5",
                    backgroundColor = Color(0xFF8E24AA),
                    textColor = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            // Hàng 4: Ô 6 (Cam) - Chiếm toàn bộ chiều rộng
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
            ) {
                GridBox(
                    number = "6",
                    backgroundColor = Color(0xFFFF9500),
                    textColor = Color.White,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tiêu đề form nhập thông tin
            Text(
                text = "Nhap thong tin sinh vien",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
            )

            // Ô nhập Name với placeholder (chữ mờ xám)
            OutlinedTextField(
                value = nameInput,
                onValueChange = {
                    nameInput = it
                    if (it.isNotBlank()) nameError = null
                },
                placeholder = {
                    Text(
                        text = "Enter your name",
                        color = Color.Gray,
                    )
                },
                isError = nameError != null,
                supportingText = {
                    nameError?.let { error ->
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE67E22),
                    unfocusedBorderColor = Color(0xFFCCCCCC),
                )
            )

            // Ô nhập Student ID / MSSV với placeholder (chữ mờ xám)
            OutlinedTextField(
                value = studentIdInput,
                onValueChange = {
                    studentIdInput = it
                    if (it.isNotBlank()) studentIdError = null
                },
                placeholder = {
                    Text(
                        text = "Enter your student ID",
                        color = Color.Gray,
                    )
                },
                isError = studentIdError != null,
                supportingText = {
                    studentIdError?.let { error ->
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE67E22),
                    unfocusedBorderColor = Color(0xFFCCCCCC)
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Nút bấm "Click me" màu cam ở bottom-center
            Button(
                onClick = { validateAndSubmit() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE67E22)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Click me",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun Screen2(
    name: String,
    studentId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Nút Back màu cam ở top-left
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE67E22),
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp,
                ),
            ) {
                Text(
                    text = "Back",
                    fontSize = 14.sp,
                    color = Color.White,
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Phần hiển thị ở giữa màn hình
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Vòng tròn xám ở giữa
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFF888888), shape = CircleShape)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Screen 2",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Text(
                    text = "Name: $name",
                    fontSize = 16.sp,
                    color = Color(0xFF555555)
                )

                Text(
                    text = "Student ID: $studentId",
                    fontSize = 16.sp,
                    color = Color(0xFF555555)
                )
            }
        }
    }
}

@Composable
fun GridBox(
    number: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number,
            color = textColor,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Screen1Preview() {
    BàiTậpBuổi1Theme {
        Screen1(onNavigateToScreen2 = { _, _ -> })
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Screen2Preview() {
    BàiTậpBuổi1Theme {
        Screen2(name = "Nguyen Van Long", studentId = "BIT240001", onBack = {})
    }
}
