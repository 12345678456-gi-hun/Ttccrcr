package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {
    @Query("SELECT * FROM workspaces ORDER BY lastModified DESC")
    fun getAllWorkspaces(): Flow<List<Workspace>>

    @Query("SELECT * FROM workspaces WHERE id = :id LIMIT 1")
    suspend fun getWorkspaceById(id: Long): Workspace?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspace(workspace: Workspace): Long

    @Update
    suspend fun updateWorkspace(workspace: Workspace)

    @Query("UPDATE workspaces SET vercelProjectId = :projectId WHERE id = :id")
    suspend fun updateVercelProjectId(id: Long, projectId: String?)

    @Delete
    suspend fun deleteWorkspace(workspace: Workspace)
}

@Dao
interface ProjectFileDao {
    @Query("SELECT * FROM project_files WHERE workspaceId = :workspaceId ORDER BY path ASC")
    fun getFilesByWorkspaceFlow(workspaceId: Long): Flow<List<ProjectFile>>

    @Query("SELECT * FROM project_files WHERE workspaceId = :workspaceId")
    suspend fun getFilesByWorkspaceSync(workspaceId: Long): List<ProjectFile>

    @Query("SELECT * FROM project_files WHERE workspaceId = :workspaceId AND path = :path LIMIT 1")
    suspend fun getFileByPath(workspaceId: Long, path: String): ProjectFile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: ProjectFile): Long

    @Query("UPDATE project_files SET content = :content WHERE workspaceId = :workspaceId AND path = :path")
    suspend fun updateFileContent(workspaceId: Long, path: String, content: String)

    @Query("DELETE FROM project_files WHERE workspaceId = :workspaceId AND path = :path")
    suspend fun deleteFileByPath(workspaceId: Long, path: String)

    @Delete
    suspend fun deleteFile(file: ProjectFile)
}

@Dao
interface SettingDao {
    @Query("SELECT value FROM global_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSettingValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: Setting)
}
