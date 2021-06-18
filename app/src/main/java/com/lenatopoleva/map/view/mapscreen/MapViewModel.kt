package com.lenatopoleva.map.view.mapscreen

import androidx.lifecycle.LiveData
import com.lenatopoleva.map.model.data.AppState
import com.lenatopoleva.map.model.data.DataModel
import com.lenatopoleva.map.model.repository.Repository
import com.lenatopoleva.map.model.repository.RepositoryImpl
import com.lenatopoleva.map.navigation.Screens
import com.lenatopoleva.map.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.terrakok.cicerone.Router

class MapViewModel (private val repository: Repository<List<DataModel>>, private val router: Router): BaseViewModel<AppState>(){

    private val liveData: LiveData<AppState> = liveDataForViewToObserve

    fun subscribe(): LiveData<AppState> {
        return liveData
    }

    fun markersMenuItemClicked(){
        router.navigateTo(Screens.MarkerListScreen())
    }

    override fun backPressed(): Boolean {
        router.exit()
        return true
    }

    override fun getData() {
        liveDataForViewToObserve.value = AppState.Loading(null)
        cancelJob()
        viewModelCoroutineScope.launch {
            getDataFromRepository()
        }
    }

    private suspend fun getDataFromRepository() =
        withContext(Dispatchers.IO) {
            liveDataForViewToObserve.postValue(AppState.Success(repository.getData()))
        }

    fun makeAMark(latitude: Double, longitude: Double) {
        viewModelCoroutineScope.launch {
            withContext(Dispatchers.IO){
                repository.saveData(DataModel("New place", "-annotation-", latitude, longitude))
                print("New mark added; latitude: $latitude, longitude: $longitude")
                liveDataForViewToObserve.postValue(AppState.Success(repository.getData()))
            }
        }
    }

    override fun handleError(error: Throwable) {
        liveDataForViewToObserve.postValue(AppState.Error(error))
    }
}