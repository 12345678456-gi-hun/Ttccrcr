package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "workspaces")
data class Workspace(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val templateType: String = "NONE", // NONE, GAMING, MINECRAFT
    val vercelProjectId: String? = null, // Vercel projectId saved locally
    val lastModified: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "project_files",
    foreignKeys = [
        ForeignKey(
            entity = Workspace::class,
            parentColumns = ["id"],
            childColumns = ["workspaceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["workspaceId", "path"], unique = true)]
)
data class ProjectFile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workspaceId: Long,
    val path: String,
    val content: String,
    val isBinary: Boolean = false,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val binaryData: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ProjectFile
        if (id != other.id) return false
        if (workspaceId != other.workspaceId) return false
        if (path != other.path) return false
        if (content != other.content) return false
        if (isBinary != other.isBinary) return false
        if (binaryData != null) {
            if (other.binaryData == null) return false
            if (!binaryData.contentEquals(other.binaryData)) return false
        } else if (other.binaryData != null) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + workspaceId.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + isBinary.hashCode()
        result = 31 * result + (binaryData?.contentHashCode() ?: 0)
        return result
    }
}

@Entity(tableName = "global_settings")
data class Setting(
    @PrimaryKey val key: String,
    val value: String
)
