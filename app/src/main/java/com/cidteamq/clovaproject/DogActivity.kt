package com.cidteamq.clovaproject

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.cidteamq.clovaproject.dashboard.DashboardView
import com.cidteamq.clovaproject.interact.InteractAreaLayout
import com.cidteamq.clovaproject.interact.InteractAreaState
import com.cidteamq.clovaproject.interact.fragment.InteractIdleView
import com.cidteamq.clovaproject.modelInfo.ModelInfoWrapper
import com.cidteamq.clovaproject.utils.BackBtnTimer
import com.cidteamq.clovaproject.utils.DogWebSocketListener
import com.cidteamq.clovaproject.utils.InfoView
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import com.pnikosis.materialishprogress.ProgressWheel
import com.squareup.picasso.Picasso
import com.unity3d.player.UnityPlayer
import kotlinx.android.synthetic.main.activity_dog.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class DogActivity : AppCompatActivity() {
    var dogName: String = ""
    val listener: DogWebSocketListener = DogWebSocketListener(this)
    private val backBtnTimer = BackBtnTimer(this)
    private var unityPlayer: UnityPlayer? = null
    private var interactArea: InteractAreaLayout? = null
    private var dogView: InfoView? = null
    private var dashboard: DashboardView? = null
    private var dashboardBtn: RelativeLayout? = null
    private var dashboardIcon: ImageView? = null
    private var modelInfoBtn: RelativeLayout? = null
    private var modelInfoWrapper: RelativeLayout? = null

    private var connectionIndicator: ProgressWheel? = null
    private var dashboardStatusIcon: LinearLayout? = null
    private var refreshIcon: ImageView? = null
    private var newLogCounter: TextView? = null
    private var showingDog = true
    private var showingIntro = false
    private var toolbar: Toolbar? = null
    private var userId: String = ""
    private var userName: String = ""
    private var userPic: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog)
        interactArea = findViewById(R.id.interactAreaWrapper)
        dogView = findViewById(R.id.dogView)
        dashboard = findViewById(R.id.dashboard)
        modelInfoWrapper = findViewById(R.id.modelInfoWrapper)

        dashboardStatusIcon = findViewById(R.id.dashboardStatusIcon)
        refreshIcon = findViewById(R.id.refreshIcon)
        newLogCounter = findViewById(R.id.newLogCounter)

        val intent = this.intent
        dogName = intent.getStringExtra("dogName")
        userId = intent.getStringExtra("userId")
        userName = intent.getStringExtra("userName")
        userPic = intent.getStringExtra("userPic")
        Log.d("TEST", "dogName: ${dogName}, userId: ${userId}, userName: ${userName}, userPic: ${userPic}")

        setupDrawer()

        dashboardBtn = findViewById(R.id.dashboardBtn)
        dashboardIcon = findViewById(R.id.dashboardIcon)
        connectionIndicator = findViewById(R.id.connectionIndicator)
        connectionIndicator!!.barColor = Color.WHITE
        connectionIndicator!!.spin()

        modelInfoBtn = findViewById(R.id.modelInfoBtn)
        modelInfoBtn!!.isClickable = true
        modelInfoBtn!!.setOnClickListener {
            swapIntroductionArea()
        }

        listener.setUrl(userId, dogName)
        listener.start()

        initUnity()
        makeDog(dogName)
        changePose("Bark")
    }

    fun swapDashboardArea() {
        val dogWrapper = findViewById<RelativeLayout>(R.id.dogWrapper)
        val dashboardWrapper = findViewById<LinearLayout>(R.id.dashboardWrapper)

        if (showingDog) {
            dogWrapper.visibility = View.GONE
            dashboardWrapper.visibility = View.VISIBLE
        } else {
            dogWrapper.visibility = View.VISIBLE
            dashboardWrapper.visibility = View.GONE
        }
        logChecked()
        showingDog = !showingDog
    }

    fun swapIntroductionArea() {
        val dogWrapper = findViewById<RelativeLayout>(R.id.dogWrapper)
        if (showingDog) {
            dogWrapper.visibility = View.GONE
            modelInfoWrapper!!.visibility = View.VISIBLE
            showingIntro = true
        } else {
            dogWrapper.visibility = View.VISIBLE
            modelInfoWrapper!!.visibility = View.GONE
            showingIntro = false
        }
        showingDog = !showingDog
    }

    fun changePose(pose: String) {
        UnityPlayer.UnitySendMessage("EventHandler", "ChangePose", pose)
    }

    fun onWebSocketOpen() {
        Thread(Runnable {
            runOnUiThread {
                val idleView = interactArea!!.getView(InteractAreaState.State.IDLE) as InteractIdleView
                interactArea!!.swapView(interactArea!!.getCurrentView(), idleView)
                idleView.makeAvailable()

                connectionIndicator!!.stopSpinning()
                connectionIndicator!!.visibility = View.GONE

                dashboardStatusIcon!!.visibility = View.GONE
                refreshIcon!!.visibility = View.GONE
                newLogCounter!!.visibility = View.GONE

                dashboardIcon!!.visibility = View.VISIBLE
                dashboardIcon!!.setImageResource(R.drawable.ic_brain)

                dashboardBtn!!.isClickable = true
                dashboardBtn!!.setOnClickListener {
                    swapDashboardArea()
                }
            }
        }).start()
    }

    fun onWebSocketFailure() {
        Thread(Runnable {
            runOnUiThread {
                val idleView = interactArea!!.getView(InteractAreaState.State.IDLE) as InteractIdleView
                interactArea!!.swapView(interactArea!!.getCurrentView(), idleView)
                idleView.makeUnavailable()

                connectionIndicator!!.stopSpinning()
                connectionIndicator!!.visibility = View.GONE

                dashboardStatusIcon!!.visibility = View.VISIBLE
                refreshIcon!!.visibility = View.VISIBLE
                newLogCounter!!.visibility = View.GONE

                dashboardIcon!!.visibility = View.VISIBLE
                dashboardIcon!!.setImageResource(R.drawable.ic_signal_off)

                dashboardBtn!!.isClickable = true
                dashboardBtn!!.setOnClickListener {
                    dashboardIcon!!.visibility = View.GONE
                    dashboardBtn!!.isClickable = false

                    connectionIndicator!!.visibility = View.VISIBLE
                    connectionIndicator!!.spin()

                    dashboardStatusIcon!!.visibility = View.GONE
                    refreshIcon!!.visibility = View.GONE

                    listener.start()
                }
            }
        }).start()
    }

    fun logCountUpdate(count: Int) {
        if (count > 0) {
            dashboardStatusIcon!!.visibility = View.VISIBLE
            newLogCounter!!.visibility = View.VISIBLE
            newLogCounter!!.text = count.toString()
            refreshIcon!!.visibility = View.GONE
        } else {
            logChecked()
        }
    }

    private fun logChecked() {
        dashboardStatusIcon!!.visibility = View.GONE
        refreshIcon!!.visibility = View.GONE
        newLogCounter!!.visibility = View.GONE
        dashboard!!.resetLogCounter()
    }

    private fun initUnity() {
        window.setFormat(PixelFormat.RGBX_8888)
        unityPlayer = UnityPlayer(this)
        val glesMode = unityPlayer!!.settings.getInt("gles_mode", 1)
        unityPlayer!!.init(glesMode, false)
        val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        dogView!!.addView(unityPlayer!!.view, 0, lp)
    }

    private fun makeDog(model: String) {
        val name = modelToRealName(model)
        UnityPlayer.UnitySendMessage("EventHandler", "MakeDog", name)
    }

    private fun modelToRealName(model: String): String = when (model) {
        "MLP" -> "Beagle"
        "RNN" -> "Corgi"
        "DQN" -> "Frenchie"
        "DRQN" -> "Pug"
        else -> ""
    }

    override fun onBackPressed() {
        if (showingIntro){
            swapIntroductionArea()
        }
        else if (!showingDog) {
            dashboard!!.back()
        } else {
            backBtnTimer.onBackPressed()
        }
    }

    override fun attachBaseContext(newBase: Context){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        listener.close()
        try {
            val v = unityPlayer!!::class.java.getDeclaredField("v")
            v.isAccessible = true
            val a = v.type.getMethod("a")
            a.invoke(v.get(unityPlayer))

            val o = unityPlayer!!::class.java.getDeclaredField("o")
            o.isAccessible = true
            o.set(unityPlayer, true)

            unityPlayer!!.pause()
            val unloadVR = unityPlayer!!::class.java.getDeclaredMethod("unloadGoogleVR")
            unloadVR.isAccessible = true
            unloadVR.invoke(unityPlayer)

            unityPlayer!!.removeAllViews()

            val h = unityPlayer!!::class.java.getDeclaredMethod("h")
            h.isAccessible = true
            h.invoke(unityPlayer)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    override fun onPause() {
        super.onPause()
        unityPlayer!!.pause()
    }
    override fun onResume() {
        super.onResume()
        unityPlayer!!.resume()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        unityPlayer!!.lowMemory()
    }
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {
            unityPlayer!!.lowMemory()
        }
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        unityPlayer!!.configurationChanged(newConfig)
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        unityPlayer!!.windowFocusChanged(hasFocus)
    }

    override fun onStart(){
        super.onStart()
        interactArea?.onStart()
    }

    override fun onStop() {
        super.onStop()
        interactArea?.onStop()
    }

    private fun setupDrawer() {
        DrawerImageLoader.init(object: AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable) {
                Picasso.with(imageView.context).load(uri).placeholder(placeholder).into(imageView);
            }

            override fun cancel(imageView: ImageView) {
                Picasso.with(imageView.context).cancelRequest(imageView);
            }
        })

        val selectdog = SecondaryDrawerItem().withIdentifier(1).withName(R.string.dog_menu_selectdog).withSelectable(false).withIcon(R.drawable.ic_paw_print)
        val license = SecondaryDrawerItem().withIdentifier(2).withName(R.string.dog_menu_license).withSelectable(false).withIcon(R.drawable.ic_settings)
        val logout = SecondaryDrawerItem().withIdentifier(3).withName(R.string.dog_menu_logout).withSelectable(false).withIcon(R.drawable.ic_lock_open)

        toolbar = findViewById(R.id.toolbar)
        var profile = ProfileDrawerItem()
            .withName(userName)
        profile = if (userId == "") profile.withIcon(resources.getDrawable(R.drawable.ic_person))
        else profile.withIcon(userPic)

        val account = AccountHeaderBuilder()
            .withActivity(this)
            .addProfiles(
                profile
            )
            .withHeaderBackground(R.drawable.bg_dog)
            .withSelectionListEnabled(false)
            .withTextColor(resources.getColor(R.color.white))
            .build()

        val drawer = DrawerBuilder()
            .withActivity(this)
            .withAccountHeader(account)
            .withToolbar(toolbar!!)
            .addDrawerItems(
                selectdog,
                license,
                logout
            )
            .withOnDrawerItemClickListener { view, position, drawerItem ->
                when (position) {
                    1 -> {
                        val i = Intent(this, DogSelectionActivity::class.java)
                        i.putExtra("userId", userId)
                        i.putExtra("userName", userName)
                        i.putExtra("userPic", userPic)
                        startActivity(i)
                        finish()
                    }
                    2 -> {
                        LibsBuilder()
                            .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                            .start(this)
                    }
                    3 -> {
                        val i = Intent(this, IntroActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                }
                true
            }
            .build()

        drawer.actionBarDrawerToggle.drawerArrowDrawable.color = resources.getColor(R.color.interactIdleBackground)
    }
}
