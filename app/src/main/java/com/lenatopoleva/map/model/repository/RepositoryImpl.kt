package com.lenatopoleva.map.model.repository

import com.lenatopoleva.map.model.data.DataModel

class RepositoryImpl: Repository<List<DataModel>> {

    var marks = mutableListOf(DataModel("Some place", "-annotation-", 111.111111, 333.333333))

    override suspend fun getData(): List<DataModel> {
        return marks
    }

    override suspend fun saveData(place: DataModel) {
        marks.add(place)
    }

    override suspend fun removeData(position: Int) {
        marks.removeAt(position)
    }

    override suspend fun editData(position: Int, name: String?, annotation: String?) {
        marks[position].name = name ?: ""
        marks[position].annotation = annotation ?: ""
    }
}