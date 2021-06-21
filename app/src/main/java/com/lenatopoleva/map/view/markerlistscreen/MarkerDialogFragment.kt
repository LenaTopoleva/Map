package com.lenatopoleva.map.view.markerlistscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.lenatopoleva.map.R
import com.lenatopoleva.map.utils.livedata_event.Event
import kotlinx.android.synthetic.main.dialog_fragment_marker.*
import org.koin.android.viewmodel.ext.android.getViewModel

class MarkerDialogFragment: DialogFragment() {

    private val model by lazy { requireParentFragment().getViewModel<MarkerListViewModel>() }
    private val closeDialogObserver = Observer<Event<Int>> { closeDialog() }

    companion object {
        fun newInstance(listPosition: Int?, name: String?, annotation: String?) = MarkerDialogFragment().apply {
            arguments = Bundle().apply {
                if (listPosition != null) {
                    putInt(MARKER_POSITION, listPosition)
                }
                putString(NAME, name)
                putString(ANNOTATION, annotation)
            }
        }
        const val MARKER_POSITION = "markerPosition"
        private const val NAME = "name"
        private const val ANNOTATION = "annotation"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.setTitle("Marker")
        val v = View.inflate(context, R.layout.dialog_fragment_marker, null)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        btnYes.setOnClickListener{
            arguments?.getInt(
                MARKER_POSITION)?.let { position ->
                model.dialogFragmentBtnYesClicked(
                    position,
                    marker_name_edit_text.text.toString(),
                    marker_annotation_edit_text.text.toString())
            }
            closeDialog() // Переместить логику в модель
        }
        btnCancel.setOnClickListener{
            model.dialogFragmentBtnCancelClicked()
            closeDialog() // Переместить логику в модель
        }
        marker_name_edit_text.setText(arguments?.getString(NAME))
        marker_annotation_edit_text.setText(arguments?.getString(ANNOTATION))
    }

    private fun closeDialog() {
        this.dismiss()
    }
}