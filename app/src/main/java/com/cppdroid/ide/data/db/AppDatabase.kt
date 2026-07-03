package com.cppdroid.ide.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ===== ENTITIES =====

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val path: String,
    val description: String = "",
    val language: String = "cpp",
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
)

@Entity(tableName = "files")
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val name: String,
    val path: String,
    val content: String = "",
    val isOpen: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
)

@Entity(tableName = "build_logs")
data class BuildLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val command: String,
    val output: String,
    val isSuccess: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

// ===== DAOs =====

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY lastModified DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)
}

@Dao
interface FileDao {
    @Query("SELECT * FROM files WHERE projectId = :projectId")
    fun getFilesForProject(projectId: Long): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE id = :id")
    suspend fun getFileById(id: Long): FileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileEntity): Long

    @Update
    suspend fun updateFile(file: FileEntity)

    @Delete
    suspend fun deleteFile(file: FileEntity)

    @Query("UPDATE files SET content = :content, lastModified = :time WHERE id = :id")
    suspend fun updateFileContent(id: Long, content: String, time: Long = System.currentTimeMillis())
}

@Dao
interface BuildLogDao {
    @Query("SELECT * FROM build_logs WHERE projectId = :projectId ORDER BY timestamp DESC LIMIT 50")
    fun getBuildLogsForProject(projectId: Long): Flow<List<BuildLogEntity>>

    @Insert
    suspend fun insertLog(log: BuildLogEntity)

    @Query("DELETE FROM build_logs WHERE projectId = :projectId")
    suspend fun clearLogsForProject(projectId: Long)
}

// ===== DATABASE =====

@Database(
    entities = [ProjectEntity::class, FileEntity::class, BuildLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun fileDao(): FileDao
    abstract fun buildLogDao(): BuildLogDao

    companion object {
        const val DATABASE_NAME = "cppdroid_db"
    }
}
