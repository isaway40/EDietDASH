package com.example.e_dietdash.activity.diet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_dietdash.R
import com.example.e_dietdash.`object`.Const
import com.example.e_dietdash.`object`.Const.GIZI
import com.example.e_dietdash.`object`.Const.MAKAN
import com.example.e_dietdash.`object`.Const.PATH_COLLECTION
import com.example.e_dietdash.`object`.CustomDialog
import com.example.e_dietdash.activity.MainActivity
import com.example.e_dietdash.adapter.BuahAdapter
import com.example.e_dietdash.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*
class SayurB : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val userId = user?.uid
    val time = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    val current = formatter.format(time)
    lateinit var adapter: BuahAdapter
    private val mFirestore = FirebaseFirestore.getInstance()
    private val mUsersCollection = mFirestore.collection(PATH_COLLECTION)
    val userRef = mUsersCollection.document(userId.toString())
    val ordersRef = userRef.collection(GIZI)
    val makanRef = FirebaseFirestore.getInstance().collection(PATH_COLLECTION).document(userId.toString()).collection(GIZI).document(current).collection(MAKAN)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sayur_b)

        val kirim = findViewById<Button>(R.id.kirimsB)
        val query: Query = FirebaseFirestore.getInstance().collection(Const.FOOD)
            .whereEqualTo("category", Const.SAYUR_B)
        val recyclerView: RecyclerView = findViewById(R.id.rv_sayurB)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = BuahAdapter(query)
        recyclerView.adapter = adapter

        var natrium = 0
        var kalium = 0
        var serat = 0
        var lemak = 0

        natrium = intent.getStringExtra("natrium")!!.toInt();
        kalium = intent.getStringExtra("kalium")!!.toInt();
        serat = intent.getStringExtra("serat")!!.toInt();
        lemak = intent.getStringExtra("lemak")!!.toInt();

        kirim.setOnClickListener {
            var loop = 0
            var first = 0
            loop = recyclerView.childCount.toString().toInt()
            for (i in 0 until recyclerView.childCount) {
                val viewHolder =
                    recyclerView.getChildViewHolder(recyclerView.getChildAt(i)) as BuahAdapter.BuahViewHolder
                val inputValue = viewHolder.input.text.toString()
                if (inputValue != "") {
                    val buah = viewHolder.snapshot?.toObject(Buah::class.java)
                    NutrientData.natrium += buah!!.natrium!! * inputValue.toInt() / buah.weight!!.toInt()
                    NutrientData.kalium += buah.kalium!! * inputValue.toInt() / buah.weight!!.toInt()
                    NutrientData.serat += buah.serat!! * inputValue.toInt() / buah.weight!!.toInt()
                    NutrientData.lemak += buah.lemak!! * inputValue.toInt() / buah.weight!!.toInt()
                    if (first == 0) {
                        Eaten.nama = "nama makanan" + "," + buah.name
                        Eaten.weight += "berat makanan,$inputValue"
                        first = 1
                    }
                    Eaten.nama = Eaten.nama + "," + buah.name
                    Eaten.weight += Eaten.weight + "," + inputValue
                    loop - 1
                    if (loop == 0) {
                        break
                    }
                }
            }

            var loops = 0
            loops = recyclerView.childCount.toString().toInt()
            for (i in (recyclerView.childCount - 1) downTo 0) {

                var lastMakan = ""
                var lastNama = ""
                val dataNama = Eaten.nama
                val namaArray = dataNama?.split(",")
                Log.e("lopp", namaArray.toString())
                if (namaArray != null) {
                    lastNama = namaArray.get(namaArray.size - loops)
                }
                val dataMakan = Eaten.weight
                val makanArray = dataMakan?.split(",")
                Log.e("lopp", makanArray.toString())
                if (makanArray != null) {
                    lastMakan = makanArray.get(makanArray.size - loops)
                }
                setEat(lastNama, lastMakan)
                loops -= 1
                if (i == 0) {
                    break
                }
            }

            natrium += NutrientData.natrium
            kalium += NutrientData.kalium
            serat += NutrientData.serat
            lemak += NutrientData.lemak
            setData(natrium, kalium, serat, lemak)
        }
    }

    private fun setEat(nama: String?, weight: String?) {
        createKonsumsi(nama, weight).addOnCompleteListener {
            if (it.isSuccessful) {
                Eaten.nama = ""
                Eaten.weight = ""
            } else {
                CustomDialog.hideLoading()
                Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            CustomDialog.hideLoading()
            Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createKonsumsi(nama: String?, weight: String?): Task<Void> {
        val writeBatch = mFirestore.batch()
        if (nama != null && nama.isNotEmpty() && weight != null && weight.isNotEmpty()) {
            val data = Eat(nama, weight)
            writeBatch.set(makanRef.document(nama.toString()), data)
        }
        return writeBatch.commit()
    }

    private fun setData(natrium: Int?, kalium: Int?, serat: Int?, lemak: Int?) {
        createUser(natrium, kalium, serat, lemak).addOnCompleteListener {
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

    private fun createUser(natrium: Int?, kalium: Int?, serat: Int?, lemak: Int?): Task<Void> {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val current = formatter.format(time)
        val writeBatch = mFirestore.batch()
        val ids = current
        val data = Gizi(natrium, kalium, serat, lemak, current)
        writeBatch.set(ordersRef.document(ids), data)
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