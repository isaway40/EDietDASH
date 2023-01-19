package com.example.e_dietdash.activity.diet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_dietdash.R
import com.example.e_dietdash.`object`.Const
import com.example.e_dietdash.adapter.EditAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val userId = user?.uid
    val time = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    val current = formatter.format(time)
    lateinit var adapter: EditAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_consumed)
        val query: Query = FirebaseFirestore.getInstance().collection(Const.PATH_COLLECTION).document(userId.toString()).collection(
            Const.GIZI).document(current).collection(Const.MAKAN)

        val recyclerView: RecyclerView = findViewById(R.id.rv_consumed)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = EditAdapter(query)
        recyclerView.adapter = adapter

    }
}