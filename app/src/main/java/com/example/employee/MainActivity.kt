package com.example.employee
import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun onClickAddName(view: View?) {
// Add a new student record
        val values = ContentValues()
        values.put(
            EmployeeProvider.NAME,
            (findViewById<View>(R.id.editText2) as EditText).text.toString()
        )
        values.put(
            EmployeeProvider.SALARY,
            (findViewById<View>(R.id.editText3) as EditText).text.toString()
        )
        val uri = contentResolver.insert(
            EmployeeProvider.CONTENT_URI, values
        )

        Toast.makeText(baseContext, uri.toString(), Toast.LENGTH_LONG).show()
    }
    fun onClickRetrive(view: View?) {
        // Retrieve student records
        val URL = "content://com.example.employee.EmployeeProvider"
        val employees = Uri.parse(URL)
        val x: String = (findViewById<View>(R.id.editText2) as EditText).text.toString()
        var c = contentResolver.query(employees, null, "name LIKE ?",
            Array(1){"%$x%"}, null)
        //val col = arrayOf(EmployeeProvider.NAME)
        //val input_name: EditText = findViewById(R.id.text)
        //val c = contentResolver.query(EmployeeProvider.CONTENT_URI, col, "${col[0]} LIKE ?", arrayOf(x), col[0])
        //var c = contentResolver.query(EmployeeProvider.CONTENT_URI, null, "LIKE '%" + x + "%'", null, null)
        //val //c = managedQuery(students, null, null, null, "name")
        if (c != null) {
            if (c?.moveToFirst()) {
                do {

                    Toast.makeText(this,
                        c.getString(c.getColumnIndex(EmployeeProvider._ID)) + ", " + c.getString(c.getColumnIndex(
                            EmployeeProvider.NAME)) + ", " + c.getString(c.getColumnIndex(
                            EmployeeProvider.SALARY)),
                        Toast.LENGTH_SHORT).show()
                } while (c.moveToNext())
            }
        }
    }
}