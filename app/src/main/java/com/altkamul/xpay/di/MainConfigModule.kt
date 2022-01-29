package com.altkamul.xpay.di

import android.content.Context
import androidx.room.Room
import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainConfigModule {

    // Provide Context Instance
    @Singleton
    @Provides
    fun providesContextInstance(@ApplicationContext context: Context) = context

    // Provide KTOR Instance
    @Singleton
    @Provides
    fun providesKTORInstance() = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        engine {
            connectTimeout = 20000
            socketTimeout = 10000
        }
    }

    // Provide DataBase Instance
    @Singleton
    @Provides
    fun providerDataBaseInstance(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            DataBase::class.java,
            "XPayDataBase"
        ).build()

    // Provide DataAccessObject Instance
    @Singleton
    @Provides
    fun providerDataAccessObjectInstance(dataBase: DataBase) = dataBase.getDataAccessObject()

    // Provide ApiClientImp instance
    @Singleton
    @Provides
    fun provideApiClientImpInstance(httpClient: HttpClient) = ApiClientImp(client = httpClient)
}