package com.derar.libya.favdish.view.activities

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding

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

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializeGetPhotoFromCamera() {
        getPhotoFromCamera = registerForActivityResult(
            ActivityResultContracts.TakePicturePreview(),
            ActivityResultCallback {
              mBinding.ivDishImage.setImageBitmap(it)
             changeAddDishImageToEditImage()
            }
        )
    }

    private fun changeAddDishImageToEditImage() {
        mBinding.ivAddDishImage.setImageDrawable(
            ContextCompat.getDrawable(this@AddUpdateDishActivity,R.drawable.ic_vector_edit)
        )
    }


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

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializeGetImageFromGallery() {
        getImageFromGallery = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                mBinding.ivDishImage.setImageURI(it)
                changeAddDishImageToEditImage()
            }
        )
    }

    /**
     *
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
}