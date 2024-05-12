package com.example.pos.feature.input_uang_masuk

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos.databinding.DialogSelectResourceBinding
import com.example.pos.databinding.FragmentInputUangMasukBinding
import com.example.pos.util.currentDate
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService


class InputUangMasukFragment : Fragment() {
    private val viewModel: InputUangMasukViewModel by activityViewModels()
    private lateinit var binding: FragmentInputUangMasukBinding
    private var dialog: AlertDialog? = null
    private var imageUri: String = ""
    private var mCurrentPhotoPath: String? = null
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

    private lateinit var imageCapture: ImageCapture
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInputUangMasukBinding.inflate(inflater, container, false)

        initUi()
        onClickEvent()
        return binding.root
    }

    private fun initUi() {
        viewModel.isValidated.observe(viewLifecycleOwner) { isvalidated ->
            if (!isvalidated) {
//                show message error on fragment screen
            }
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
                    date = currentDate(),
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
//                launchCamera()
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

//    private fun launchCamera() {
//        dismissDialog()
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFile())
//        intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
//        intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
//
//        launchCameraActivity.launch(intent)
//
//    }
//
//    private fun dispatchTakePictureIntent() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        try {
//            val mediaPath = createNewImageFile()
//            val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider",
//                mediaPath
//            )
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
//            launchCameraActivity.launch(takePictureIntent)
//        } catch (e: ActivityNotFoundException) {
//            // display error state to the user
//        }
//    }
//
    private var launchSomeActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        var dat = result
        if (result.resultCode
            == AppCompatActivity.RESULT_OK
        ) {
            val data = result.data
            Timber.tag("camera").d("data ${data?.data}")
            if (data != null && data.data != null
            ) {
                val selectedImageUri = data.data
                setImageView(selectedImageUri)
            }
        }
    }

//    private var launchCameraActivity = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        var dat = result
//        if (result.resultCode
//            == AppCompatActivity.RESULT_OK
//        ) {
//            val imageBitmap = result.data?.extras?.get("data") as Bitmap
//            val imagePath = saveImageToStorage(imageBitmap)
//            setImageView(Uri.parse(imagePath))
//
////            val data = result.data
////            if (data != null && data.data != null
////            ) {
////                val selectedImageUri = data.data
////                setImageView(selectedImageUri)
////            }
////            if (data?.data == null) {
////                val bitmap = data?.extras?.get("data") as Int
////            }
//
////            switch(result.resultCode) {
////                case GlobalConstants . IMAGE_CAPTURE :
////                Uri u;
////                if (hasImageCaptureBug()) {
////                    File fi = new File("/sdcard/tmp");
////                    try {
////                        u = Uri.parse(
////                            android.provider.MediaStore.Images.Media.insertImage(
////                                getContentResolver(),
////                                fi.getAbsolutePath(),
////                                null,
////                                null
////                            )
////                        );
////                        if (!fi.delete()) {
////                            Log.i("logMarker", "Failed to delete " + fi);
////                        }
////                    } catch (FileNotFoundException e) {
////                        e.printStackTrace();
////                    }
////                } else {
////                    u = intent.getData();
////                }
////            }
//        }
//    }

    private fun getBitmap(uri: Uri?): Bitmap? {
        var selectedImageBitmap: Bitmap? = null
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
                        uri!!
                    )
                    ImageDecoder.decodeBitmap(source)
                }
            }
            return selectedImageBitmap
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun setImageView(uri: Uri?) {
        val selectedImageBitmap = getBitmap(uri)
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

    private fun getPhotoFile(): Uri {
        val file = File(requireContext().externalCacheDir, "camera.jpg")
        return FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".provider",
            file
        )
    }
//    private fun saveImageToStorage(bitmap: Bitmap): String? {
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
//            Locale.getDefault()).format(Date())
//        val imageFileName = "JPEG_$timeStamp.jpg"
//        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//
//        return try {
//            val imageFile = File(storageDir, imageFileName)
//            val fos = FileOutputStream(imageFile)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
//            fos.close()
//            imageFile.absolutePath
//        } catch (e: IOException) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    @Throws(IOException::class)
//    fun createNewImageFile(): File {
//        // Create an image file name
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "JPEG_${timeStamp}_", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        ).apply {
//            // Save a file: path for use with ACTION_VIEW intents
//            absolutePath
//        }
//    }
}

