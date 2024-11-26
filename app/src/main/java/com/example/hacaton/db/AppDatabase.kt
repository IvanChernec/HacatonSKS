package com.example.hacaton.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hacaton.mappers.ScheduleWithDetails

@Database(entities = [Group::class, Teacher::class, Subject::class, Schedule::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun teacherDao(): TeacherDao
    abstract fun subjectDao(): SubjectDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "schedule_database"
                ).build()
            }
            return instance!!
        }
    }
}

// Data Access Objects (DAOs)
@Dao
interface GroupDao {
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
interface TeacherDao {
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
    @Query("SELECT * FROM subjects")
    suspend fun getAllSubjects(): List<Subject>

    @Insert
    suspend fun insertSubject(subject: Subject)

}

@Dao
interface ScheduleDao {

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

