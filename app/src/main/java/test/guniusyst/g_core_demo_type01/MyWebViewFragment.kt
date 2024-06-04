/*package test.guniusyst.g_core_demo_type01
import android.util.Base64
import android.webkit.ClientCertRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate

class MyWebViewFragment {
    private inner class MyWebViewClient : WebViewClient() {

        private var mPrivateKey: PrivateKey? = null
        private var mCertificates = arrayOf<X509Certificate?>()

        // 中略

        // クライアント証明書による認証を求められた場合に呼ばれる
        override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest?) {
            // SharedPreferencesであらかじめ保存しておいた証明書とパスワードを取り出す
            val encodingCert = viewModel.getClientCert()
            val clientCertPass = viewModel.getClientCertPass()

            if (mPrivateKey == null || mCertificates.isEmpty() && (encodingCert != null && clientCertPass != null) ) {
                val inputStream = Base64.decode(encodingCert, 0).inputStream()
                val keyStore = KeyStore.getInstance("PKCS12")
                val password = clientCertPass!!.toCharArray()
                keyStore.load(inputStream, password)  //証明書の読み込み
                val alias = keyStore.aliases().nextElement()
                val key = keyStore.getKey(alias, password)
                // 読み込んだ証明書をX509Certificateクラスの配列として取り出す
                if (key is PrivateKey) {
                    mPrivateKey = key
                    val cert = keyStore.getCertificate(alias)
                    mCertificates = arrayOfNulls(1)
                    mCertificates[0] = cert as X509Certificate
                }
                inputStream.close()
            }
            // 証明書を利用した通信を行う
            request!!.proceed(mPrivateKey, mCertificates)
        }
    }

}
 */