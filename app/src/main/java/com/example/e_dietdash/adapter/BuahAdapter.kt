package com.example.e_dietdash.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.e_dietdash.R
import com.example.e_dietdash.model.Buah
import com.example.e_dietdash.model.Eaten
import com.example.e_dietdash.model.NutrientData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

class BuahAdapter(query: Query) : FirestoreAdapter<BuahAdapter.BuahViewHolder>(query) {
    class BuahViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nama: TextView = itemView.findViewById(R.id.nama)
        private val satuan: TextView = itemView.findViewById(R.id.satuan)
        private val check: CheckBox = itemView.findViewById(R.id.check)
        private val view: ConstraintLayout = itemView.findViewById(R.id.view)
        private val input: EditText = itemView.findViewById(R.id.input)

        fun bind(snapshot: DocumentSnapshot) {
            view.visibility = View.GONE
            val buah: Buah? = snapshot.toObject(Buah::class.java)
            nama.text = buah?.name.toString()
            satuan.text = buah?.satuan.toString()
            check.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    view.visibility = View.VISIBLE
                }
                if (!isChecked) {
                    view.visibility = View.GONE
                    input.text = null
                }
            }
            val test = input.text.toString()
            if (test != ""){
                    NutrientData.natrium += buah!!.natrium!! * input.text.toString().toInt()/100
                    NutrientData.kalium += buah.kalium!! * input.text.toString().toInt()/100
                    NutrientData.serat += buah.serat!! * input.text.toString().toInt()/100
                    NutrientData.lemak += buah.lemak!! * input.text.toString().toInt()/100
                    Eaten.nama = buah.name
                    Eaten.weight += Eaten.weight + "," + input
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuahViewHolder {
        return BuahViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_diet, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BuahViewHolder, position: Int) {
        getSnapshot(position)?.let { snapshot -> holder.bind(snapshot) }
    }
}