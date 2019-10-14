package com.cidteamq.clovaproject.interact.fragment

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.cidteamq.clovaproject.utils.*
import com.naver.speech.clientapi.SpeechRecognitionResult
import java.lang.ref.WeakReference
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.LinearLayout
import com.cidteamq.clovaproject.DogActivity
import com.cidteamq.clovaproject.R
import com.cidteamq.clovaproject.interact.InteractAreaLayout
import com.cidteamq.clovaproject.interact.InteractAreaState
import com.cidteamq.clovaproject.interact.InteractView
import com.pnikosis.materialishprogress.ProgressWheel
import org.json.JSONArray
import org.json.JSONObject

class InteractVoiceView : InteractView {
    private val CLIENT_ID = "YOUR_CLIENT_ID_HERE"
    private var recognitionHandler: RecognitionHandler? = null
    private var naverRecognizer: NaverRecognizer? = null
    private var txtResult: TextView? = null
    private var recognitionResults = arrayListOf<String>()
    private var writer: AudioWriterPCM? = null
    private var msgReceivedTime: Long = 0

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        this.tag = InteractAreaState.State.VOICE

        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.fragment_interact_voice, this, false)
        addView(v)

        val dogActivity = context as DogActivity
        val websocket = dogActivity.listener
        websocket.addHandler(WebSocketHandler(this))

        val progressIndicator = findViewById<ProgressWheel>(R.id.progressIndicator)
        progressIndicator!!.barColor = Color.WHITE

        recognitionHandler = RecognitionHandler(this)
        naverRecognizer = NaverRecognizer(context, recognitionHandler!!, CLIENT_ID)
        txtResult = findViewById(R.id.csrResult)
    }

    override fun hide() {
        (context as Activity).runOnUiThread({
            this.visibility = View.GONE
            val progressIndicator = findViewById<ProgressWheel>(R.id.progressIndicator)
            progressIndicator.visibility = View.GONE
            if (progressIndicator.isSpinning)
                progressIndicator.stopSpinning()
        })
    }

    override fun show() {
        this.visibility = View.VISIBLE
        val inactiveColor = ResourcesCompat.getColor(resources, R.color.voiceInactive, null)
        txtResult?.setTextColor(inactiveColor)

        val bgColor = ResourcesCompat.getColor(resources, R.color.interactVoiceBackground, null)
        val wrapper = findViewById<LinearLayout>(R.id.voiceViewWrapper)
        wrapper.setBackgroundColor(bgColor)

        if (!naverRecognizer?.speechRecognizer!!.isRunning) {
            startRecognize()
        }
    }

    override fun onStart() {
        naverRecognizer?.speechRecognizer?.initialize()
        if (this.visibility != View.VISIBLE) return
        if (!naverRecognizer?.speechRecognizer!!.isRunning) {
            startRecognize()
        }
    }

    override fun onStop() {
        naverRecognizer?.speechRecognizer?.release()
    }

    fun sendRecognitionResult() {
        val dogActivity = context as DogActivity
        val websocket = dogActivity.listener
        val msgBuilder = WebSocketMessageBuilder()
        val msg = msgBuilder.setMessageType(DogWebSocketListener.MessageType.PRED)
            .addMessage("command", recognitionResults[0])
            .build()
        websocket.send(msg)
        msgReceivedTime = System.currentTimeMillis()
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            R.id.clientReady -> {
                writer = AudioWriterPCM(
                        Environment.getExternalStorageDirectory().absolutePath + "/NaverSpeechTest")
                writer?.open("Test")
            }

            R.id.audioRecording -> writer?.write(msg.obj as ShortArray)

            R.id.partialResult -> {
                txtResult?.text = msg.obj as String
            }

            R.id.finalResult -> {
                val speechRecognitionResult = msg.obj as SpeechRecognitionResult
                val results = speechRecognitionResult.results
                recognitionResults.clear()
                for (result in results) {
                    recognitionResults.add(result)
                }
                val successColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
                if (recognitionResults[0] == "") {
                    txtResult?.text = "음성을 인식하지 못했습니다."
                    swapToIdle()
                } else {
                    val wrapper = findViewById<LinearLayout>(R.id.voiceViewWrapper)
                    wrapper.setBackgroundColor(successColor)
                    val progressIndicator = findViewById<ProgressWheel>(R.id.progressIndicator)
                    progressIndicator.visibility = View.VISIBLE
                    progressIndicator.spin()
                    sendRecognitionResult()
                }
            }

            R.id.recognitionError -> {
                writer?.close()
                txtResult?.text = "Error code : " + msg.obj.toString()

                val builder: AlertDialog.Builder
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = AlertDialog.Builder(context);

                }
                builder.setTitle("Naver Clova CSR API에서 오류가 발생했습니다.")
                    .setMessage(parseErrorCode(msg.obj as Int))
                    .setPositiveButton(android.R.string.yes, { dialog, which ->
                        // continue with delete
                    }
                )
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
                swapToIdle()
            }

            R.id.clientInactive -> {
                writer?.close()
            }
        }
    }

    private fun serverOnMessage(msg: Message) {
        when (msg.what) {
            DogWebSocketListener.MessageType.PRED.ordinal -> {
                val result = JSONObject(msg.obj as String)
                result.put("command", recognitionResults[0])
                result.put("recognition", JSONArray(recognitionResults))
                val feedbackView = (parent as InteractAreaLayout).getView(InteractAreaState.State.FEEDBACK) as InteractFeedbackView
                feedbackView.setResult(result)

                val currentTime = System.currentTimeMillis()
                Log.d(TAG, "${msgReceivedTime} --> $currentTime (${currentTime - msgReceivedTime})")
                if (currentTime > msgReceivedTime + 1000) {
                    swapToFeedback()
                } else {
                    val timeLeft = msgReceivedTime + 1000 - currentTime
                    val task = object: AsyncTask<String, String, String>() {
                        override fun doInBackground(vararg p0: String?): String {
                            Thread.sleep(timeLeft)
                            swapToFeedback()
                            return "0"
                        }
                    }
                    task.execute()
                }
            }
        }
    }

    private fun swapToIdle() {
        val voiceView = this
        val task = object: AsyncTask<String, String, String>() {
            override fun doInBackground(vararg p0: String?): String {
                Thread.sleep(3000)
                val parent = parent as InteractAreaLayout
                val idleView = parent.getView(InteractAreaState.State.IDLE) as InteractIdleView
                parent.swapView(voiceView, idleView)
                return "0"
            }
        }
        task.execute()
    }

    private fun swapToFeedback() {
        val parent = parent as InteractAreaLayout
        val feedbackView = parent.getView(InteractAreaState.State.FEEDBACK) as InteractFeedbackView
        parent.swapView(this, feedbackView)
    }

    private fun startRecognize() {
        recognitionResults.clear()
        txtResult?.text = "듣는 중..."
        naverRecognizer?.recognize()
    }

    private fun parseErrorCode(code: Int): String = when (code) {
        10 -> "네트워크 자원 초기화 오류"
        11 -> "네트워크 자원 해제 오류"
        12, 13, 14, 41 -> "네트워크 타임아웃"
        15 -> "유효하지 않은 패킷"
        20 -> "오디오 자원 초기화 오류"
        21 -> "오디오 자원 해제 오류"
        22 -> "녹음 권한이 없습니다"
        30 -> "인증 권한 오류"
        40 -> "인식 결과 오류"
        42 -> "예상치 못한 음성인식 결과가 감지되었습니다"
        50 -> "규정되지 않은 이벤트 발생"
        60 -> "프로토콜 버전 오류"
        61 -> "클라이언트 정보 오류"
        62 -> "음성인식 가용 서버 부족"
        63 -> "음성인식 서버 세션 만료"
        64 -> "음성 패킷 사이즈 초과"
        65 -> "인증용 타임 스탬프 불량"
        66 -> "올바른 서비스 타입이 아님"
        67 -> "올바른 언어 타입이 아님"
        70 -> "Open API 인증 오류"
        71 -> "정해진 API 호출 제한량 소진"
        else -> "알 수 없는 에러: $code"
    }

    internal class RecognitionHandler(view: InteractVoiceView) : Handler() {
        private val mView: WeakReference<InteractVoiceView> = WeakReference(view)

        override fun handleMessage(msg: Message) {
            val view = mView.get()
            view?.handleMessage(msg)
        }
    }

    internal class WebSocketHandler(view: InteractVoiceView) : Handler() {
        private val mView: WeakReference<InteractVoiceView> = WeakReference(view)

        override fun handleMessage(msg: Message) {
            val view = mView.get()
            view?.serverOnMessage(msg)
        }
    }

    companion object {
        private val TAG = InteractVoiceView::class.java.simpleName
    }
}
