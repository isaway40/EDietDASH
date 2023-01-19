package com.example.e_dietdash.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.e_dietdash.R
import com.example.e_dietdash.model.Buah
import com.example.e_dietdash.model.Edit
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

class EditAdapter(query: Query) : FirestoreAdapter<EditAdapter.EditViewHolder>(query) {
    class EditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val qualityConsumed: TextView = itemView.findViewById(R.id.quantity_consumed)
        val quality: TextView = itemView.findViewById(R.id.quantity)
        val edit: Button = itemView.findViewById(R.id.btn_edit)
        val kirim: Button = itemView.findViewById(R.id.kirim)
        val delete: Button = itemView.findViewById(R.id.btn_delete)
        val satuan: TextView = itemView.findViewById(R.id.satuan)
        val edit_consumed: EditText = itemView.findViewById(R.id.edit_comsumed)
        val name_consumed: TextView = itemView.findViewById(R.id.name_consumed)
        var snapshot: DocumentSnapshot? = null

        fun bind(snapshot: DocumentSnapshot) {
            this.snapshot = snapshot
            val edits: Edit? = snapshot.toObject(Edit::class.java)
            name_consumed.text = edits?.nama.toString()
            satuan.text = edits?.weight.toString()

            edit.setOnClickListener {
                edit.visibility = View.GONE
                delete.visibility = View.GONE
                quality.visibility = View.GONE
                qualityConsumed.visibility = View.GONE
                kirim.visibility = View.VISIBLE
                satuan.visibility = View.VISIBLE
                edit_consumed.visibility = View.VISIBLE
            }
            kirim.setOnClickListener {
                edit.visibility = View.VISIBLE
                delete.visibility = View.VISIBLE
                quality.visibility = View.VISIBLE
                qualityConsumed.visibility = View.VISIBLE
                kirim.visibility = View.GONE
                satuan.visibility = View.GONE
                edit_consumed.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditViewHolder {
        return EditViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_consumed, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EditViewHolder, position: Int) {
        getSnapshot(position)?.let { snapshot -> holder.bind(snapshot) }
    }
}