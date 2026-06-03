package com.receiptmerger.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.receiptmerger.app.data.dao.MergeProjectDao;
import com.receiptmerger.app.data.dao.MergeProjectDao_Impl;
import com.receiptmerger.app.data.dao.ReceiptDao;
import com.receiptmerger.app.data.dao.ReceiptDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ReceiptMergerDatabase_Impl extends ReceiptMergerDatabase {
  private volatile ReceiptDao _receiptDao;

  private volatile MergeProjectDao _mergeProjectDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `receipts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `projectId` TEXT NOT NULL, `filePath` TEXT NOT NULL, `fileName` TEXT NOT NULL, `fileSize` INTEGER NOT NULL, `mimeType` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `merge_projects` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `template` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `modifiedAt` INTEGER NOT NULL, `outputPath` TEXT NOT NULL, `status` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '25d6a3a0708a61848877c09a8cfbe03f')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `receipts`");
        db.execSQL("DROP TABLE IF EXISTS `merge_projects`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsReceipts = new HashMap<String, TableInfo.Column>(7);
        _columnsReceipts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("projectId", new TableInfo.Column("projectId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("filePath", new TableInfo.Column("filePath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("fileName", new TableInfo.Column("fileName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("fileSize", new TableInfo.Column("fileSize", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("mimeType", new TableInfo.Column("mimeType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReceipts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReceipts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReceipts = new TableInfo("receipts", _columnsReceipts, _foreignKeysReceipts, _indicesReceipts);
        final TableInfo _existingReceipts = TableInfo.read(db, "receipts");
        if (!_infoReceipts.equals(_existingReceipts)) {
          return new RoomOpenHelper.ValidationResult(false, "receipts(com.receiptmerger.app.data.ReceiptEntity).\n"
                  + " Expected:\n" + _infoReceipts + "\n"
                  + " Found:\n" + _existingReceipts);
        }
        final HashMap<String, TableInfo.Column> _columnsMergeProjects = new HashMap<String, TableInfo.Column>(7);
        _columnsMergeProjects.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMergeProjects.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMergeProjects.put("template", new TableInfo.Column("template", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMergeProjects.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMergeProjects.put("modifiedAt", new TableInfo.Column("modifiedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMergeProjects.put("outputPath", new TableInfo.Column("outputPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMergeProjects.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMergeProjects = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMergeProjects = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMergeProjects = new TableInfo("merge_projects", _columnsMergeProjects, _foreignKeysMergeProjects, _indicesMergeProjects);
        final TableInfo _existingMergeProjects = TableInfo.read(db, "merge_projects");
        if (!_infoMergeProjects.equals(_existingMergeProjects)) {
          return new RoomOpenHelper.ValidationResult(false, "merge_projects(com.receiptmerger.app.data.MergeProjectEntity).\n"
                  + " Expected:\n" + _infoMergeProjects + "\n"
                  + " Found:\n" + _existingMergeProjects);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "25d6a3a0708a61848877c09a8cfbe03f", "60f0400ce4dd246a5b95157a5425973d");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "receipts","merge_projects");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `receipts`");
      _db.execSQL("DELETE FROM `merge_projects`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ReceiptDao.class, ReceiptDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MergeProjectDao.class, MergeProjectDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ReceiptDao receiptDao() {
    if (_receiptDao != null) {
      return _receiptDao;
    } else {
      synchronized(this) {
        if(_receiptDao == null) {
          _receiptDao = new ReceiptDao_Impl(this);
        }
        return _receiptDao;
      }
    }
  }

  @Override
  public MergeProjectDao projectDao() {
    if (_mergeProjectDao != null) {
      return _mergeProjectDao;
    } else {
      synchronized(this) {
        if(_mergeProjectDao == null) {
          _mergeProjectDao = new MergeProjectDao_Impl(this);
        }
        return _mergeProjectDao;
      }
    }
  }
}
