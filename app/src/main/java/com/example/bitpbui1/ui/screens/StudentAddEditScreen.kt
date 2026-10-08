package com.example.bitpbui1.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.SubcomposeAsyncImage
import com.example.bitpbui1.R
import com.example.bitpbui1.data.Student
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAddEditScreen(
    studentId: Long?,
    onGetStudent: suspend (Long) -> Student?,
    onSaveStudent: (Long?, String, String, String, String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var nameInput by remember { mutableStateOf("") }
    var studentIdInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var avatarUriInput by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var studentIdError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    var showEditConfirmDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    val isEditMode = studentId != null

    val errNameEmptyMsg = stringResource(R.string.err_name_empty)
    val errStudentIdEmptyMsg = stringResource(R.string.err_student_id_empty)
    val errStudentIdInvalidMsg = stringResource(R.string.err_student_id_invalid)
    val errEmailEmptyMsg = stringResource(R.string.err_email_empty)
    val errEmailInvalidMsg = stringResource(R.string.err_email_invalid)
    val permDeniedMsg = stringResource(R.string.permission_denied_msg)

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = copyUriToInternalStorage(context, it)
            avatarUriInput = savedPath
        }
    }

    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            photoPickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, permDeniedMsg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(studentId) {
        if (studentId != null) {
            val student = onGetStudent(studentId)
            student?.let {
                nameInput = it.name
                studentIdInput = it.studentId
                emailInput = it.email
                avatarUriInput = it.avatarUri
            }
        }
    }

    fun validateAndSave() {
        var isValid = true

        // Validate Họ tên
        if (nameInput.trim().isEmpty()) {
            nameError = errNameEmptyMsg
            isValid = false
        } else {
            nameError = null
        }

        // Validate MSSV theo định dạng B**22****
        val studentIdRegex = Regex("^[bB][a-zA-Z]{2}(2[2-9]|30)\\d{4}$")
        if (studentIdInput.trim().isEmpty()) {
            studentIdError = errStudentIdEmptyMsg
            isValid = false
        } else if (!studentIdRegex.matches(studentIdInput.trim())) {
            studentIdError = errStudentIdInvalidMsg
            isValid = false
        } else {
            studentIdError = null
        }

        // Validate Email
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (emailInput.trim().isEmpty()) {
            emailError = errEmailEmptyMsg
            isValid = false
        } else if (!emailRegex.matches(emailInput.trim())) {
            emailError = errEmailInvalidMsg
            isValid = false
        } else {
            emailError = null
        }

        if (isValid) {
            if (isEditMode) {
                // Sửa thông tin -> Yêu cầu bật hộp thoại xác nhận
                showEditConfirmDialog = true
            } else {
                // Thêm mới trực tiếp
                onSaveStudent(
                    null,
                    nameInput.trim(),
                    studentIdInput.trim(),
                    emailInput.trim(),
                    avatarUriInput.trim()
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) stringResource(R.string.edit_title) else stringResource(R.string.add_title),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE67E22)
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Xem trước Avatar
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                if (avatarUriInput.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = avatarUriInput,
                        contentDescription = "Preview Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color(0xFFE67E22),
                                    strokeWidth = 2.dp
                                )
                            }
                        },
                        error = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default Avatar",
                                tint = Color.Gray,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Avatar",
                        tint = Color.Gray,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            // Nút Chọn Ảnh từ Thiết Bị -> Bật hộp thoại hỏi trước khi cấp quyền/mở gallery
            OutlinedButton(
                onClick = { showPermissionDialog = true },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = stringResource(R.string.choose_from_gallery))
            }

            // Ô Nhập Link Ảnh (nếu có URL online)
            OutlinedTextField(
                value = avatarUriInput,
                onValueChange = { avatarUriInput = it },
                label = { Text(stringResource(R.string.avatar_url_label)) },
                placeholder = { Text(stringResource(R.string.avatar_url_placeholder)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Image, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE67E22),
                    unfocusedBorderColor = Color(0xFFCCCCCC)
                )
            )

            // Ô Nhập Họ và Tên
            OutlinedTextField(
                value = nameInput,
                onValueChange = {
                    nameInput = it
                    if (it.isNotBlank()) nameError = null
                },
                label = { Text(stringResource(R.string.full_name_label)) },
                placeholder = { Text(stringResource(R.string.full_name_placeholder)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                },
                isError = nameError != null,
                supportingText = {
                    nameError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE67E22),
                    unfocusedBorderColor = Color(0xFFCCCCCC)
                )
            )

            // Ô Nhập MSSV
            OutlinedTextField(
                value = studentIdInput,
                onValueChange = {
                    studentIdInput = it
                    if (it.isNotBlank()) studentIdError = null
                },
                label = { Text(stringResource(R.string.student_id_label)) },
                placeholder = { Text(stringResource(R.string.student_id_placeholder)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Badge, contentDescription = null)
                },
                isError = studentIdError != null,
                supportingText = {
                    studentIdError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE67E22),
                    unfocusedBorderColor = Color(0xFFCCCCCC)
                )
            )

            // Ô Nhập Email
            OutlinedTextField(
                value = emailInput,
                onValueChange = {
                    emailInput = it
                    if (it.isNotBlank()) emailError = null
                },
                label = { Text(stringResource(R.string.email_label)) },
                placeholder = { Text(stringResource(R.string.email_placeholder)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                },
                isError = emailError != null,
                supportingText = {
                    emailError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE67E22),
                    unfocusedBorderColor = Color(0xFFCCCCCC)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nút Lưu và Hủy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { validateAndSave() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isEditMode) stringResource(R.string.btn_save_changes) else stringResource(R.string.btn_add),
                        fontSize = 16.sp
                    )
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = stringResource(R.string.btn_cancel), fontSize = 16.sp, color = Color.Gray)
                }
            }
        }
    }

    // Dialog xác nhận sửa thông tin
    if (showEditConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showEditConfirmDialog = false },
            title = { Text(stringResource(R.string.confirm_edit_title)) },
            text = { Text(stringResource(R.string.confirm_edit_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEditConfirmDialog = false
                        onSaveStudent(
                            studentId,
                            nameInput.trim(),
                            studentIdInput.trim(),
                            emailInput.trim(),
                            avatarUriInput.trim()
                        )
                    }
                ) {
                    Text(stringResource(R.string.btn_yes), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditConfirmDialog = false }) {
                    Text(stringResource(R.string.btn_no))
                }
            }
        )
    }

    // Dialog hỏi quyền trước khi truy cập avatar/gallery
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(stringResource(R.string.permission_dialog_title)) },
            text = { Text(stringResource(R.string.permission_dialog_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        val currentPermission = ContextCompat.checkSelfPermission(context, permissionToRequest)
                        if (currentPermission == PackageManager.PERMISSION_GRANTED) {
                            photoPickerLauncher.launch("image/*")
                        } else {
                            permissionLauncher.launch(permissionToRequest)
                        }
                    }
                ) {
                    Text(stringResource(R.string.btn_agree), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

private fun copyUriToInternalStorage(context: Context, uri: Uri): String {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return uri.toString()
        val file = File(context.filesDir, "avatar_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (_: Exception) {
        uri.toString()
    }
}
