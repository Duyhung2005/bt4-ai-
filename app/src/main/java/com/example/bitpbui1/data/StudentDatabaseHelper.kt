package com.example.bitpbui1.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StudentDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "student_management.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_STUDENTS = "students"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_STUDENT_ID = "student_id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_AVATAR_URI = "avatar_uri"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_STUDENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_STUDENT_ID TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_AVATAR_URI TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        // Seed initial sample data
        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        onCreate(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        val sampleStudents = listOf(
            Student(
                name = "Nguyễn Văn Long",
                studentId = "BIT240001",
                email = "long.nv@gmail.com",
                avatarUri = "https://picsum.photos/seed/student1/300/300"
            ),
            Student(
                name = "Trần Thị Mai",
                studentId = "BDC230045",
                email = "mai.tt@gmail.com",
                avatarUri = "https://picsum.photos/seed/student2/300/300"
            ),
            Student(
                name = "Lê Hoàng Nam",
                studentId = "BAT220088",
                email = "nam.lh@gmail.com",
                avatarUri = "https://picsum.photos/seed/student3/300/300"
            )
        )

        for (student in sampleStudents) {
            val values = ContentValues().apply {
                put(COLUMN_NAME, student.name)
                put(COLUMN_STUDENT_ID, student.studentId)
                put(COLUMN_EMAIL, student.email)
                put(COLUMN_AVATAR_URI, student.avatarUri)
            }
            db.insert(TABLE_STUDENTS, null, values)
        }
    }

    fun getAllStudents(): List<Student> {
        val studentList = mutableListOf<Student>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_STUDENTS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_ID DESC"
        )

        cursor.use { c ->
            if (c.moveToFirst()) {
                val idIndex = c.getColumnIndexOrThrow(COLUMN_ID)
                val nameIndex = c.getColumnIndexOrThrow(COLUMN_NAME)
                val studentIdIndex = c.getColumnIndexOrThrow(COLUMN_STUDENT_ID)
                val emailIndex = c.getColumnIndexOrThrow(COLUMN_EMAIL)
                val avatarUriIndex = c.getColumnIndexOrThrow(COLUMN_AVATAR_URI)

                do {
                    val student = Student(
                        id = c.getLong(idIndex),
                        name = c.getString(nameIndex),
                        studentId = c.getString(studentIdIndex),
                        email = c.getString(emailIndex),
                        avatarUri = c.getString(avatarUriIndex)
                    )
                    studentList.add(student)
                } while (c.moveToNext())
            }
        }
        return studentList
    }

    fun getStudentById(id: Long): Student? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_STUDENTS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use { c ->
            if (c.moveToFirst()) {
                val idIndex = c.getColumnIndexOrThrow(COLUMN_ID)
                val nameIndex = c.getColumnIndexOrThrow(COLUMN_NAME)
                val studentIdIndex = c.getColumnIndexOrThrow(COLUMN_STUDENT_ID)
                val emailIndex = c.getColumnIndexOrThrow(COLUMN_EMAIL)
                val avatarUriIndex = c.getColumnIndexOrThrow(COLUMN_AVATAR_URI)

                return Student(
                    id = c.getLong(idIndex),
                    name = c.getString(nameIndex),
                    studentId = c.getString(studentIdIndex),
                    email = c.getString(emailIndex),
                    avatarUri = c.getString(avatarUriIndex)
                )
            }
        }
        return null
    }

    fun insertStudent(student: Student): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.name)
            put(COLUMN_STUDENT_ID, student.studentId)
            put(COLUMN_EMAIL, student.email)
            put(COLUMN_AVATAR_URI, student.avatarUri)
        }
        return db.insert(TABLE_STUDENTS, null, values)
    }

    fun updateStudent(student: Student): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.name)
            put(COLUMN_STUDENT_ID, student.studentId)
            put(COLUMN_EMAIL, student.email)
            put(COLUMN_AVATAR_URI, student.avatarUri)
        }
        return db.update(
            TABLE_STUDENTS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(student.id.toString())
        )
    }

    fun deleteStudent(id: Long): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_STUDENTS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }
}
