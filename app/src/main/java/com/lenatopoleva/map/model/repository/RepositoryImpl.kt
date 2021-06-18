package com.lenatopoleva.map.model.repository

import com.lenatopoleva.map.model.data.DataModel

class RepositoryImpl: Repository<List<DataModel>> {
    override suspend fun getData(): List<DataModel>? {
        return listOf(DataModel("qqq", "www", 111.0, 333.0))
    }
}