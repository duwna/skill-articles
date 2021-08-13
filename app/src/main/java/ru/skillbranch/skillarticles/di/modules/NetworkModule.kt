package ru.skillbranch.skillarticles.di.modules

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.skillbranch.skillarticles.AppConfig
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.providers.adapters.DateAdapter
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.interceptors.ErrorStatusInterceptor
import ru.skillbranch.skillarticles.data.remote.interceptors.NetworkStatusInterceptor
import ru.skillbranch.skillarticles.data.remote.interceptors.TokenAuthenticator
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideRestService(retrofit: Retrofit): RestService {
        return retrofit.create(RestService::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(AppConfig.BASE_URL)
        .build()

    @Singleton
    @Provides
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        authenticator: TokenAuthenticator,
        statusInterceptor: NetworkStatusInterceptor,
        errorStatusInterceptor: ErrorStatusInterceptor
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
            .readTimeout(2, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .authenticator(authenticator)
            .addInterceptor(statusInterceptor)
            .addInterceptor(logging)
            .addInterceptor(errorStatusInterceptor)
            .build()
    }


    @Singleton
    @Provides
    fun provideLogging(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun provideNetworkStatusInterceptor(monitor: NetworkMonitor): NetworkStatusInterceptor {
        return NetworkStatusInterceptor(monitor)
    }

    @Singleton
    @Provides
    fun provideErrorStatusInterceptor(moshi: Moshi): ErrorStatusInterceptor {
        return ErrorStatusInterceptor(moshi)
    }

    @Singleton
    @Provides
    fun provideTokenAuthenticator(
        prefs: PrefManager, lazyApi: Lazy<RestService>
    ): TokenAuthenticator {
        return TokenAuthenticator(prefs, lazyApi)
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(DateAdapter)
        .add(KotlinJsonAdapterFactory())
        .build()
}