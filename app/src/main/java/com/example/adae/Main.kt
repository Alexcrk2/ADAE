@file:Suppress("DEPRECATION")
package com.example.adae

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kpstv.library.Paypal

class Main: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment())
            .commit()
    }
}

class MainFragment : Fragment(R.layout.activity_shop2) {
    private lateinit var paypal: Paypal

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val options = Paypal.Options(
            paypalButtonId = "LE4DPD9LCBG3U",
            purchaseCompleteUrl = "https://www.pokemon.com/es/jcc-pokemon/jugar-en-linea/",
            isSandbox = true // Para aceptar pagos reales hay que cambiarlo a false
        )

        paypal = Paypal.Builder(options)
            .setCallingContext(this)

        val button = view.findViewById<Button>(R.id.btn_checkout)
        button.setOnClickListener {
            paypal.checkout()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (paypal.isPurchaseComplete(requestCode, resultCode)) {
            /**
             * Called when whole payment process is completed
             * This will called after onCheckOutComplete.
             *
             * You can also fetch purchase details from [Intent]
             */
            val details = data?.getSerializableExtra(Paypal.PURCHASE_DATA) as? Paypal.History
            AlertDialog.Builder(requireContext())
                .setTitle("Comprada completada")
                .setMessage("Pago completado para la cuenta  ${details?.email} con Id: ${paypal.options.paypalButtonId}")
                .setPositiveButton("Todo OK", null)
                .show()

        } else if (paypal.isPurchaseCancelled(requestCode, resultCode)) {
            /**
             * Cuando el usuario cancela el pago sale este cartel
             *
             */
            Toast.makeText(requireContext(), "Compra cancelada por el usuario", Toast.LENGTH_SHORT).show()
        }
    }
}