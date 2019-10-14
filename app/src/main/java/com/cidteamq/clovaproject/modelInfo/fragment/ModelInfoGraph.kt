package com.cidteamq.clovaproject.modelInfo.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cidteamq.clovaproject.R
import android.view.animation.AnimationUtils
import com.cidteamq.clovaproject.DogActivity
import kotlinx.android.synthetic.main.fragment_model_info_graph.*
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import com.squareup.picasso.Picasso


/**
 * Created by aksdmj on 17. 12. 17.
 */
class ModelInfoGraph : Fragment {

    constructor(): super()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_model_info_graph, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var dogname = (view.context as DogActivity).dogName

        var imageToShow = ArrayList<Int>()
        var textToShow = ArrayList<Int>()
        if(dogname == "MLP"){
            imageToShow.add(R.drawable.mlp_1)
            imageToShow.add(R.drawable.mlp_2)
            imageToShow.add(R.drawable.mlp_3)
            imageToShow.add(R.drawable.mlp_4)
            imageToShow.add(R.drawable.mlp_5)
            imageToShow.add(R.drawable.mlp_6)
            imageToShow.add(R.drawable.mlp_7)
            imageToShow.add(R.drawable.mlp_8)
            textToShow.add(R.string.mlp_1)
            textToShow.add(R.string.mlp_2)
            textToShow.add(R.string.mlp_3)
            textToShow.add(R.string.mlp_4)
            textToShow.add(R.string.mlp_5)
            textToShow.add(R.string.mlp_6)
            textToShow.add(R.string.mlp_7)
            textToShow.add(R.string.mlp_8)


        }
        else if(dogname == "RNN"){
            imageToShow.add(R.drawable.rnn_1)
            imageToShow.add(R.drawable.rnn_2)
            imageToShow.add(R.drawable.rnn_3)
            imageToShow.add(R.drawable.rnn_4)
            imageToShow.add(R.drawable.rnn_5)
            imageToShow.add(R.drawable.rnn_6)
            textToShow.add(R.string.rnn_1)
            textToShow.add(R.string.rnn_2)
            textToShow.add(R.string.rnn_3)
            textToShow.add(R.string.rnn_4)
            textToShow.add(R.string.rnn_5)
            textToShow.add(R.string.rnn_6)

        }
        else if(dogname == "DQN"){
            imageToShow.add(R.drawable.dqn_1)
            imageToShow.add(R.drawable.dqn_2)
            imageToShow.add(R.drawable.dqn_3)
            imageToShow.add(R.drawable.dqn_4)
            imageToShow.add(R.drawable.dqn_5)
            textToShow.add(R.string.dqn_1)
            textToShow.add(R.string.dqn_2)
            textToShow.add(R.string.dqn_3)
            textToShow.add(R.string.dqn_4)
            textToShow.add(R.string.dqn_5)
        }
        else{
            imageToShow.add(R.drawable.drqn_1)
            imageToShow.add(R.drawable.drqn_2)
            imageToShow.add(R.drawable.drqn_3)
            imageToShow.add(R.drawable.drqn_4)
            textToShow.add(R.string.drqn_1)
            textToShow.add(R.string.drqn_2)
            textToShow.add(R.string.drqn_3)
            textToShow.add(R.string.drqn_4)
        }

        Picasso.with(context).load(imageToShow[0]).into(slideImage)
        infoText.setText(textToShow[0])
        var index = 0




        var animTransAlpha = AnimationUtils.loadAnimation(view.context, R.anim.anim_translate_alpha)
        rightButton.setOnClickListener{
            val fadeInDuration = 2000 // Configure time values here

            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.interpolator = DecelerateInterpolator() // add this
            fadeIn.duration = fadeInDuration.toLong()

            index ++
            if(index == imageToShow.size) {
                index = 0
            }
            Picasso.with(context).load(imageToShow[index]).into(slideImage)
            infoText.setText(textToShow[index])
            slideImage.setAnimation(fadeIn)

        }
        leftButton.setOnClickListener{
            val fadeInDuration = 2000 // Configure time values here

            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.interpolator = DecelerateInterpolator() // add this
            fadeIn.duration = fadeInDuration.toLong()

            index --
            if(index < 0) {
                index = imageToShow.size-1
            }
            Picasso.with(context).load(imageToShow[index]).into(slideImage)
            infoText.setText(textToShow[index])
            slideImage.setAnimation(fadeIn)

        }


    }


}
