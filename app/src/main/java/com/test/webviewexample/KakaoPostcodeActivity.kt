package com.test.webviewexample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_webview.*

class KakaoPostcodeActivity : BaseWebViewActivity() {
    override fun createJavascriptInterface() = object : BaseJavascriptInterface {
        override fun getName() = "android"

        @JavascriptInterface
        fun onStartScript(message: String) {
            runOnUiThread {
                Log.d("onStartScript", message)
                Toast.makeText(this@KakaoPostcodeActivity, message, Toast.LENGTH_LONG).show()
            }
        }

        @JavascriptInterface
        fun onReceiveData(json: String) {
            runOnUiThread {
                Log.d("onReceiveData", json)
                Toast.makeText(this@KakaoPostcodeActivity, json, Toast.LENGTH_LONG).show()
                closeWebView(json)
            }
        }

    }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openWebView()

        toggleLoadFrom.setOnClickListener {
            openWebView()
        }

        buttonOpen.setOnClickListener {
            openWebView()
        }

        buttonClose.setOnClickListener {
            closeWebView()
        }
    }

    private fun closeWebView(message:String = "") {
        webView.loadDataWithBaseURL(
            null,
            """<html><body>Webview closed<br><textarea rows="20" cols="50">$message</textarea></body></html>""",
            "text/html",
            "utf-8",
            null
        )
    }

    private fun openWebView() {

        if (toggleLoadFrom.isChecked) {
            openUrl()
        } else {
            openData()
        }
    }

    private fun openUrl() {

        webView.loadUrl("https://sir-playground.firebaseapp.com/kakaopostcodessl/")
    }

    private fun openData() {
        val kakaoPostcodeJavaScript = """
            <!--autoload=false 파라미터를 이용하여 자동으로 로딩되는 것을 막습니다.-->
            <script src="https://ssl.daumcdn.net/dmaps/map_js_init/postcode.v2.js?autoload=false"></script>
            <script>
                window.android.onStartScript("Start Script from local data");
                //load함수를 이용하여 core스크립트의 로딩이 완료된 후, 우편번호 서비스를 실행합니다.
                daum.postcode.load(function(){
                    new daum.Postcode({
                        oncomplete: function(data) {
                            window.android.onReceiveData(JSON.stringify(data));
                        }
                    }).embed();
                });
            </script>
            """

        webView.loadDataWithBaseURL(
            "https://ssl.daumcdn.net/",
            kakaoPostcodeJavaScript,
            "text/html",
            "utf-8",
            null
        )
    }

}
