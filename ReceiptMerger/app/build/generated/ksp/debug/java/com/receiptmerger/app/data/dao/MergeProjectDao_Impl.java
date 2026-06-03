package com.receiptmerger.app.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.receiptmerger.app.data.MergeProjectEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MergeProjectDao_Impl implements MergeProjectDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MergeProjectEntity> __insertionAdapterOfMergeProjectEntity;

  private final EntityDeletionOrUpdateAdapter<MergeProjectEntity> __deletionAdapterOfMergeProjectEntity;

  private final EntityDeletionOrUpdateAdapter<MergeProjectEntity> __updateAdapterOfMergeProjectEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteProjectById;

  public MergeProjectDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMergeProjectEntity = new EntityInsertionAdapter<MergeProjectEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `merge_projects` (`id`,`name`,`template`,`createdAt`,`modifiedAt`,`outputPath`,`status`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MergeProjectEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getTemplate());
        statement.bindLong(4, entity.getCreatedAt());
        statement.bindLong(5, entity.getModifiedAt());
        statement.bindString(6, entity.getOutputPath());
        statement.bindString(7, entity.getStatus());
      }
    };
    this.__deletionAdapterOfMergeProjectEntity = new EntityDeletionOrUpdateAdapter<MergeProjectEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `merge_projects` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MergeProjectEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfMergeProjectEntity = new EntityDeletionOrUpdateAdapter<MergeProjectEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `merge_projects` SET `id` = ?,`name` = ?,`template` = ?,`createdAt` = ?,`modifiedAt` = ?,`outputPath` = ?,`status` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MergeProjectEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getTemplate());
        statement.bindLong(4, entity.getCreatedAt());
        statement.bindLong(5, entity.getModifiedAt());
        statement.bindString(6, entity.getOutputPath());
        statement.bindString(7, entity.getStatus());
        statement.bindString(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteProjectById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM merge_projects WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertProject(final MergeProjectEntity project,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMergeProjectEntity.insert(project);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteProject(final MergeProjectEntity project,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMergeProjectEntity.handle(project);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateProject(final MergeProjectEntity project,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMergeProjectEntity.handle(project);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteProjectById(final String projectId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteProjectById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, projectId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteProjectById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MergeProjectEntity>> getAllProjects() {
    final String _sql = "SELECT * FROM merge_projects";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"merge_projects"}, new Callable<List<MergeProjectEntity>>() {
      @Override
      @NonNull
      public List<MergeProjectEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTemplate = CursorUtil.getColumnIndexOrThrow(_cursor, "template");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedAt");
          final int _cursorIndexOfOutputPath = CursorUtil.getColumnIndexOrThrow(_cursor, "outputPath");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<MergeProjectEntity> _result = new ArrayList<MergeProjectEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MergeProjectEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpTemplate;
            _tmpTemplate = _cursor.getString(_cursorIndexOfTemplate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpModifiedAt;
            _tmpModifiedAt = _cursor.getLong(_cursorIndexOfModifiedAt);
            final String _tmpOutputPath;
            _tmpOutputPath = _cursor.getString(_cursorIndexOfOutputPath);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new MergeProjectEntity(_tmpId,_tmpName,_tmpTemplate,_tmpCreatedAt,_tmpModifiedAt,_tmpOutputPath,_tmpStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getProjectById(final String projectId,
      final Continuation<? super MergeProjectEntity> $completion) {
    final String _sql = "SELECT * FROM merge_projects WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, projectId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MergeProjectEntity>() {
      @Override
      @Nullable
      public MergeProjectEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTemplate = CursorUtil.getColumnIndexOrThrow(_cursor, "template");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedAt");
          final int _cursorIndexOfOutputPath = CursorUtil.getColumnIndexOrThrow(_cursor, "outputPath");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final MergeProjectEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpTemplate;
            _tmpTemplate = _cursor.getString(_cursorIndexOfTemplate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpModifiedAt;
            _tmpModifiedAt = _cursor.getLong(_cursorIndexOfModifiedAt);
            final String _tmpOutputPath;
            _tmpOutputPath = _cursor.getString(_cursorIndexOfOutputPath);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _result = new MergeProjectEntity(_tmpId,_tmpName,_tmpTemplate,_tmpCreatedAt,_tmpModifiedAt,_tmpOutputPath,_tmpStatus);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getLatestProject(final Continuation<? super MergeProjectEntity> $completion) {
    final String _sql = "SELECT * FROM merge_projects ORDER BY modifiedAt DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MergeProjectEntity>() {
      @Override
      @Nullable
      public MergeProjectEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTemplate = CursorUtil.getColumnIndexOrThrow(_cursor, "template");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfModifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedAt");
          final int _cursorIndexOfOutputPath = CursorUtil.getColumnIndexOrThrow(_cursor, "outputPath");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final MergeProjectEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpTemplate;
            _tmpTemplate = _cursor.getString(_cursorIndexOfTemplate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpModifiedAt;
            _tmpModifiedAt = _cursor.getLong(_cursorIndexOfModifiedAt);
            final String _tmpOutputPath;
            _tmpOutputPath = _cursor.getString(_cursorIndexOfOutputPath);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _result = new MergeProjectEntity(_tmpId,_tmpName,_tmpTemplate,_tmpCreatedAt,_tmpModifiedAt,_tmpOutputPath,_tmpStatus);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
