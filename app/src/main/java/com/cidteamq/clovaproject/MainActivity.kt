package com.cidteamq.clovaproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.amazonaws.regions.Regions
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager
import com.facebook.AccessToken
import java.util.*
import com.facebook.GraphRequest
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton
import com.nhn.android.naverlogin.OAuthLoginHandler
import android.os.AsyncTask
import android.widget.LinearLayout
import org.json.JSONObject
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.net.URLDecoder


class MainActivity : AppCompatActivity() {
    private val permissions = arrayOf<String>(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private var callbackManager: CallbackManager? = null
    private var credentialsProvider: CognitoCachingCredentialsProvider? = null
    private var syncClient: CognitoSyncManager? = null
    companion object {
        var handler:OAuthLoginHandler?=null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, permissions, 200)

        val poolId = "ap-northeast-2:84cb843a-550a-47ff-acea-5136dd5cd443"

        credentialsProvider = CognitoCachingCredentialsProvider(
            applicationContext, /* get the context for the application */
            poolId, /* Identity Pool ID */
            Regions.AP_NORTHEAST_2           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        )

        syncClient = CognitoSyncManager(
            applicationContext,
            Regions.AP_NORTHEAST_2, // Region
            credentialsProvider)


        callbackManager = CallbackManager.Factory.create()


        val btnLoginFacebook = findViewById<LinearLayout>(R.id.facebookBtnWrapper)

        btnLoginFacebook.setOnClickListener {
            // start Facebook Login
            LoginManager.getInstance().logInWithReadPermissions(this@MainActivity, Arrays.asList("public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    setFacebookSession(loginResult.accessToken)

                    val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                    ) { `object`, response ->
                        val fullName = `object`.getString("name")
                        Toast.makeText(this@MainActivity, "안녕하세요 ${fullName} 님!", Toast.LENGTH_LONG).show()
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email")
                    request.parameters = parameters
                    request.executeAsync()

                    val intent = Intent(this@MainActivity, DogSelectionActivity::class.java).apply {
                        this@MainActivity.finish()
                    }

                    val token = loginResult.accessToken
                    val id = token.userId
                    intent.putExtra("userId", "${id}_facebook")
                    intent.putExtra("userName", Profile.getCurrentProfile().name)
                    intent.putExtra("userPic", Profile.getCurrentProfile().getProfilePictureUri(400, 400).toString())

                    startActivity(intent)
                }

                override fun onCancel() {
                    Toast.makeText(this@MainActivity, "Facebook login cancelled",
                        Toast.LENGTH_LONG).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(this@MainActivity, "Error in Facebook login " + error.message, Toast.LENGTH_LONG).show()
                    Log.d("FACEBOOK_ERROR", error.message)
                }
            })
        }
        LoginManager.getInstance().logOut()

        val mOAuthLoginModule = OAuthLogin.getInstance()
        mOAuthLoginModule.init(
            this@MainActivity
            ,"0Kddz37Mh2sGGFvk_fKY"
            ,"AHNhMKMp3E"
            ,"창통Q조"
            //,OAUTH_CALLBACK_INTENT
            // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
        )

        handler = @SuppressLint("HandlerLeak")

        object : OAuthLoginHandler() {
            override fun run(success: Boolean) {
                if (success) {
                    RequestApiTask().execute()
                }
                else {
                    val errorCode = mOAuthLoginModule.getLastErrorCode(applicationContext).code
                    val errorDesc = mOAuthLoginModule.getLastErrorDesc(applicationContext)
                    Toast.makeText(applicationContext, "errorCode:" + errorCode
                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show()
                }
            }
        }

        val btnLoginNaver = findViewById<OAuthLoginButton>(R.id.login_button2)
        btnLoginNaver.setOAuthLoginHandler(handler)
    }

    private inner class RequestApiTask : AsyncTask<Void, Void, String>() {
        override fun onPreExecute() {
        }

        override fun doInBackground(vararg params: Void): String {
            val url = "https://openapi.naver.com/v1/nid/me"
            val at = OAuthLogin.getInstance().getAccessToken(applicationContext)
            return OAuthLogin.getInstance().requestApi(applicationContext, at, url)
        }

        override fun onPostExecute(content: String) {
            Log.d("hi",content)
            val mainObject = JSONObject(content)
            val oneObject = mainObject.getJSONObject("response")
            val temp = oneObject.getString("name")
            val intent = Intent(this@MainActivity, DogSelectionActivity::class.java).apply {
                this@MainActivity.finish()
            }
            val userId = oneObject.getString("id")
            intent.putExtra("userId", String.format("%s_naver", userId))
            intent.putExtra("userName", URLDecoder.decode(temp,"utf-8"))
            intent.putExtra("userPic", oneObject.getString("profile_image"))
            startActivity(intent)

            Toast.makeText(this@MainActivity, "안녕하세요 ${URLDecoder.decode(temp,"utf-8")}님!", Toast.LENGTH_LONG).show()
        }
    }

    override fun attachBaseContext(newBase: Context){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase)); 
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    private fun setFacebookSession(accessToken: AccessToken) {
        val logins = java.util.HashMap<String, String>()
        logins.put("graph.facebook.com", accessToken.token)
        credentialsProvider?.logins = logins
    }
}
