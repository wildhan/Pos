package com.example.pos.feature.edit_uang_masuk

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos.data.model.local.Record
import com.example.pos.databinding.DialogSelectResourceBinding
import com.example.pos.databinding.FragmentEditUangMasukBinding
import com.example.pos.util.toUri
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class EditUangMasukFragment : Fragment() {
    private val viewModel: EditUangMasukViewModel by activityViewModels()
    private lateinit var binding: FragmentEditUangMasukBinding
    private var dialog: AlertDialog? = null
    private var imageUri: String = ""
    private var currentDate: Long? = null

    private val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditUangMasukBinding.inflate(inflater, container, false)
        initUi()
        onClickEvent()
        return binding.root
    }


    private fun initUi() {
        val id = arguments?.getInt("id")
        viewModel.isValidated.observe(viewLifecycleOwner) { isvalidated ->
            if (!isvalidated) {
//                show message error on fragment screen
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            id?.let {
                viewModel.getRecord(it).observe(viewLifecycleOwner) { records ->
                    viewModel.setupRecord(records?.get(0))
                    initRecords(records?.get(0))
                }
            }
        }
    }

    private fun initRecords(records: Record?) {
        binding.apply {
            tietTo.setText(records?.to)
            tietFrom.setText(records?.from)
            tietTotal.setText(records?.total)
            tietNote.setText(records?.note)
            tietType.setText(records?.type)
            currentDate = records?.date
//            if (!records?.imageUri.isNullOrEmpty())
//                setImageView(records?.imageUri?.toUri())
        }
    }

    private fun onClickEvent() {
        binding.apply {
            btnSave.setOnClickListener {
                viewModel.insert(
                    to = tietTo.text.toString(),
                    from = tietFrom.text.toString(),
                    total = tietTotal.text.toString(),
                    note = tietNote.text.toString(),
                    type = tietType.text.toString(),
                    date = currentDate,
                    imageUri = imageUri
                )
                findNavController().navigateUp()
            }
            cvImage.setOnClickListener {
                if (imageUri.isEmpty())
                    checkPermissionGallery()
            }
            btnImgEdit.setOnClickListener {
                checkPermissionGallery()
            }
            btnImgDelete.setOnClickListener {
                imageUri = ""
                ivPhoto.setImageBitmap(null)
                llButton.visibility = View.GONE
            }
        }
    }

    private fun showSelectDialog() {
        val dialogView = DialogSelectResourceBinding.inflate(layoutInflater)
        dialog = AlertDialog.Builder(this.requireContext())
            .setView(dialogView.root)
            .create()

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        with(dialogView) {
            ivCamera.setOnClickListener {

            }
            ivGallery.setOnClickListener {
                imageChooser()
            }
        }

        dialog!!.show()

    }

    private fun dismissDialog() {
        if (dialog!!.isShowing)
            dialog!!.dismiss()
    }

    private fun imageChooser() {
        dismissDialog()
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)

        launchSomeActivity.launch(intent)
    }

    private var launchSomeActivity
            : ActivityResultLauncher<Intent> = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode
            == AppCompatActivity.RESULT_OK
        ) {
            val data = result.data
            // do your operation from here....
            if (data != null && data.data != null
            ) {
                val selectedImageUri = data.data
                setImageView(selectedImageUri)
            }
        }
    }

    private fun setImageView(uri: Uri?) {
        var selectedImageBitmap: Bitmap? = null
        if (uri != null) {
            try {
                selectedImageBitmap = when {
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.P -> {
                        MediaStore.Images.Media.getBitmap(
                            this.requireContext().contentResolver,
                            uri
                        )
                    }

                    else -> {
                        val source = ImageDecoder.createSource(
                            this.requireContext().contentResolver,
                            uri
                        )
                        ImageDecoder.decodeBitmap(source)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (selectedImageBitmap != null) {
            imageUri = uri.toString()
            binding.apply {
                ivPhoto.setImageBitmap(selectedImageBitmap)
                llButton.visibility = View.VISIBLE
            }
        } else {
            Toast.makeText(requireContext(), "Gagal menampilkan gambar", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun checkPermissionGallery() {
        when {
            hasPermissions(requireContext(), permission) -> {
                showSelectDialog()
            }

            showPermissionRationale(permission)
            -> {
                Snackbar.make(binding.root, "Permission Denied", Snackbar.LENGTH_SHORT)
            }

            else -> {
                requestMultiplePermissions.launch(
                    permission
                )
            }
        }
    }

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all {
            it.value
        }
        if (granted) {
            showSelectDialog()
        } else {
            Snackbar.make(binding.root, "Permission Denied", Snackbar.LENGTH_SHORT)
        }
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun showPermissionRationale(permissions: Array<String>): Boolean =
        permissions.all {
            shouldShowRequestPermissionRationale(it)
        }
}