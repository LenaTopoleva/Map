package com.lenatopoleva.map.view.markerlistscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lenatopoleva.map.model.data.AppState
import com.lenatopoleva.map.model.data.DataModel
import com.lenatopoleva.map.model.data.MarkerListItem
import com.lenatopoleva.map.model.repository.Repository
import com.lenatopoleva.map.utils.livedata_event.Event
import com.lenatopoleva.map.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.terrakok.cicerone.Router

class MarkerListViewModel(private val repository: Repository<List<DataModel>>, private val router: Router): BaseViewModel<AppState>() {

    private val _liveData: LiveData<AppState> = liveDataForViewToObserve
    private val _openDialogFragmentLiveData = MutableLiveData<Event<MarkerListItem>>()
    private val _saveEditMarkerLiveData = MutableLiveData<Event<Int>>()
    private val _cancelEditMarkerLiveData = MutableLiveData<Event<Int>>()
    private val _removeItemLiveData = MutableLiveData<Event<Int>>()

    lateinit var data: MutableList<DataModel>

    val openDialogFragmentLiveData: LiveData<Event<MarkerListItem>>
        get() = _openDialogFragmentLiveData

    val saveEditMarkerLiveData: LiveData<Event<Int>>
        get() = _saveEditMarkerLiveData

    val cancelEditMarkerLiveData: LiveData<Event<Int>>
        get() = _cancelEditMarkerLiveData

    val removeItemLiveData: LiveData<Event<Int>>
        get() = _removeItemLiveData

    fun subscribe(): LiveData<AppState> {
        return _liveData
    }

    fun handleData(data: List<DataModel>){
        println("handleData")
        this.data = data.toMutableList()
    }

    fun getMarkerList() = data

    fun getItemCount() = data.size

    override fun getData() {
        liveDataForViewToObserve.value = AppState.Loading(null)
        cancelJob()
        viewModelCoroutineScope.launch { getDataFromRepository() }
    }

    private suspend fun getDataFromRepository() =
        withContext(Dispatchers.IO) {
            liveDataForViewToObserve.postValue(AppState.Success(repository.getData()))
    }

    override fun handleError(error: Throwable) {
        liveDataForViewToObserve.postValue(AppState.Error(error))
    }

    override fun onCleared() {
        liveDataForViewToObserve.value = AppState.Success(emptyList())//Set View to original state in onStop
        super.onCleared()
    }

    fun markerListEditItemBtnClicked(layoutPosition: Int, name: String, annotation: String?){
       _openDialogFragmentLiveData.value = Event(MarkerListItem(name, annotation, layoutPosition))
    }

    fun markerListRemoveItemBtnClicked(layoutPosition: Int){
        //Сохрняем изменения в репозитооии
        viewModelCoroutineScope.launch {
            withContext(Dispatchers.IO){
                repository.removeData(layoutPosition)
            }
        }
        // Обновляем закешированные данные
        data.removeAt(layoutPosition)

        //Отображаем изменения
        _removeItemLiveData.value = Event(layoutPosition)
    }

    fun dialogFragmentBtnYesClicked(layoutPosition: Int, name: String, annotation: String?){
        //Сохрняем изменения в репозитооии
        viewModelCoroutineScope.launch {
            withContext(Dispatchers.IO){
                repository.editData(layoutPosition, name, annotation)
            }
        }
        // Обновляем закешированные данные
        data[layoutPosition].name = name
        data[layoutPosition].annotation = annotation
        println("new data: ${data[layoutPosition].name}")

        //Отображаем изменения
        _saveEditMarkerLiveData.value = Event(layoutPosition)
    }

    fun dialogFragmentBtnCancelClicked(){
        _cancelEditMarkerLiveData.value = Event(0)
    }

    override fun backPressed(): Boolean {
        router.exit()
        return true
    }
}