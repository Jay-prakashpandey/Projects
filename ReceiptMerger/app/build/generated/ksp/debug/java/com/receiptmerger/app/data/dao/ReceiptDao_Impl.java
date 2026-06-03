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
import com.receiptmerger.app.data.ReceiptEntity;
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
public final class ReceiptDao_Impl implements ReceiptDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReceiptEntity> __insertionAdapterOfReceiptEntity;

  private final EntityDeletionOrUpdateAdapter<ReceiptEntity> __deletionAdapterOfReceiptEntity;

  private final EntityDeletionOrUpdateAdapter<ReceiptEntity> __updateAdapterOfReceiptEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteProjectReceipts;

  public ReceiptDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReceiptEntity = new EntityInsertionAdapter<ReceiptEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `receipts` (`id`,`projectId`,`filePath`,`fileName`,`fileSize`,`mimeType`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReceiptEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getProjectId());
        statement.bindString(3, entity.getFilePath());
        statement.bindString(4, entity.getFileName());
        statement.bindLong(5, entity.getFileSize());
        statement.bindString(6, entity.getMimeType());
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfReceiptEntity = new EntityDeletionOrUpdateAdapter<ReceiptEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `receipts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReceiptEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfReceiptEntity = new EntityDeletionOrUpdateAdapter<ReceiptEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `receipts` SET `id` = ?,`projectId` = ?,`filePath` = ?,`fileName` = ?,`fileSize` = ?,`mimeType` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReceiptEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getProjectId());
        statement.bindString(3, entity.getFilePath());
        statement.bindString(4, entity.getFileName());
        statement.bindLong(5, entity.getFileSize());
        statement.bindString(6, entity.getMimeType());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteProjectReceipts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM receipts WHERE projectId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertReceipt(final ReceiptEntity receipt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReceiptEntity.insert(receipt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertReceipts(final List<ReceiptEntity> receipts,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReceiptEntity.insert(receipts);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteReceipt(final ReceiptEntity receipt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfReceiptEntity.handle(receipt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateReceipt(final ReceiptEntity receipt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfReceiptEntity.handle(receipt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteProjectReceipts(final String projectId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteProjectReceipts.acquire();
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
          __preparedStmtOfDeleteProjectReceipts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ReceiptEntity>> getProjectReceipts(final String projectId) {
    final String _sql = "SELECT * FROM receipts WHERE projectId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, projectId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"receipts"}, new Callable<List<ReceiptEntity>>() {
      @Override
      @NonNull
      public List<ReceiptEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfMimeType = CursorUtil.getColumnIndexOrThrow(_cursor, "mimeType");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ReceiptEntity> _result = new ArrayList<ReceiptEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReceiptEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpProjectId;
            _tmpProjectId = _cursor.getString(_cursorIndexOfProjectId);
            final String _tmpFilePath;
            _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final String _tmpMimeType;
            _tmpMimeType = _cursor.getString(_cursorIndexOfMimeType);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ReceiptEntity(_tmpId,_tmpProjectId,_tmpFilePath,_tmpFileName,_tmpFileSize,_tmpMimeType,_tmpCreatedAt);
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
  public Object getReceiptById(final int id,
      final Continuation<? super ReceiptEntity> $completion) {
    final String _sql = "SELECT * FROM receipts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReceiptEntity>() {
      @Override
      @Nullable
      public ReceiptEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfMimeType = CursorUtil.getColumnIndexOrThrow(_cursor, "mimeType");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ReceiptEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpProjectId;
            _tmpProjectId = _cursor.getString(_cursorIndexOfProjectId);
            final String _tmpFilePath;
            _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final String _tmpMimeType;
            _tmpMimeType = _cursor.getString(_cursorIndexOfMimeType);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new ReceiptEntity(_tmpId,_tmpProjectId,_tmpFilePath,_tmpFileName,_tmpFileSize,_tmpMimeType,_tmpCreatedAt);
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
