package com.todo.todoapp.services;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.todo.todoapp.BuildConfig;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = BuildConfig.API_URL;

    public static Retrofit getRetrofitInstance(final Context context) {
        if (retrofit == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(context));

            // Aceita o certificado local para desenvolvimento
            if (BuildConfig.DEBUG) {
                try {
                    TrustManager[] trustAllCerts = new TrustManager[]{
                            new X509TrustManager() {
                                @Override
                                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                                @Override
                                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                                @Override
                                public X509Certificate[] getAcceptedIssuers() {
                                    return new X509Certificate[0];
                                }
                            }
                    };

                    SSLContext sslContext = SSLContext.getInstance("SSL");
                    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                    clientBuilder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                    clientBuilder.hostnameVerifier((hostname, session) -> true); // Ignora a verificação de hostname
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context1) ->
                            LocalDate.parse(json.getAsString()))
                    .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, type, context1) ->
                            new com.google.gson.JsonPrimitive(date.toString()))
                    .create();

            OkHttpClient client = clientBuilder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
