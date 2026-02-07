package de.shiirroo.tps.webserver.adapter;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.helper.Utilities;
import de.shiirroo.tps.history.TpsHistory;
import de.shiirroo.tps.webserver.WebServerConfig;
import lombok.Getter;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

class DefaultWebAdapter implements WebAdapter {

    @Getter
    private HttpServer server;


    @Override
    public void registerWebServer() {
        try {
            var config = Tps.get().getConfig().get().getWebServerConfig();
            server = config.isHttpOnly() ? createHttpServer(config) : createHttpsServer(config);
                server.createContext("/Shiirroo/TPS", exchange -> {
                var test = TpsHistory.get().asJson();
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(200, test.getBytes().length);
                exchange.getResponseBody().write(test.getBytes());
                exchange.close();
            });
            server.start();
            Tps.getLog().info("DefaultWebAdapter: Started embedded "+ (config.isHttpOnly() ? "http" :"https" )+ " server on " + config.getBindIP() + ":" + config.getPort());
        } catch (Exception e) {
            Tps.getLog().severe("DefaultWebAdapter: Failed to start embedded HTTP server: " + e.getMessage());
        }
    }

    @Override
    public void unregisterWebServer() {
        if (server != null) {
            server.stop(0);
            Tps.getLog().info("DefaultWebAdapter: Stopped embedded HTTP server");
        } else {
            Tps.getLog().warning("DefaultWebAdapter: No server instance found to stop");
        }
    }

    private HttpServer createHttpsServer(WebServerConfig config) throws NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException, OperatorCreationException, KeyStoreException, UnrecoverableKeyException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair keyPair = kpg.generateKeyPair();

        X500Name issuer = new X500Name("CN=localhost");
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime() + 365L * 24 * 3600 * 1000); // 1 year
        X509v3CertificateBuilder builder = new X509v3CertificateBuilder(issuer, serial, notBefore, notAfter, issuer, SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()));

        var signer = new JcaContentSignerBuilder("SHA256WithRSA").build(keyPair.getPrivate());
        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(builder.build(signer));

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, null);

        var pass  = Utilities.randomPassword(100);
        ks.setKeyEntry("alias", keyPair.getPrivate(), pass, new java.security.cert.Certificate[]{cert});

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, pass);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        var server = HttpsServer.create(new InetSocketAddress(config.getBindIP(), config.getPort()), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                params.setSSLParameters(sslContext.getDefaultSSLParameters());
            }
        });
        return server;
    }


    private  HttpServer createHttpServer(WebServerConfig config) throws IOException {
        return HttpServer.create(new InetSocketAddress(config.getBindIP(), config.getPort()), 0);
    }

}
