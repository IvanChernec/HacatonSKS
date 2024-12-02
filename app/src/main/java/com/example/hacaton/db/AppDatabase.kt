package com.example.hacaton.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverters
import com.example.hacaton.mappers.ScheduleWithDetails

@TypeConverters(Converters::class)
@Database(entities = [Group::class, Teacher::class, Subject::class, Schedule::class, Note::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun teacherDao(): TeacherDao
    abstract fun subjectDao(): SubjectDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun noteDao(): NoteDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "schedule_database"
                ).addMigrations(MIGRATION_3_4)  // Добавляем миграцию
            .build()
            }
            return instance!!
        }
    }
}

// Data Access Objects (DAOs)
@Dao
interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groups: List<Group>)

    @Query("SELECT * FROM groups WHERE id = :id")
    suspend fun getGroupById(id: Int): Group

    @Query("SELECT * FROM groups")
    suspend fun getAllGroups(): List<Group>

    @Insert
    suspend fun insertGroup(group: Group)

    @Query("SELECT COUNT(*) FROM groups")
    suspend fun getGroupCount(): Int

    @Query("SELECT * FROM groups WHERE name = :name LIMIT 1")
    suspend fun getGroupByName(name: String): Group
}
@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: Note)
    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes WHERE scheduleId = :scheduleId ORDER BY timestamp DESC")
    suspend fun getNotesForSchedule(scheduleId: Int): List<Note>

    @Query("SELECT * FROM notes WHERE scheduleId = :scheduleId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastNoteForSchedule(scheduleId: Int): Note?
}

@Dao
interface TeacherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(teachers: List<Teacher>)

    @Query("SELECT * FROM teachers WHERE id = :id")
    suspend fun getTeacherById(id: Int): Teacher

    @Query("SELECT * FROM teachers")
    suspend fun getAllTeachers(): List<Teacher>

    @Insert
    suspend fun insertTeacher(teacher: Teacher)

    @Query("SELECT COUNT(*) FROM teachers")
    suspend fun getTeacherCount(): Int

    @Query("SELECT * FROM teachers WHERE name = :name LIMIT 1")
    suspend fun getTeacherByName(name: String): Teacher
}

@Dao
interface SubjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subjects: List<Subject>)

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Int): Subject

    @Query("SELECT * FROM subjects")
    suspend fun getAllSubjects(): List<Subject>

    @Insert
    suspend fun insertSubject(subject: Subject)

}

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(schedules: List<Schedule>)

    @Query("SELECT * FROM schedule WHERE id = :scheduleId")
    suspend fun getScheduleById(scheduleId: Int): Schedule?

    @Query("""
        SELECT * FROM schedule 
        WHERE subjectId = :subjectId 
        AND (day > :currentDay OR (day = :currentDay AND week > :currentWeek))
        ORDER BY week ASC, day ASC 
        LIMIT 1
    """)
    suspend fun getNextSimilarSchedule(
        subjectId: Int,
        currentDay: Int,
        currentWeek: Int
    ): Schedule?

    @Transaction
    @Query("SELECT * FROM notes WHERE scheduleId = :scheduleId ORDER BY timestamp DESC")
    suspend fun getNotesForSchedule(scheduleId: Int): List<Note>

    @Query("""
    SELECT s.*, 
           sub.name as subjectName,
           t.name as teacherName,
           g.name as groupName
    FROM schedule s
    JOIN subjects sub ON s.subjectId = sub.id
    JOIN teachers t ON s.teacherId = t.id
    JOIN groups g ON s.groupId = g.id
    WHERE g.id = :groupId
""")
    suspend fun getScheduleWithDetailsForGroup(groupId: Int): List<ScheduleWithDetails>

    @Query("""
        SELECT schedule.*, 
               subjects.name as subjectName,
               teachers.name as teacherName,
               groups.name as groupName
        FROM schedule
        INNER JOIN groups ON schedule.groupId = groups.id
        INNER JOIN teachers ON schedule.teacherId = teachers.id
        INNER JOIN subjects ON schedule.subjectId = subjects.id
        WHERE groups.id = :groupId
        ORDER BY schedule.startTime ASC
    """)
    suspend fun getScheduleForGroup(groupId: Int): List<ScheduleWithDetails>

    @Query("""
        SELECT schedule.*, 
               subjects.name as subjectName,
               teachers.name as teacherName,
               groups.name as groupName
        FROM schedule
        INNER JOIN groups ON schedule.groupId = groups.id
        INNER JOIN teachers ON schedule.teacherId = teachers.id
        INNER JOIN subjects ON schedule.subjectId = subjects.id
        WHERE teachers.id = :teacherId
        ORDER BY schedule.startTime ASC
    """)
    suspend fun getScheduleForTeacher(teacherId: Int): List<ScheduleWithDetails>

    @Insert
    suspend fun insertSchedule(schedule: Schedule)

    @Query("SELECT COUNT(*) FROM schedule")
    suspend fun getScheduleCount(): Int
}

