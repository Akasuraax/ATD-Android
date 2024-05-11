import android.content.Context
import com.example.atdapp.R
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

object SSLTrustManager {
    fun addCertificatesToTrustStore(context: Context) {
        try {
            // Load CAs from an InputStream
            val cf = CertificateFactory.getInstance("X.509")
            val caInput = context.resources.openRawResource(R.raw.certificat)
            val ca: Certificate = cf.generateCertificate(caInput)
            caInput.close()
            // Create a KeyStore containing our trusted CAs
            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
            keyStore.setCertificateEntry("ca", ca)
            // Create a TrustManager that trusts the CAs in our KeyStore
            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
            tmf.init(keyStore)
            // Install the TrustManager
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, tmf.trustManagers, SecureRandom())
            // Install the all-trusting host verifier
            val allHostsValid = HostnameVerifier { hostname, session -> true }
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
            // Install the all-trusting host verifier
            val socketFactory = sslContext.socketFactory
            HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
