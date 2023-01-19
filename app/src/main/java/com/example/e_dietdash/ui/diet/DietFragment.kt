package com.example.e_dietdash.ui.diet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.e_dietdash.R
import com.example.e_dietdash.`object`.Const
import com.example.e_dietdash.activity.diet.BuahActivity
import com.example.e_dietdash.activity.MainActivity
import com.example.e_dietdash.activity.diet.BuahBActivity
import com.example.e_dietdash.activity.diet.EditActivity
import com.example.e_dietdash.databinding.FragmentDietBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DietFragment : Fragment() {

    private var binding: FragmentDietBinding? = null
    private lateinit var viewPager: ViewPager

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_diet, container, false)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val userId = user?.uid
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val current = formatter.format(time)
        val db = FirebaseFirestore.getInstance()
        val nestedRef = db.collection(Const.PATH_COLLECTION).document(userId.toString()).collection(Const.GIZI).document(current)

        nestedRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val natrium = documentSnapshot.get("natrium").toString()
                val kalium = documentSnapshot.get("kalium").toString()
                val serat = documentSnapshot.get("serat").toString()
                val lemak = documentSnapshot.get("lemak").toString()

                val edit = view.findViewById<ImageButton>(R.id.btn_more)
                edit.setOnClickListener {
                    val intent = Intent (activity, EditActivity::class.java)
                    intent.putExtra("natrium", natrium);
                    intent.putExtra("kalium", kalium);
                    intent.putExtra("serat", serat);
                    intent.putExtra("lemak", lemak);
                    activity?.startActivity(intent)
                }

                val linearLayout = view.findViewById<CardView>(R.id.to_buah)
                linearLayout.setOnClickListener {
                    val intent = Intent (activity, BuahActivity::class.java)
                    intent.putExtra("natrium", natrium);
                    intent.putExtra("kalium", kalium);
                    intent.putExtra("serat", serat);
                    intent.putExtra("lemak", lemak);
                    activity?.startActivity(intent)
                }

                val linearLayoutb = view.findViewById<CardView>(R.id.to_buahb)
                linearLayoutb.setOnClickListener {
                    val intent = Intent (activity, BuahBActivity::class.java)
                    intent.putExtra("natrium", natrium);
                    intent.putExtra("kalium", kalium);
                    intent.putExtra("serat", serat);
                    intent.putExtra("lemak", lemak);
                    activity?.startActivity(intent)
                }
                Toast.makeText(requireContext(), natrium.toString(), Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
        }

        val lmmenu = LinearLayoutManager(activity)
        lmmenu.orientation = LinearLayoutManager.HORIZONTAL
        val lmnutrition = LinearLayoutManager(activity)
        lmnutrition.orientation = LinearLayoutManager.VERTICAL

        val mainActivity = activity as MainActivity
        viewPager = mainActivity.findViewById(R.id.viewPager)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}