package com.derar.libya.favdish.view.activities

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.derar.libya.favdish.R
import com.derar.libya.favdish.databinding.ActivityAddUpdateDishBinding
import com.derar.libya.favdish.databinding.DialogCustomImageSelectionBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding

    private var mImagePath = ""

    private lateinit var getImageFromGallery: ActivityResultLauncher<String?>
    private lateinit var getPhotoFromCamera: ActivityResultLauncher<Void?>


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


            initializeGetImageFromGallery()
            initializeGetPhotoFromCamera()



        setupActionBar()

        mBinding.ivAddDishImage.setOnClickListener(this@AddUpdateDishActivity)
    }
    /**
     * initialize getPhotoFromCamera for take photo from camera
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializeGetPhotoFromCamera() {
        getPhotoFromCamera = registerForActivityResult(
            ActivityResultContracts.TakePicturePreview(),
            ActivityResultCallback {
              setDishImage(it)
             changeAddDishImageToEditImage()
            }
        )
    }

    /**
     * Change add_dish image to be edit_dish image
     */
    private fun changeAddDishImageToEditImage() {
        mBinding.ivAddDishImage.setImageDrawable(
            ContextCompat.getDrawable(this@AddUpdateDishActivity,R.drawable.ic_vector_edit)
        )
    }

    /**
     * Lunch getPhotoFromCamera for take photo from camera
     */
    private fun lunchGetPhotoFromCamera() {
        try {
          getPhotoFromCamera.launch(null)
        } catch (e: Exception) {
            Log.e("getImage","${e.message}")
            Toast.makeText(
                this,
                "Failed to load the image from gallery.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    /**
     * initialize GetImageFromGallery for get image from the gallery
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializeGetImageFromGallery() {
        getImageFromGallery = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {

                //Get dish image from gallery and make id DishImage
               setDishImage(it)

                //Set add_dish to be edit_dish
                changeAddDishImageToEditImage()
            }
        )
    }

    /**
     * Set imageDish to be passed image
     */
    private fun setDishImage(it: Any?) {
        // Set Selected Image URI to the imageView using Glide
        Glide.with(this@AddUpdateDishActivity)
            .load(it)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    @Nullable e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // log exception
                    Log.e("TAG", "Error loading image", e)
                    return false // important to return false so the error placeholder can be placed
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    // TODO Step 6: Get the Bitmap and save it to the local storage and get the Image Path.
                    val bitmap: Bitmap = resource.toBitmap()

                    mImagePath = bitmap.saveImageToInternalStorage()
                    Log.i("ImagePath", mImagePath)
                    return false
                }
            })
            .into(mBinding.ivDishImage)


    }


    /**
     * Lunch getImageFromGallery for go to the gallery and get an image
     */
    private fun lunchGetImageFromGallery() {
        try {
            getImageFromGallery.launch("image/*")
        } catch (e: Exception) {
            Log.e("getImage","${e.message}")
            Toast.makeText(
                this,
                "Failed to load the image from gallery.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onClick(v: View) {

        when (v.id) {

            R.id.iv_add_dish_image -> {

                customImageSelectionDialog()
                return
            }
        }
    }

    /**
     * A function for ActionBar setup.
     */
    private fun setupActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        mBinding.toolbarAddDishActivity.setNavigationOnClickListener { onBackPressed() }
    }


    /**
     * A function to launch the custom image selection dialog.
     */
    private fun customImageSelectionDialog() {
        val dialog = Dialog(this@AddUpdateDishActivity)

        val binding: DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        dialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener {

            // TODO Step 3: Let ask for the permission while selecting the image from camera using Dexter Library. And Remove the toast message.
            // START
            Dexter.withContext(this@AddUpdateDishActivity)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    //Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        // Here after all the permission are granted launch the CAMERA to capture an image.
                        report?.let{
                            if (report.areAllPermissionsGranted()) {
                               lunchGetPhotoFromCamera()
                            }
                        }

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        // TODO Step 6: Show the alert dialog
                        // START
                        showRationalDialogForPermissions()
                        // END
                    }
                }).onSameThread()
                .check()
            // END

            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {

            // TODO Step 7: Ask for the permission while selecting the image from Gallery using Dexter Library. And Remove the toast message.
            Dexter.withContext(this@AddUpdateDishActivity)
                .withPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        // TODO Step 4: Show the Toast message for now just to know that we have the permission.
                        // START
                        lunchGetImageFromGallery()
                        // END
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        // TODO Step 4: Show the Toast message for now just to know that we have the permission.
                        // START
                        Toast.makeText(
                            this@AddUpdateDishActivity,
                            "You have denied the storage permission select image.",
                            Toast.LENGTH_SHORT
                        ).show()
                        // END
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                })
                .check()
            // END
            dialog.dismiss()
        }

        //Start the dialog and display it on screen.
        dialog.show()
    }


    // TODO Step 5: Create a function to show the alert message that the permission is necessary to proceed further if user deny it. And ask him to allow it from setting.
    // START
    /**
     * A function used to show the alert dialog when the permissions are denied and need to allow it from settings app info.
     */
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
    // END

    /**
     * A function to save a copy of an image to internal storage for FavDishApp to use.
     *
     * @param bitmap
     */
    private fun Bitmap.saveImageToInternalStorage():String {
        // Get the context wrapper instance
        val wrapper = ContextWrapper(applicationContext)

        // Initializing a new file
        // The bellow line return a directory in internal storage
        /**
         * The Mode Private here is
         * File creation mode: the default mode, where the created file can only
         * be accessed by the calling application (or all applications sharing the
         * same user ID).
         */
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        // Mention a file name to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            this.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException) { // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image absolute path
        return file.absolutePath
    }



    companion object{
        // Declare a constant variable for directory name to store the images.
        const val IMAGE_DIRECTORY="FavDishImages"
    }
}