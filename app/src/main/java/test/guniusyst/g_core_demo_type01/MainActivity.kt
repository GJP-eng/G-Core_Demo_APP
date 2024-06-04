package test.guniusyst.g_core_demo_type01

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.InputStream
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class MainActivity : AppCompatActivity() {
    // カメラと録音のパーミッション要求用のリクエストコード
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        webView.webViewClient = MyWebViewClient(getCertificate(this)) // MyWebViewClientを設定
        webView.webChromeClient = object : WebChromeClient() {// WebChromeClientをセットアップ
            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val requestedResources = request.resources
                    if (requestedResources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                        request.grant(requestedResources) // カメラへのアクセスを許可
                    }
                }
            }
        }
        webView.settings.javaScriptEnabled = true // JavaScriptを有効化する（必要に応じて）
        webView.settings.mediaPlaybackRequiresUserGesture = true // ユーザージェスチャーが不要な場合は、ビデオ自動再生を許可
        webView.settings.domStorageEnabled = true // WebRTCでのデータの永続性のために必要な設定
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        setContentView(webView)
        //webView.loadUrl("http://34.97.152.224:8080/")/*ローカル外からでもアクセス可能*/
        webView.loadUrl("http://mcn-c6d219090.miyazaki-catv.ne.jp:8080")
        checkPermissions()
    }

    private inner class MyWebViewClient(private val certificate: X509Certificate?) : WebViewClient() {
        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            // エラーを無視してWebViewをロードする例
            handler?.proceed()
        }

        // WebView内でリンクがクリックされた際の処理
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            // 通常はreturn falseでWebView内でリンクが開かれますが、特定の条件で外部ブラウザを起動させたりできます。
            return false
        }
    }

    private fun getCertificate(context: Context): X509Certificate? {
        try {
            // assetsディレクトリから証明書ファイルを読み込む
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val certificateInputStream: InputStream = context.assets.open("server_crt.pem")
            val certificate: Certificate = certificateFactory.generateCertificate(certificateInputStream)
            //certificateInputStream.close()

            return certificate as X509Certificate
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    // ウェブページ履歴を操作する（戻るボタンで戻れるようにする）
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val webView = findViewById<WebView>(R.id.webview)
        // canGoBack()でアクセスできるウェブページ履歴がある場合に true を返す
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack() // 履歴を後に進める場合はgoForward()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun checkPermissions() {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

        if (cameraPermission != PackageManager.PERMISSION_GRANTED || audioPermission != PackageManager.PERMISSION_GRANTED) {
            // 未許可の場合、ユーザーに許可を求めるダイアログを表示
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    // onRequestPermissionsResult でユーザーの権限許可の結果を取得するコードが必要です
    // ユーザーが権限要求に応答すると、このメソッドが呼ばれます
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // ユーザーが権限を許可したかどうかを確認
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // 許可された場合の処理を記述する
            } else {
                // 権限が拒否された場合の処理を記述する
                return;
            }
        }
    }
}