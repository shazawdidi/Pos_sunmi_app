package com.altkamul.xpay.di

import com.altkamul.xpay.api.ApiClientImp
import com.altkamul.xpay.db.DataAccessObject
import com.altkamul.xpay.repositroy.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object DependenciesInjectionModule {

    // Provide SplashRepository Instance
    @ViewModelScoped
    @Provides
    fun providesSplashScreenRepositoryInstance(dataAccessObject: DataAccessObject) =
        SplashRepository(dataAccessObject)

    // Provide LoginRepository Instance
    @ViewModelScoped
    @Provides
    fun providesLoginRepositoryInstance(
        apiClient: ApiClientImp,
        dataAccessObject: DataAccessObject,
    ) =
        LoginRepository(api = apiClient, dao = dataAccessObject)

    // Provide ParentRepository Instance
    @ViewModelScoped
    @Provides
    fun providesParentRepositoryInstance(
        apiClient: ApiClientImp,
        dataAccessObject: DataAccessObject,
    ) =
        ParentRepository(api = apiClient, dao = dataAccessObject)

    // PROVIDE LOADING REPOSITORY INSTANCE
    @ViewModelScoped
    @Provides
    fun providesLoadingRepositoryInstance(
        apiClient: ApiClientImp,
        dataAccessObject: DataAccessObject,
    ) =
        LoadingRepository(api = apiClient, dataAccessObject = dataAccessObject)

    // Provide ContactUs Repository
    @ViewModelScoped
    @Provides
    fun providesContactUsRepository(dataAccessObject: DataAccessObject) =
        ContactUsRepository(dataAccessObject = dataAccessObject)

    // Provide Change Branch Repository
    @ViewModelScoped
    @Provides
    fun providesChangeBranchRepository(
        dataAccessObject: DataAccessObject,
        apiClient: ApiClientImp
    ) =
        ChangeBranchRepository(dataAccessObject = dataAccessObject, apiClientImp = apiClient)

    // Provide Claim Repository
    @ViewModelScoped
    @Provides
    fun providesClaimRepository(apiClient: ApiClientImp, dataAccessObject: DataAccessObject) =
        ClaimRepository(apiClientImp = apiClient, dataAccessObject = dataAccessObject)

    @ViewModelScoped
    @Provides
    fun providesAccountRepository(apiClient: ApiClientImp, dataAccessObject: DataAccessObject) =
        AccountRepository(apiClient, dataAccessObject)

    @ViewModelScoped
    @Provides
    fun providesChangePasswordRepository(
        dataAccessObject: DataAccessObject,
        apiClient: ApiClientImp
    ) =
        ChangePasswordRepository(dataAccessObject, apiClient)

    @Singleton
    @Provides
    fun provideBackgroundTasksRepository(
        apiClient: ApiClientImp,
        dataAccessObject: DataAccessObject
    ) = BackgroundTasksRepository(dataAccessObject, apiClient)
}