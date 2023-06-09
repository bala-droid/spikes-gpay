package com.example.upi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import java.util.ArrayList
import java.util.Locale
import java.util.Random

class MainActivity : AppCompatActivity() {
    lateinit var amount: EditText
    lateinit var upiid: EditText
    lateinit var name: EditText
    lateinit var note: EditText
    lateinit var send: AppCompatButton
    val Payment = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        send.setOnClickListener {
            val money = amount.text.toString()
            val note = note.text.toString()
            val username = name.text.toString()
            val id = upiid.text.toString()
        }

    }

    val r = Random()

    fun upiPayment(money: String, note: String, username: String, id: String){
        val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", id)
            .appendQueryParameter("pn", username)
            .appendQueryParameter("tn", note)
            .appendQueryParameter("am", money)
            .appendQueryParameter("cu", "INR")
            .build()

        val upiintent = Intent(Intent.ACTION_VIEW)
        upiintent.data = uri

        val choose = Intent.createChooser(upiintent, "Pay")
        if(null!=choose.resolveActivity(packageManager)){
            startActivityForResult(choose, Payment)
        }
        else{
            Toast.makeText(this, "Application not found", Toast.LENGTH_LONG).show()
        }

    }

    fun isConnectionAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        if (netInfo != null
            && netInfo.isConnected
            && netInfo.isAvailable) {
            return true
        }
        return false
    }

    fun upiPaymentDataOperation(data: ArrayList<String>){
        if(isConnectionAvailable(this@MainActivity)){
            if(data.isEmpty()) {
                Toast.makeText(this, "Payment has been Cancelled", Toast.LENGTH_SHORT).show()
            }
            else{
                val str = data[0].toString()
                var cancel = ""
                var status = ""
                var approvalRefNo = ""
                val response = str.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (i in response.indices) {
                    val equalStr = response[i].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if(equalStr.size >= 2) {
                        if(equalStr[0].equals("Status")){
                            status = equalStr[1].toLowerCase(Locale.ROOT)
                        }
                        else if (equalStr[0].equals("ApprovalRefNo", ignoreCase = true) || equalStr[0].equals(
                                "txnRef", ignoreCase = true))
                        {
                            approvalRefNo = equalStr[1]
                        }
                        else {
                            cancel = "Payment cancelled - user"
                        }
                    }
                }

                if (status == "success") {
                    Toast.makeText(this@MainActivity, "Payment Successful", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this@MainActivity, "Payment Failed", Toast.LENGTH_LONG).show()
                }
            }
        }
        else{
            Toast.makeText(this@MainActivity, "No Internet", Toast.LENGTH_LONG).show()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Payment -> if (Activity.RESULT_OK == resultCode) {
                if (data != null){
                    val txt = data.getStringExtra("response")  //passing out the response
                    val dList = ArrayList<String>()     //responses getting stored
                    if(txt!=null){
                        dList.add(txt)
                    }

                }
            }
        }
    }
}


