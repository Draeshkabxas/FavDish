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
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.derar.libya.favdish.R
import com.derar.libya.favdish.application.FavDishApplication
import com.derar.libya.favdish.databinding.ActivityAddUpdateDishBinding
import com.derar.libya.favdish.databinding.DialogCustomImageSelectionBinding
import com.derar.libya.favdish.databinding.DialogCustomListBinding
import com.derar.libya.favdish.model.entities.FavDish
import com.derar.libya.favdish.view.adapters.CustomListItemAdapter
import com.derar.libya.favdish.viewmodel.FavDishViewModel
import com.derar.libya.favdish.viewmodel.FavDishViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.tutorials.eu.favdish.utils.Constants
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

    private val mFavDishViewModel: FavDishViewModel by viewModels{
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    private lateinit var mCustomListDialog: Dialog


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        initializeGetImageFromGallery()
        initializeGetPhotoFromCamera()



        setupActionBar()

        mBinding.ivAddDishImage.setOnClickListener(this@AddUpdateDishActivity)

        mBinding.etType.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)

        mBinding.btnAddDish.setOnClickListener(this)
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
            ContextCompat.getDrawable(this@AddUpdateDishActivity, R.drawable.ic_vector_edit)
        )
    }

    /**
     * Lunch getPhotoFromCamera for take photo from camera
     */
    private fun lunchGetPhotoFromCamera() {
        try {
            getPhotoFromCamera.launch(null)
        } catch (e: Exception) {
            Log.e("getImage", "${e.message}")
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
            Log.e("getImage", "${e.message}")
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

            R.id.et_category ->{
                customItemsListDialog(
                    resources.getString(R.string.title_select_dish_category),
                    Constants.dishCategories(),
                    Constants.DISH_CATEGORY)
                return
            }
            R.id.et_type->{
                customItemsListDialog(
                    resources.getString(R.string.title_select_dish_type),
                    Constants.dishTypes(),
                    Constants.DISH_TYPE)
                return
            }
            R.id.et_cooking_time ->{
                customItemsListDialog(
                    resources.getString(R.string.title_select_dish_cooking_time),
                    Constants.dishCookTime(),
                    Constants.DISH_COOKING_TIME)

                return
            }

            R.id.btn_add_dish ->{

                /**
                 * Wrap each entry with its error for show the error if the entry is empty
                 */
                val title = mBinding.etTitle.text.toString().trim { it <= ' ' } to
                        resources.getString(R.string.err_msg_enter_dish_title)
                val type= mBinding.etType.text.toString().trim { it <= ' ' } to
                        resources.getString(R.string.err_msg_select_dish_type)
                val category = mBinding.etCategory.text.toString().trim { it <= ' ' }to
                        resources.getString(R.string.err_msg_select_dish_category)
                val cookingTime= mBinding.etCookingTime.text.toString().trim { it <= ' ' }to
                        resources.getString(R.string.err_msg_select_dish_cooking_time)
                val directionToCook = mBinding.etDirectionToCook.text.toString().trim { it <= ' ' }to
                        resources.getString(R.string.err_msg_enter_dish_cooking_instructions)
                val ingredients = mBinding.etIngredients.text.toString().trim { it <= ' ' }to
                        resources.getString(R.string.err_msg_enter_dish_ingredients)
                val image = mImagePath to resources.getString(R.string.err_msg_select_dish_image)

                /**
                 * Add all entries to dishDetailsWithMassageError
                 */
                val dishDetailsWithMassageError = mapOf<String,String>(
                    title,
                    type,
                    category,
                    cookingTime,
                    directionToCook,
                    ingredients,
                    image
                )

                /**
                 * check if all entries not empty then save the dish
                 * if any entry is empty show its error as toast to user
                 */
                if (checkMissingDishDetails(dishDetailsWithMassageError)){
                    val favDishDetails:FavDish = FavDish(
                        image.first,
                        Constants.DISH_IMAGE_SOURCE_LOCAL,
                        title.first,
                        type.first,
                        category.first,
                        ingredients.first,
                        cookingTime.first,
                        directionToCook.first,
                        false
                    )
                    mFavDishViewModel.insert(favDishDetails)
                    Toast.makeText(this,
                        "You successfully added your favorite dish details."
                        ,Toast.LENGTH_SHORT).show()
                    Log.d("Insertion","Successfully")
                    finish()
                }
            }
        }
    }
    /**
     * check if passed all map keys not empty
     * if all not empty the return true
     * if any key is empty show its value as toast to user and return false
     * @param map the map that will check
     */
    private fun checkMissingDishDetails(map:Map<String,String>):Boolean{
        var result:Boolean =true
       map.forEach { item ->
           if (TextUtils.isEmpty(item.key)){
               Toast.makeText(this,item.value,Toast.LENGTH_SHORT).show()
               result =false
               return@forEach
           }
       }
        return result
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
                        report?.let {
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
    private fun Bitmap.saveImageToInternalStorage(): String {
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


    fun selectedListItem(
        item:String  , selection: String
    ) {
        mCustomListDialog.dismiss()
        when (selection) {
            Constants.DISH_TYPE -> {
                mBinding.etType.setText(item)
            }

            Constants.DISH_CATEGORY -> {
                mBinding.etCategory.setText(item)
            }
            Constants.DISH_COOKING_TIME -> {
                mBinding.etCookingTime.setText(item)
            }
        }
    }


    /**
     * A function to launch the custom list dialog.
     *
     * @param title - Define the title at runtime according to the list items.
     * @param itemsList - List of items to be selected.
     * @param selection - By passing this param you can identify the list item selection.
     */
    private fun customItemsListDialog(title: String, itemsList: List<String>, selection: String) {
        mCustomListDialog = Dialog(this@AddUpdateDishActivity)

        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mCustomListDialog.setContentView(binding.root)

        binding.tvTitle.text = title

        // Set the LayoutManager that this RecyclerView will use.
        binding.rvList.layoutManager = LinearLayoutManager(this@AddUpdateDishActivity)
        // Adapter class is initialized and list is passed in the param.
        val adapter = CustomListItemAdapter(this@AddUpdateDishActivity, itemsList, selection)
        // adapter instance is set to the recyclerview to inflate the items.
        binding.rvList.adapter = adapter
        //Start the dialog and display it on screen.
        mCustomListDialog.show()
    }


    companion object {
        // Declare a constant variable for directory name to store the images.
        const val IMAGE_DIRECTORY = "FavDishImages"
    }
}