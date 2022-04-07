package com.example.employee
import android.content.*
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import java.lang.IllegalArgumentException
import java.util.HashMap
class EmployeeProvider() : ContentProvider() {
    companion object {
        val PROVIDER_NAME = "com.example.employee.EmployeeProvider"
        val URL = "content://" + PROVIDER_NAME + "/employees"
        val CONTENT_URI = Uri.parse(URL)
        val _ID = "_id"
        val NAME = "name"
        val SALARY = "salary"
        private val EMPLOYEES_PROJECTION_MAP: HashMap<String, String>? = null
        val EMPLOYEES = 1
        val EMPLOYEE_ID = 2
        val uriMatcher: UriMatcher? = null
        val DATABASE_NAME = "HR"
        val EMPLOYEES_TABLE_NAME = "employees"
        val DATABASE_VERSION = 1
        val CREATE_DB_TABLE = " CREATE TABLE " + EMPLOYEES_TABLE_NAME +
                " (_id INTEGER PRIMARY KEY ISAUTOINCREMENT, " + " name TEXT NOT NULL, " +
                " salary INTEGER NOT NULL);"
        private var sUriMatcher = UriMatcher(UriMatcher.NO_MATCH);
        init
        {
            sUriMatcher.addURI(PROVIDER_NAME, "students", EMPLOYEES);
            sUriMatcher.addURI(PROVIDER_NAME, "students/#", EMPLOYEE_ID);
        }

    }
    private var db: SQLiteDatabase? = null
    private class DatabaseHelper internal constructor(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_DB_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS " + EMPLOYEES_TABLE_NAME)
            onCreate(db)
        }
    }
    override fun onCreate(): Boolean {
        val context = context
        val dbHelper = DatabaseHelper(context)
          //Create a write able database which will trigger its
          //creation if it doesn't already exist.
        db = dbHelper.writableDatabase
        return if (db == null) false else true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        //Add a new student record
        val rowID = db!!.insert(EMPLOYEES_TABLE_NAME, "", values)
        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            val _uri = ContentUris.withAppendedId(CONTENT_URI, rowID)
            context!!.contentResolver.notifyChange(_uri, null)
            return _uri
        }
        throw SQLException("Failed to add a record into $uri")
    }
    override fun query(
        uri: Uri, projection: Array<String>?,
        selection: String?, selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        var sortOrder = sortOrder
        val qb = SQLiteQueryBuilder()
        qb.tables = EMPLOYEES_TABLE_NAME
        if (uriMatcher != null) {
            when (uriMatcher.match(uri)) {
                /* STUDENTS -> qb.projectionMap =
                    STUDENTS_PROJECTION_MAP */
                EMPLOYEE_ID -> qb.appendWhere(_ID + "=" + uri.pathSegments[1])
                else -> {
                    null
                }
            }
        }
        if (sortOrder == null || sortOrder === "") {
            /**
             * By default sort on student names
             */
            sortOrder = NAME
        }
        val c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        /**
         * register to watch a content URI for changes  */
        c.setNotificationUri(context!!.contentResolver, uri)
        return c }
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int { return 0 }
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?
    ): Int { return 0 }
    override fun getType(uri: Uri): String? {
        /*when (uriMatcher!!.match(uri)) {
            EMPLOYEES -> return "vnd.android.cursor.dir/vnd.example.employees"
            EMPLOYEE_ID -> return "vnd.android.cursor.item/vnd.example.employees"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }*/
        return "String"
    }
}