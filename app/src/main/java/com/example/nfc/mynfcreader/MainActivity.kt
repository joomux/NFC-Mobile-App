package com.example.nfc.mynfcreader

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.provider.Settings.ACTION_NFC_SETTINGS
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.nfc.mynfcreader.utils.Utils
import com.example.nfc.mynfcreader.parser.NdefMessageParser
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.os.Parcelable
import android.util.Log;
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject




import android.content.Context
//import android.support.v7.app.AppCompatActivity
import android.os.VibrationEffect
import android.os.Build
import android.os.Vibrator

class MainActivity : Activity() {
    private var nfcAdapter: NfcAdapter? = null
    // launch our application when a new Tag or Card will be scanned
    private var pendingIntent: PendingIntent? = null
    // display the data read
    private var text: TextView? = null
    private var displayName: TextView? = null
    //private val client = OkHttpClient()
    public var token: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text = findViewById<View>(R.id.text) as TextView
        displayName = findViewById<View>(R.id.displayName) as TextView
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        sendPost()


        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                Intent(this, this.javaClass)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
    }


    fun sendPost() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://cx2u-api.herokuapp.com/api-token/"

        var mRequestQueue = Volley.newRequestQueue(this)

        val requestJsonPayloadMap = mutableMapOf<String, String>()
        requestJsonPayloadMap["username"] = "api_user"
        requestJsonPayloadMap["password"] = "Demo1234"

        // Creating JSON Object out of the Hash-map
        val requestJSONObject = JSONObject(requestJsonPayloadMap)

        val volleyEnrollRequest = object : JsonObjectRequest(Request.Method.POST, url, requestJSONObject,
                Response.Listener {
                    // Success Part
                    println("success!!!!!!!!")
                    println(it["token"])
                    token = it["token"].toString()
                },

                Response.ErrorListener {
                    // Failure Part
                    println("fail!!!!!!!!")
                }
        ) {
            // Providing Request Headers

            override fun getHeaders(): Map<String, String> {
                // Create HashMap of your Headers as the example provided below

                val headers = HashMap<String, String>()

                return headers
            }

            // Either override the below method or pass the payload as parameter above, dont do both

            override fun getParams(): Map<String, String> {
                // Create HashMap of your params as the example provided below

                val body = HashMap<String, String>()
                body["Content-Type"] = "application/json; charset=utf-8"

                return body
            }
        }

        mRequestQueue!!.add(volleyEnrollRequest!!)
    }

