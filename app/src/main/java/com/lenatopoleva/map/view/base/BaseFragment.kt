package com.lenatopoleva.map.view.base

import android.view.View
import androidx.fragment.app.Fragment
import com.lenatopoleva.map.R
import com.lenatopoleva.map.model.data.AppState
import com.lenatopoleva.map.model.data.DataModel
import com.lenatopoleva.map.utils.ui.AlertDialogFragment
import com.lenatopoleva.map.viewmodel.BaseViewModel
import kotlinx.android.synthetic.main.loading_layout.*

abstract class BaseFragment<T : AppState> : Fragment() {

    companion object {
        private const val DIALOG_FRAGMENT_TAG = "74a54328-5d62-46bf-ab6b-cbf5d8c79522"
    }

    abstract val model: BaseViewModel<T>

    protected fun renderData(appState: T) {
        when (appState) {
            is AppState.Success -> {
                showViewWorking()
                appState.data?.let {
                    if (it.isEmpty()) {
                        showAlertDialog(
                            getString(R.string.dialog_tittle_sorry),
                            getString(R.string.empty_server_response_on_success)
                        )
                    } else {
                        handleData(it)
                    }
                } ?: showAlertDialog(
                    getString(R.string.dialog_tittle_sorry),
                    getString(R.string.empty_server_response_on_success)
                )
            }
            is AppState.Loading -> {
                showViewLoading()
                if (appState.progress != null) {
                    progress_bar_horizontal.visibility = View.VISIBLE
                    progress_bar_round.visibility = View.GONE
                    progress_bar_horizontal.progress = appState.progress
                } else {
                    progress_bar_horizontal.visibility = View.GONE
                    progress_bar_round.visibility = View.VISIBLE
                }
            }
            is AppState.Error -> {
                showViewWorking()
                showAlertDialog(getString(R.string.error_stub), appState.error.message)
            }
        }
    }

    private fun showViewWorking() {
        loading_frame_layout.visibility = View.GONE
    }

    private fun showViewLoading() {
        loading_frame_layout.visibility = View.VISIBLE
    }

    protected fun showAlertDialog(title: String?, message: String?) {
        AlertDialogFragment.newInstance(title, message).show(requireFragmentManager(), DIALOG_FRAGMENT_TAG)
    }

    abstract fun handleData(data: List<DataModel>)

}