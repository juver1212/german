package com.itp.trackinn.ServiceGeolocation;

import android.util.Log;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApiAdapter {

    public Service getClientService(String baseUrl){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient(new BigDecimal("60")))
                .build();


        return retrofit.create(Service.class);
    }


    public Service getClientServiceSinSSL(String baseUrl){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClientUnsafe(new BigDecimal("60")))
                .build();

        return retrofit.create(Service.class);
    }


    public Service getClientServiceSinSSL(String baseUrl, int seconds){


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClientUnsafe(new BigDecimal(String.valueOf(seconds))))
                .build();

        return retrofit.create(Service.class);
    }


    /*public Service getClientServiceAuthorization(String baseUrl, String token){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                    builder.header("Authorization", "Bearer " + token);
                    return chain.proceed(builder.build());
                })
                .connectTimeout(new BigDecimal("60").intValue(), TimeUnit.SECONDS)
                .readTimeout(new BigDecimal("60").intValue(), TimeUnit.SECONDS)
                .writeTimeout(new BigDecimal("60").intValue(), TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(Service.class);
    }
  */
    private static OkHttpClient getOkHttpClient(BigDecimal timeOutSeconds) {
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if(timeOutSeconds != null){
                builder.connectTimeout(timeOutSeconds.intValue(), TimeUnit.SECONDS);
                builder.readTimeout(timeOutSeconds.intValue(), TimeUnit.SECONDS);
                builder.writeTimeout(timeOutSeconds.intValue(), TimeUnit.SECONDS);
                Log.i("RestApiAdapter", "MODIFICAMOS EL TIEMPO DE ESPERA EN LA RESPUESTA: "+timeOutSeconds.intValue());
            }else{
                Log.i("RestApiAdapter", "TIEMPO DE ESPERA POR DEFAULT");
            }
            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static OkHttpClient getOkHttpClientUnsafe(BigDecimal timeOutSeconds) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            if(timeOutSeconds != null){
                builder.connectTimeout(timeOutSeconds.intValue(), TimeUnit.SECONDS);
                builder.readTimeout(timeOutSeconds.intValue(), TimeUnit.SECONDS);
                builder.writeTimeout(timeOutSeconds.intValue(), TimeUnit.SECONDS);
                Log.i("RestApiAdapter", "MODIFICAMOS EL TIEMPO DE ESPERA EN LA RESPUESTA: "+timeOutSeconds.intValue());
            }else{
                Log.i("RestApiAdapter", "TIEMPO DE ESPERA POR DEFAULT");
            }
            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
