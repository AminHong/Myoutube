package com.amin.myoutube

import android.Manifest
import android.accounts.AccountManager
import android.os.Bundle
import android.content.Intent
import android.os.AsyncTask
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bumptech.glide.load.engine.bitmap_recycle.IntegerArrayAdapter
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeScopes
import com.google.api.services.youtube.model.Playlist
import com.google.api.services.youtube.model.PlaylistListResponse
import pub.devrel.easypermissions.EasyPermissions
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks{

    companion object {
        const val REQUEST_ACCOUNT_PICKER = 1000
        const val REQUEST_AUTHORIZATION = 1001
        const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
        const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003

        lateinit var THIS_IS: MainActivity

        fun onResult(result: List<Playlist>?){
            THIS_IS.getPlayLists(result)
        }

        lateinit var mService: YouTube
    }

    private lateinit var mCredential: GoogleAccountCredential

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MainActivity.THIS_IS = this
        mCredential = GoogleAccountCredential.usingOAuth2(AppController.getInstance(), Collections.singletonList(YouTubeScopes.YOUTUBE))

        supportFragmentManager.beginTransaction().replace(R.id.main_browse_fragment, MainFragment()).commit()

        getResultsFromApi()

        //println("getNumber ${getNumber()}")
    }

    fun getNumber(): Int{
        var a = 0

        try {
            println("ttttt")
            val s = "t"
            a = Integer.parseInt(s)
            return a
        }catch (e: Exception){
            println("11111")
            a = 1
            return a
        }finally {
            println("22222")
            a = 2
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            REQUEST_GOOGLE_PLAY_SERVICES -> {
                if(resultCode != RESULT_OK){
                    Toast.makeText(this, "This app requires Google Play Services. Please install "
                            + "Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT).show()
                }else{
                    getResultsFromApi()
                }
            }

            REQUEST_ACCOUNT_PICKER -> {
                if (resultCode == RESULT_OK && data?.extras != null) {
                    val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                    println("REQUEST_ACCOUNT_PICKER accountName=$accountName")
                    if(accountName != null){
                        mCredential.selectedAccountName = accountName
                        getResultsFromApi()
                    }
                }
            }

            REQUEST_AUTHORIZATION -> {
                if(resultCode == RESULT_OK){
                    getResultsFromApi()
                }
            }
        }
    }

    private fun getResultsFromApi(){
        if(!isGooglePlayServicesAvailable()){
            acquireGooglePlayServices(this)
        }else if (mCredential.selectedAccountName == null) {
            chooseAccount()
        }else if (!isDeviceOnline()) {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show()
        }else{
            MainActivity.mService = YouTube.Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), mCredential)
                    .setApplicationName("MyoutubeList")
                    .build()

            MakeRequestTask().execute()
        }
    }

    private fun chooseAccount(){
        if (EasyPermissions.hasPermissions(AppController.appController, Manifest.permission.GET_ACCOUNTS)) {
            // Start a dialog from which the user can choose an account
            startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER)
        }else{
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {
        println("onPermissionsDenied")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
        println("onPermissionsGranted")
        getResultsFromApi()
    }

    fun getPlayLists(result: List<Playlist>?){
        (supportFragmentManager.findFragmentById(R.id.main_browse_fragment) as MainFragment).setupMenuElements(result)
    }

    class MakeRequestTask : AsyncTask<Void, Void, List<Playlist>>(){

        private var mLastError: Exception? = null

        override fun doInBackground(vararg params: Void?): List<Playlist>? {
            return try {
                getPlayListFromApi()
            }catch (e: Exception){
                mLastError = e
                cancel(true)
                null
            }
        }

        private fun getPlayListFromApi(): List<Playlist>?{
            val result: PlaylistListResponse = MainActivity.mService.playlists().list("snippet,contentDetails")
                    .setMine(true)
                    .setMaxResults(25)
                    .execute()

            return result.items
        }

        override fun onPostExecute(result: List<Playlist>?) {
            if (result == null || result.isEmpty()) {
                Toast.makeText(AppController.getInstance(), "No network connection available.", Toast.LENGTH_SHORT).show()
            }else{
                MainActivity.onResult(result)
            }
        }

        override fun onCancelled() {
            if (mLastError != null) {
                when (mLastError) {
                    is GooglePlayServicesAvailabilityIOException -> {
                        showGooglePlayServicesAvailabilityErrorDialog(MainActivity.THIS_IS, (mLastError as GooglePlayServicesAvailabilityIOException).connectionStatusCode)
                    }

                    is UserRecoverableAuthIOException -> {
                        ActivityCompat.startActivityForResult(MainActivity.THIS_IS, (mLastError as UserRecoverableAuthIOException).intent, MainActivity.REQUEST_AUTHORIZATION, null)
                    }

                    else -> {
                        Toast.makeText(AppController.getInstance(), "The following error occurred:\n" + mLastError!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(AppController.getInstance(), "Request cancelled.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
