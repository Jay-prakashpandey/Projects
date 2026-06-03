package com.receiptmerger.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.receiptmerger.app.data.MergeProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MergeProjectDao {
    @Insert
    suspend fun insertProject(project: MergeProjectEntity)

    @Update
    suspend fun updateProject(project: MergeProjectEntity)

    @Delete
    suspend fun deleteProject(project: MergeProjectEntity)

    @Query("SELECT * FROM merge_projects")
    fun getAllProjects(): Flow<List<MergeProjectEntity>>

    @Query("SELECT * FROM merge_projects WHERE id = :projectId")
    suspend fun getProjectById(projectId: String): MergeProjectEntity?

    @Query("SELECT * FROM merge_projects ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getLatestProject(): MergeProjectEntity?

    @Query("DELETE FROM merge_projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: String)
}
