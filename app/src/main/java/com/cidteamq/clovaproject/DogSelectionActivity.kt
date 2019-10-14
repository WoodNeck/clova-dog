package com.cidteamq.clovaproject

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.LinearLayout
import com.cidteamq.clovaproject.utils.BackBtnTimer
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class DogSelectionActivity : AppCompatActivity() {
    var lastClicked: DogSelectionFragment? = null
    var userId: String? = null
    var userName: String? = null
    var userPic: String? = null
    val dogs = ArrayList<DogSelectionFragment>()
    private val backBtnTimer = BackBtnTimer(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_selection)

        val rowAbove = findViewById<LinearLayout>(R.id.rowAbove)
        val rowBelow = findViewById<LinearLayout>(R.id.rowBelow)
        val mlp = findViewById<DogSelectionFragment>(R.id.mlpDog)
        val rnn = findViewById<DogSelectionFragment>(R.id.rnnDog)
        val dqn = findViewById<DogSelectionFragment>(R.id.dqnDog)
        val drqn = findViewById<DogSelectionFragment>(R.id.drqnDog)
        dogs.add(mlp)
        dogs.add(rnn)
        dogs.add(dqn)
        dogs.add(drqn)

        mlp.setResource(R.mipmap.beagle_icon, "MLP")
        mlp.setColor("#795548", "#4b2c20", "#251610")
        mlp.setReference(rowAbove, rnn, rowBelow)
        mlp.setDescription(R.string.dog_desc_mlp, R.string.model_desc_mlp)

        rnn.setResource(R.mipmap.corgi_icon, "RNN")
        rnn.setColor("#ff9800", "#c66900", "#633400")
        rnn.setReference(rowAbove, mlp, rowBelow)
        rnn.setDescription(R.string.dog_desc_rnn, R.string.model_desc_rnn)

        dqn.setResource(R.mipmap.frenchie_icon, "DQN")
        dqn.setColor("#546e7a", "#29434e", "#142127")
        dqn.setReference(rowBelow, drqn, rowAbove)
        dqn.setDescription(R.string.dog_desc_dqn, R.string.model_desc_dqn)

        drqn.setResource(R.mipmap.pug_icon, "DRQN")
        drqn.setColor("#bdbdbd", "#8d8d8d", "#464646")
        drqn.setReference(rowBelow, dqn, rowAbove)
        drqn.setDescription(R.string.dog_desc_drqn, R.string.model_desc_drqn)

        val intent = this.intent
        userId = intent.getStringExtra("userId")
        userName = intent.getStringExtra("userName")
        userPic = intent.getStringExtra("userPic")

    }

    override fun attachBaseContext(newBase: Context){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onBackPressed() {
        if (lastClicked != null) {
            if (lastClicked!!.shrink()) {
                dogs.forEach { it.toogleClickable() }
                lastClicked = null
            }
        } else {
            backBtnTimer.onBackPressed()
        }
    }

    fun fragmentClicked(fragment: DogSelectionFragment) {
        lastClicked = fragment
        dogs.forEach { it.toogleClickable() }
    }
}
