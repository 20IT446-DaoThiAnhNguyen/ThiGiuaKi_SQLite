package com.example.sqlite

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private lateinit var edName: EditText
    private lateinit var edEmail: EditText
    private lateinit var edContact: EditText
    private lateinit var edAddress: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnView: Button
    private lateinit var btnUpdate: Button

    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter: StudentAdapter? = null
    private var std: StudentModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initRecyclerView()
        sqliteHelper = SQLiteHelper(this)

        btnAdd.setOnClickListener { addStudent() }
        btnView.setOnClickListener { getStudent() }
        btnUpdate.setOnClickListener { updateStudent() }
        //Delete

        adapter?.setOnClickItem {
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            // update
            edName.setText(it.name)
            edEmail.setText(it.email)
            edContact.setText(it.contact)
            edAddress.setText(it.address)
            std = it
        }

        adapter?.setOnClickDeleteItem{
            deleteStudent(it.id)
        }
    }
    private fun getStudent() {
        val stdList = sqliteHelper.getAllStudent()
        Log.e("ppp", "${stdList.size}")

        // display data in RecyclerView
        adapter?.addItems(stdList)

    }

    private fun addStudent(){
        val name = edName.text.toString()
        val email = edEmail.text.toString()
        val contact = edContact.text.toString()
        val address = edAddress.text.toString()

        if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Plaese enter requied field", Toast.LENGTH_SHORT).show()
        }else {
            val std = StudentModel(name = name, email = email, contact = contact, address = address)
            val status = sqliteHelper.insertStudent(std)

            if(status > -1) {
                Toast.makeText(this, "Student Added...", Toast.LENGTH_SHORT).show()
                clearEditText()
                getStudent()
            }else{
                Toast.makeText(this, "Record not saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStudent() {
        val name = edName.text.toString()
        val email = edEmail.text.toString()
        val contact = edContact.text.toString()
        val address = edAddress.text.toString()

        //check record not change
        if (name == std?.name && email == std?.email && contact == std?.contact && address == std?.address) {
            Toast.makeText(this, "Record not changed...", Toast.LENGTH_SHORT).show()
            return
        }
        if (std == null) return
        val std = StudentModel(id = std!!.id, name = name, email = email, contact = contact, address = address)
        val status = sqliteHelper.updateStudent(std)
        if (status > -1) {
            clearEditText()
            getStudent()
        }else{
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteStudent(id:Int){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete item?")
        builder.setCancelable(true)
        builder.setPositiveButton("Yes"){ dialog, _ ->
            sqliteHelper.deleteStudentById(id)
            getStudent()
            dialog.dismiss()
        }
        builder.setNegativeButton("No"){ dialog, _ ->

            dialog.dismiss()
        }
        var alert = builder.create()
        alert.show()
    }

    private fun clearEditText() {
        edName.setText("")
        edEmail.setText("")
        edContact.setText("")
        edAddress.setText("")
        edName.requestFocus()
    }

    private fun initRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentAdapter()
        recyclerView.adapter = adapter
    }
    private fun initView() {
        edName = findViewById(R.id.edName)
        edEmail = findViewById(R.id.edEmail)
        edContact = findViewById(R.id.edContact)
        edAddress = findViewById(R.id.edAddress)
        btnAdd = findViewById(R.id.btnAdd)
        btnView = findViewById(R.id.btnView)
        btnUpdate = findViewById(R.id.btnUpdate)
        recyclerView = findViewById(R.id.recyclerView)
    }
}