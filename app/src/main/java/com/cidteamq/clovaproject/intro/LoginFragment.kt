package com.cidteamq.clovaproject.intro

import android.annotation.SuppressLint
import android.app.Activity
import android.support.v4.app.Fragment
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager
import com.amazonaws.regions.Regions
import com.cidteamq.clovaproject.DogSelectionActivity
import com.cidteamq.clovaproject.MainActivity
import com.cidteamq.clovaproject.R
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton
import org.json.JSONObject
import java.net.URLDecoder
import java.util.*

class LoginFragment : Fragment() {
    var callbackManager: CallbackManager? = null
    private var syncClient: CognitoSyncManager? = null
    private var credentialsProvider: CognitoCachingCredentialsProvider? = null
    companion object {
        var handler: OAuthLoginHandler?=null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val poolId = "ap-northeast-2:84cb843a-550a-47ff-acea-5136dd5cd443"

        credentialsProvider = CognitoCachingCredentialsProvider(
            context, /* get the context for the application */
            poolId, /* Identity Pool ID */
            Regions.AP_NORTHEAST_2           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        )

        syncClient = CognitoSyncManager(
            context,
            Regions.AP_NORTHEAST_2, // Region
            credentialsProvider)


        callbackManager = CallbackManager.Factory.create()


        val btnLoginFacebook = view.findViewById<LinearLayout>(R.id.facebookBtnWrapper)

        btnLoginFacebook.setOnClickListener {
            // start Facebook Login
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    setFacebookSession(loginResult.accessToken)

                    val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                    ) { `object`, response ->
                        val fullName = `object`.getString("name")
                        Toast.makeText(context, "안녕하세요 ${fullName} 님!", Toast.LENGTH_LONG).show()
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email")
                    request.parameters = parameters
                    request.executeAsync()

                    val intent = Intent(context, DogSelectionActivity::class.java).apply {
                        (context as Activity).finish()
                    }

                    val token = loginResult.accessToken
                    val id = token.userId
                    intent.putExtra("userId", "${id}_facebook")
                    intent.putExtra("userName", Profile.getCurrentProfile().name)
                    intent.putExtra("userPic", Profile.getCurrentProfile().getProfilePictureUri(400, 400).toString())

                    startActivity(intent)
                }

                override fun onCancel() {
                    Toast.makeText(context, "Facebook login cancelled",
                        Toast.LENGTH_LONG).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(context, "Error in Facebook login " + error.message, Toast.LENGTH_LONG).show()
                    Log.d("FACEBOOK_ERROR", error.message)
                }
            })
        }
        LoginManager.getInstance().logOut()

        val mOAuthLoginModule = OAuthLogin.getInstance()
        mOAuthLoginModule.init(
            context
            ,"0Kddz37Mh2sGGFvk_fKY"
            ,"AHNhMKMp3E"
            ,"창통Q조"
        )

        MainActivity.handler = @SuppressLint("HandlerLeak")

        object : OAuthLoginHandler() {
            override fun run(success: Boolean) {
                if (success) {
                    RequestApiTask().execute()
                }
                else {
                    val errorCode = mOAuthLoginModule.getLastErrorCode(context).code
                    val errorDesc = mOAuthLoginModule.getLastErrorDesc(context)
                    Toast.makeText(context, "errorCode:" + errorCode
                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show()
                }
            }
        }

        val btnLoginNaver = view.findViewById<OAuthLoginButton>(R.id.login_button2)
        btnLoginNaver.setOAuthLoginHandler(MainActivity.handler)

        return view
    }

    private fun setFacebookSession(accessToken: AccessToken) {
        val logins = java.util.HashMap<String, String>()
        logins.put("graph.facebook.com", accessToken.token)
        credentialsProvider?.logins = logins
    }

    private inner class RequestApiTask : AsyncTask<Void, Void, String>() {
        override fun onPreExecute() {
        }

        override fun doInBackground(vararg params: Void): String {
            val url = "https://openapi.naver.com/v1/nid/me"
            val at = OAuthLogin.getInstance().getAccessToken(context)
            return OAuthLogin.getInstance().requestApi(context, at, url)
        }

        override fun onPostExecute(content: String) {
            Log.d("hi", content)
            val mainObject = JSONObject(content)
            val oneObject = mainObject.getJSONObject("response")
            val temp = oneObject.getString("name")
            val intent = Intent(context, DogSelectionActivity::class.java).apply {
                (context as Activity).finish()
            }
            val userId = oneObject.getString("id")
            intent.putExtra("userId", String.format("%s_naver", userId))
            intent.putExtra("userName", URLDecoder.decode(temp, "utf-8"))
            intent.putExtra("userPic", oneObject.getString("profile_image"))
            startActivity(intent)

            Toast.makeText(context, "안녕하세요 ${URLDecoder.decode(temp, "utf-8")}님!", Toast.LENGTH_LONG).show()
        }
    }
}