//    fun sendGet() {
//        val queue = Volley.newRequestQueue(this)
//        val url = "https://cx2u-api.herokuapp.com/register/contact/"
//
//        var mRequestQueue = Volley.newRequestQueue(this)
//
//        val requestJsonPayloadMap = mutableMapOf<String, String>()
//        requestJsonPayloadMap["Authorization"] = "token " + token
//        requestJsonPayloadMap["password"] = "Demo1234"
//
//        // Creating JSON Object out of the Hash-map
//        val requestJSONObject = JSONObject(requestJsonPayloadMap)
//
//        val volleyEnrollRequest = object : JsonObjectRequest(Request.Method.GET, url, requestJSONObject,
//                Response.Listener {
//                    // Success Part
//                    println("success!!!!!!!!")
//                    println(it["token"])
//                },
//
//                Response.ErrorListener {
//                    // Failure Part
//                    println("fail!!!!!!!!")
//                }
//        ) {
//            // Providing Request Headers
//
//            override fun getHeaders(): Map<String, String> {
//                // Create HashMap of your Headers as the example provided below
//
//                val headers = HashMap<String, String>()
//
//                return headers
//            }
//
//            // Either override the below method or pass the payload as parameter above, dont do both
//
//            override fun getParams(): Map<String, String> {
//                // Create HashMap of your params as the example provided below
//
//                val body = HashMap<String, String>()
//                body["Content-Type"] = "application/json; charset=utf-8"
//
//                return body
//            }
//        }
//
//        mRequestQueue!!.add(volleyEnrollRequest!!)
//    }


    override fun onResume() {
        super.onResume()

        val nfcAdapterRefCopy = nfcAdapter
        if (nfcAdapterRefCopy != null) {
            if (!nfcAdapterRefCopy.isEnabled())
                showNFCSettings()

            nfcAdapterRefCopy.enableForegroundDispatch(this, pendingIntent, null, null)
        }
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        resolveIntent(intent)
    }

    private fun showNFCSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show()
        val intent = Intent(ACTION_NFC_SETTINGS)
        startActivity(intent)
    }



    fun Context.vibrate(milliseconds:Long = 500){
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Check whether device/hardware has a vibrator
        val canVibrate:Boolean = vibrator.hasVibrator()

        if(canVibrate){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                // void vibrate (VibrationEffect vibe)
                vibrator.vibrate(
                        VibrationEffect.createOneShot(
                                milliseconds,
                                // The default vibration strength of the device.
                                VibrationEffect.DEFAULT_AMPLITUDE
                        )
                )
            }else{
                // This method was deprecated in API level 26
                vibrator.vibrate(milliseconds)
            }
        }
    }









    /**
     * Tag data is converted to string to display
     *
     * @return the data dumped from this tag in String format
     */
    private fun dumpTagData(tag: Tag): String {
        var isSend = true;
        val sb = StringBuilder()
        val id = tag.getId()
        //vibrate here
        vibrate()
        var title = StringBuilder()
        displayName?.setText("")
        title.append("Please Wait\n")
        displayName?.setText(title.toString())


        println("data has been dumped")

        if(isSend){
            isSend = false;
            sendToSalesforce(Utils.toReversedHex(id));
        }



        sb.append("ID (hex): ").append(Utils.toHex(id)).append('\n')
        sb.append("ID (reversed hex): ").append(Utils.toReversedHex(id)).append('\n')
        sb.append("ID (dec): ").append(Utils.toDec(id)).append('\n')
        sb.append("ID (reversed dec): ").append(Utils.toReversedDec(id)).append('\n')

        val prefix = "android.nfc.tech."
        sb.append("Technologies: ")
        for (tech in tag.getTechList()) {
            sb.append(tech.substring(prefix.length))
            sb.append(", ")
        }

        sb.delete(sb.length - 2, sb.length)

        for (tech in tag.getTechList()) {
            if (tech == MifareClassic::class.java.name) {
                sb.append('\n')
                var type = "Unknown"

                try {
                    val mifareTag = MifareClassic.get(tag)

                    when (mifareTag.type) {
                        MifareClassic.TYPE_CLASSIC -> type = "Classic"
                        MifareClassic.TYPE_PLUS -> type = "Plus"
                        MifareClassic.TYPE_PRO -> type = "Pro"
                    }
                    sb.append("Mifare Classic type: ")
                    sb.append(type)
                    sb.append('\n')

                    sb.append("Mifare size: ")
                    sb.append(mifareTag.size.toString() + " bytes")
                    sb.append('\n')

                    sb.append("Mifare sectors: ")
                    sb.append(mifareTag.sectorCount)
                    sb.append('\n')

                    sb.append("Mifare blocks: ")
                    sb.append(mifareTag.blockCount)
                } catch (e: Exception) {
                    sb.append("Mifare classic error: " + e.message)
                }

            }

            if (tech == MifareUltralight::class.java.name) {
                sb.append('\n')
                val mifareUlTag = MifareUltralight.get(tag)
                var type = "Unknown"
                when (mifareUlTag.type) {
                    MifareUltralight.TYPE_ULTRALIGHT -> type = "Ultralight"
                    MifareUltralight.TYPE_ULTRALIGHT_C -> type = "Ultralight C"
                }
                sb.append("Mifare Ultralight type: ")
                sb.append(type)
            }
        }

        return sb.toString()
    }

    private fun resolveIntent(intent: Intent) {
        val action = intent.action

        if (NfcAdapter.ACTION_TAG_DISCOVERED == action
                || NfcAdapter.ACTION_TECH_DISCOVERED == action
                || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

            if (rawMsgs != null) {
                Log.i("NFC", "Size:" + rawMsgs.size);
                val ndefMessages: Array<NdefMessage> = Array(rawMsgs.size, { i -> rawMsgs[i] as NdefMessage });
                displayNfcMessages(ndefMessages)
            } else {
                val empty = ByteArray(0)
                val id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
                val tag = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag
                val payload = dumpTagData(tag).toByteArray()
                val record = NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload)
                //val emptyMsg = NdefMessage(arrayOf(record))
                //val emptyNdefMessages: Array<NdefMessage> = arrayOf(emptyMsg);
                //displayNfcMessages(emptyNdefMessages)
            }
        }
    }

    private fun sendToSalesforce(hex: String) {
        var hexNoSpace = hex.replace("\\s".toRegex(), "").toUpperCase()
        println(hexNoSpace)
        var userStr = StringBuilder()
        var title = StringBuilder()
        var url = "https://cx2u-api.herokuapp.com/register/contact/" + hexNoSpace

        var mRequestQueue = Volley.newRequestQueue(this)

        val requestJsonPayloadMap = mutableMapOf<String, String>()
        // Creating JSON Object out of the Hash-map
        val requestJSONObject = JSONObject(requestJsonPayloadMap)

        val volleyEnrollRequest = object : JsonObjectRequest(Request.Method.GET, url, requestJSONObject,
                Response.Listener {
                    // Success Part
                    println("success!!!!!!!! with token")
                    println(it)

                    title.append("Welcome\n")
                    title.append(it["name"])
                    userStr.append("\n")
                    userStr.append(it["email"])
                    userStr.append("\n")
                    userStr.append("Reference ID: ")
                    userStr.append(it["reference_id"])

                    text?.setText(userStr.toString())
                    displayName?.setText(title.toString())


                },

                Response.ErrorListener {
                    // Failure Part
                    println("fail!!!!!!!!")
                    println(it)

                    text?.setText("")
                    displayName?.setText("")

                    if(it.toString() == "com.android.volley.AuthFailureError"){
                        sendPost()
                        userStr.append("Sorry, please try tapping again in a few seconds")

                    }
                    else if(it.toString() == "com.android.volley.ClientError"){
                        userStr.append("Sorry, we could not recognise this identification card")
                    }
                    else{
                        userStr.append("Sorry, an unknown error occurred")
                    }

                    text?.setText(userStr.toString())
                }
        ) {
            // Providing Request Headers

            override fun getHeaders(): Map<String, String> {
                // Create HashMap of your Headers as the example provided below

                val headers = HashMap<String, String>()
                    headers["Authorization"] = "Token " + token // b90a82bcfd22f48abca35a892e854f197ef768eb"

                return headers
            }

            // Either override the below method or pass the payload as parameter above, dont do both

            override fun getParams(): Map<String, String> {
                // Create HashMap of your params as the example provided below

                val body = HashMap<String, String>()
                body["Content-Type"] = "application/json; charset=utf-8"

                return body
            }
        }

        mRequestQueue!!.add(volleyEnrollRequest!!)
    }


    private fun displayNfcMessages(msgs: Array<NdefMessage>?) {
        if (msgs == null || msgs.isEmpty())
            return

        val builder = StringBuilder()
        val records = NdefMessageParser.parse(msgs[0])
        val size = records.size

        for (i in 0 until size) {
            val record = records[i]
            val str = record.str()
            builder.append(str).append("\n")
        }

//        text?.setText(builder.toString())
    }
}
