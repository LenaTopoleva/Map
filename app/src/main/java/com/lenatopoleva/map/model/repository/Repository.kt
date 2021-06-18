package com.lenatopoleva.map.model.repository

import com.lenatopoleva.map.model.data.DataModel

interface Repository<T> {
    suspend fun getData(): List<DataModel>?
    suspend fun saveData(place: DataModel) {}
}