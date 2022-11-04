package com.minikode.summit.di

import com.minikode.summit.repository.LocationRepository
import com.minikode.summit.repository.SensorRepository
import com.minikode.summit.repository.SummitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun providerSummitRepository() = SummitRepository()

    @Provides
    @Singleton
    fun providerSensorRepository() = SensorRepository()

    @Provides
    @Singleton
    fun providerLocationRepository() = LocationRepository()

}