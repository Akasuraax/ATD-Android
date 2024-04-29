package com.example.atdapp

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var button = view.findViewById<Button>(R.id.button)

        button.setOnClickListener {
            showSuccessDialog()
        }
    }

    private fun showSuccessDialog() {

        val successConstraintLayout = view?.findViewById<ConstraintLayout>(R.id.successConstraintLayout)
        val inflater = LayoutInflater.from(requireActivity())
        val dialogView = inflater.inflate(R.layout.sucess_dialog, successConstraintLayout, false)
        val successDone = dialogView.findViewById<Button>(R.id.successDone)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(dialogView)
        val alertDialog = builder.create()

        successDone.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }
}