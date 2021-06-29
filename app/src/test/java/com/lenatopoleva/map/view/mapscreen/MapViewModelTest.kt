package com.lenatopoleva.map.view.mapscreen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.lenatopoleva.map.model.data.AppState
import com.lenatopoleva.map.model.data.DataModel
import com.lenatopoleva.map.model.repository.RepositoryImpl
import com.nhaarman.mockito_kotlin.*
import com.nhaarman.mockito_kotlin.any
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.bytebuddy.implementation.bytecode.Throw
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import ru.terrakok.cicerone.Router
import java.io.IOException


class MapViewModelTest{

    // Для тестирования LiveData рекомендуется использовать это Правило для получения
    // консистентных результатов (иначе порядок выполнения кода не будет гарантироваться).
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Правило для тестирования корутин, упрощает тестирование асинхронных задач
    // и выполняет их сразу же и последовательно.
    @ExperimentalCoroutinesApi
    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    private lateinit var mapViewModel: MapViewModel

    @Mock
    private lateinit var repository: RepositoryImpl

    @Mock
    private lateinit var router: Router


    @ExperimentalCoroutinesApi
    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
        mapViewModel = MapViewModel(repository, router,
            DispatcherProviderStub(testCoroutineRule.testCoroutineDispatcher))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getData_invokesRepositoryGetData(){
        testCoroutineRule.runBlockingTest {
            Mockito.`when`(repository.getData()).thenReturn(
                listOf()
            )
            mapViewModel.getData()
            verify(repository, times(1)).getData()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getData_putValueToLiveData(){
        testCoroutineRule.runBlockingTest {
            //Создаем обсервер. В лямбде мы не вызываем никакие методы - в этом нет необходимости
            //так как мы проверяем работу LiveData и не собираемся ничего делать с данными, которые она возвращает
            val observer = Observer<AppState> {}
            //Получаем LiveData
            val liveData = mapViewModel.subscribe()

            Mockito.`when`(repository.getData()).thenReturn(
                listOf()
            )

            try {
                //Подписываемся на LiveData без учета жизненного цикла
                liveData.observeForever(observer)
                mapViewModel.getData()
                //Убеждаемся, что Репозиторий вернул данные и LiveData передала их Наблюдателям
                Assert.assertNotNull(liveData.value)
            } finally {
                //Тест закончен, снимаем Наблюдателя
                liveData.removeObserver(observer)
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getData_givenValueEqualToReceivedValue(){
        testCoroutineRule.runBlockingTest {
            val observer = Observer<AppState> {}
            val liveData = mapViewModel.subscribe()
            val testData = DataModel("a", "a", 1.0, 1.0)

            Mockito.`when`(repository.getData()).thenReturn(listOf(testData))

            try {
                liveData.observeForever(observer)
                mapViewModel.getData()
                val value: AppState.Success = liveData.value as AppState.Success
                Assert.assertEquals(value.data.first().name, testData.name)
            } finally {
                liveData.removeObserver(observer)
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun makeAMark_invokeRepositoryGetData(){
        testCoroutineRule.runBlockingTest {
            Mockito.`when`(repository.getData()).thenReturn(any())
            mapViewModel.makeAMark(1.0, 1.0)
            verify(repository, times(1)).getData()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun makeAMark_invokeRepositorySaveData(){
        val testData = DataModel("New place", "-annotation-", 1.0, 1.0)
        testCoroutineRule.runBlockingTest {
            Mockito.`when`(repository.saveData(any())).thenReturn(Unit)
            mapViewModel.makeAMark(1.0, 1.0)
            verify(repository, times(1)).saveData(any())

        }
    }

    @Test
    fun handleError_postValueToLivaData(){
        val testError = Throwable("error")
        val observer = Observer<AppState> {}
        val liveData = mapViewModel.subscribe()
        try {
            liveData.observeForever(observer)
            mapViewModel.handleError(testError)
            val value: AppState.Error = liveData.value as AppState.Error
            Assert.assertEquals(value.error.message, testError.message)
        } finally {
            liveData.removeObserver(observer)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun makeAMark_postValueToLiveData(){
        testCoroutineRule.runBlockingTest {
            val testData = DataModel("New place", "-annotation-", 1.0, 1.0)
            val observer = Observer<AppState> {}
            val liveData = mapViewModel.subscribe()
            Mockito.`when`(repository.getData()).thenReturn(listOf(testData))
            try {
                liveData.observeForever(observer)
                mapViewModel.makeAMark(1.0, 1.0)
                val value: AppState.Success = liveData.value as AppState.Success
                Assert.assertEquals(value.data.first().latitude, testData.latitude, 0.0)
            } finally {
                liveData.removeObserver(observer)
            }
        }
    }

    @Test
    fun backPress_invokeRouterExitMethod(){
        mapViewModel.backPressed()
        verify(router, times(1)).exit()
    }
}