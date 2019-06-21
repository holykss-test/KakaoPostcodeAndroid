package com.test.webviewexample

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.*


abstract class BaseWebViewActivity : AppCompatActivity() {

    lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_webview)

        initializeWebView(findViewById(R.id.webView))
    }

    @SuppressLint("JavascriptInterface")
    private fun initializeWebView(webView: WebView) {

        this.webView = webView

        webView.clearCache(true)

        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            allowContentAccess = true
            allowFileAccess = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }

        val cookieManager = CookieManager.getInstance()
        cookieManager?.setAcceptCookie(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            cookieManager?.setAcceptThirdPartyCookies(webView, true)
        }

        webView.webViewClient = createWebViewClientForOpenLinkInItself()
        webView.webChromeClient = createWebChromeClient()

        val javascriptInterface = createJavascriptInterface()
        webView.addJavascriptInterface(javascriptInterface, javascriptInterface.getName())

    }

    abstract fun createJavascriptInterface(): BaseJavascriptInterface

    private fun createWebViewClientForOpenLinkInItself(): WebViewClient {
        return object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return handleUrl(Uri.parse(url))
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return handleUrl(request?.url)
            }

            private fun handleUrl(url: Uri?): Boolean {
                Log.d("", "handleUrl: $url ${url?.path}")
                return false
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                Log.d("", "onReceivedHttpError: $errorResponse")
                super.onReceivedHttpError(view, request, errorResponse)
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                Log.d("", "onReceivedSslError: $error")
                super.onReceivedSslError(view, handler, error)
            }
        }
    }

    private fun createWebChromeClient(): WebChromeClient {
        return object : WebChromeClient() {

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                val message =
                    consoleMessage?.message() + " / " + consoleMessage?.messageLevel() + " / " + consoleMessage?.lineNumber()

                Log.e("WEBCHROME", message)
                return super.onConsoleMessage(consoleMessage)
            }
        }
    }

}
