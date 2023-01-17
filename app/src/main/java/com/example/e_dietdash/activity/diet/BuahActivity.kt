package com.example.e_dietdash.activity.diet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_dietdash.R
import com.example.e_dietdash.`object`.Const
import com.example.e_dietdash.`object`.CustomDialog
import com.example.e_dietdash.activity.MainActivity
import com.example.e_dietdash.adapter.BuahAdapter
import com.example.e_dietdash.model.Eaten
import com.example.e_dietdash.model.Gizi
import com.example.e_dietdash.model.NutrientData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class BuahActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()
    private val mUsersCollection = mFirestore.collection(Const.DATA)
    val user = auth.currentUser
    val userId = user?.uid
    lateinit var adapter: BuahAdapter
    var weight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buah)

        val kirim = findViewById<Button>(R.id.kirim)
        val query: Query = FirebaseFirestore.getInstance().collection(Const.FOOD).whereEqualTo("category", Const.BUAH)
        val recyclerView: RecyclerView = findViewById(R.id.rv_buah)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = BuahAdapter(query)
        recyclerView.adapter = adapter

        kirim.setOnClickListener {
            var natrium = 0
            var kalium = 0
            var serat = 0
            var lemak = 0

            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val current = formatter.format(time)
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            val docRef = db.collection("data").whereEqualTo("userId", userId).whereEqualTo("date", current)
            docRef.get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val data = document.data
                        kalium = data["kalium"] as Int
                        natrium = data["natrium"] as Int
                        serat = data["serat"] as Int
                        lemak = data["lemak"] as Int
                    }
                }
            }

            Toast.makeText(this, Eaten.nama, Toast.LENGTH_SHORT).show()
            Toast.makeText(this, Eaten.nama, Toast.LENGTH_SHORT).show()
            natrium += NutrientData.natrium
            kalium += NutrientData.kalium
            serat += NutrientData.serat
            lemak += NutrientData.lemak
            setData(userId, natrium, kalium, serat, lemak)
        }
    }

    private fun setData(strId: String?, natrium: Int?, kalium: Int?, serat: Int?, lemak: Int?) {
        createUser(strId, natrium, kalium, serat, lemak).addOnCompleteListener {
            if (it.isSuccessful) {
                NutrientData.natrium = 0
                NutrientData.lemak = 0
                NutrientData.serat = 0
                NutrientData.kalium = 0
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                CustomDialog.hideLoading()
                Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            CustomDialog.hideLoading()
            Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createUser(strId: String?, natrium: Int?, kalium: Int?, serat: Int?, lemak: Int?): Task<Void> {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val current = formatter.format(time)
        val writeBatch = mFirestore.batch()
        val ids = "$strId-$current"
        val data = Gizi(strId, natrium, kalium, serat, lemak, current)
        writeBatch.set(mUsersCollection.document(ids), data)
        return writeBatch.commit()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.startListening()
    }
}