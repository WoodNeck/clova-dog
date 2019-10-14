package com.cidteamq.clovaproject

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_css.ttsInputString
import kotlinx.android.synthetic.main.activity_css.ttsInputBtn

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import android.os.Environment
import android.os.Looper
import android.os.Handler
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class CSSActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_css)

        ttsInputBtn.setOnClickListener {
            runTTS()
        }
    }

    fun runTTS() {
        var clientInputString = ttsInputString.text.toString()

        Thread() {
            run() {
                requestTtsContext(clientInputString)
            }
        }.start()

    }

    override fun attachBaseContext(newBase: Context){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    fun requestTtsContext(clientInputString: String) {
        val clientId = "G6u3kTIiiaBRMy1oI_pO"//애플리케이션 클라이언트 아이디값";
        val clientSecret = "iDn5lbZhnM"//애플리케이션 클라이언트 시크릿값";
        try {
            val apiURL = "https://openapi.naver.com/v1/voice/tts.bin"
            val url = URL(apiURL)
            val con = url.openConnection() as HttpURLConnection
            con.setRequestMethod("POST")
            con.setRequestProperty("X-Naver-Client-Id", clientId)
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret)
            // post request
            val postParams = "speaker=mijin&speed=0&text=" + URLEncoder.encode(clientInputString, "UTF-8")
            con.setDoOutput(true)
            val wr = DataOutputStream(con.getOutputStream())
            wr.writeBytes(postParams)
            wr.flush()
            wr.close()
            val responseCode = con.getResponseCode()
            val br: BufferedReader
            if (responseCode == 200) { // 정상 호출
                println("200")
                val inputStream = con.getInputStream()
                val bytes = ByteArray(1024)
                // 랜덤한 이름으로 mp3 파일 생성
                val tempname = java.lang.Long.valueOf(Date().getTime()).toString()

                var path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC)
                val file = File(path, "/" + tempname + ".mp3")
                val outputStream = FileOutputStream(file)
                var read = inputStream.read(bytes)
                while (read != -1) {
                    outputStream.write(bytes, 0, read)
                    read = inputStream.read(bytes)
                }
                inputStream.close()

                this.runOnUiThread(Runnable { Toast.makeText(this, tempname + ".mp3로 파일을 저장했습니다.", Toast.LENGTH_SHORT).show() })
            } else {  // 에러 발생
                br = BufferedReader(InputStreamReader(con.getErrorStream()))
                val response = StringBuffer()
                var inputLine = br.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = br.readLine()
                }
                br.close()
                println(response.toString())
            }
        } catch (e: Exception) {
            println(e)
        }

    }
}
