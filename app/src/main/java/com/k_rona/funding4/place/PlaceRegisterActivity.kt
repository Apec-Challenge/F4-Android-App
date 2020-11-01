package com.k_rona.funding4.place

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.data.Funding
import com.k_rona.funding4.data.LodgingPlace
import com.k_rona.funding4.data.RegisterPlace
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_place_register.*
import kotlinx.android.synthetic.main.fragment_surround_place.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class PlaceRegisterActivity : AppCompatActivity() {
    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Server.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)


    var placeID: String = ""
    var placeTitle: String = ""
    var placeAddress: String = ""
    var placeLatitude: String = ""
    var placeLongitude: String = ""

    var placeImageUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 업로드를 위한 사진이 선택 및 편집되면 Uri 형태로 결과가 반환됨
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                val bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
                placeImageUri = bitmapToFile(bitmap!!) // Uri
                place_image.setImageURI(placeImageUri)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e("Error Image Selecting", "이미지 선택 및 편집 오류")
            }
        }
    }

    /**  Bitmap 이미지를 Local에 저장하고, URI를 반환함  **/
    private fun bitmapToFile(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(this)

        // Bitmap 파일 저장을 위한 File 객체
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "write_image.jpg")
        try {
            // Bitmap 파일을 JPEG 형태로 압축해서 출력
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Error Saving Image", e.message)
        }
        return Uri.parse(file.absolutePath)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_register)
        val bundle = intent.extras

        bundle?.let {
            placeID = it.getString("place_id")!!
            placeTitle = it.getString("place_title")!!
            placeAddress = it.getString("place_address")!!
            placeLatitude = it.getString("place_latitude")!!
            placeLongitude = it.getString("place_longitude")!!
        }

        place_description_edit_text.setText(placeTitle)

        place_image.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("이미지 추가")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setCropMenuCropButtonTitle("완료")
                .setRequestedSize(1280, 900)
                .start(this)
        }

        submit_button.setOnClickListener {
            when {
                place_name_edit_text.text.toString().isEmpty() -> {
                    place_name_edit_text.error = "This field is required."
                }

                else -> {
                    registerPlace()
                }
            }
        }
    }

    private fun registerPlace() {
        val image = File(placeImageUri!!.path.toString())

        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/data"), image)
        val placeImageField: MultipartBody.Part =
            MultipartBody.Part.createFormData("place_image", image.name, requestFile)
        val placeIDField: RequestBody =
            RequestBody.create(MediaType.parse("text/plain"), placeID)
        val placeTitleField: RequestBody =
            RequestBody.create(MediaType.parse("text/plain"), place_name_edit_text.text.toString())
        val placeDescriptionField: RequestBody = RequestBody.create(
            MediaType.parse("text/plain"),
            place_description_edit_text.text.toString()
        )
        val placeAddressField: RequestBody =
            RequestBody.create(MediaType.parse("text/plain"), placeAddress)
        val placeLngField: RequestBody =
            RequestBody.create(MediaType.parse("text/plain"), placeLongitude)
        val placeLatField: RequestBody =
            RequestBody.create(MediaType.parse("text/plain"), placeLatitude)
        val personHygieneRatingField: RequestBody = RequestBody.create(
            MediaType.parse("text/plain"),
            person_hygiene_seekbar.progress.toString()
        )
        val handSanitizerRatingField: RequestBody = RequestBody.create(
            MediaType.parse("text/plain"),
            hand_sanitizer_seekbar.progress.toString()
        )
        val temperatureCheckRatingField: RequestBody = RequestBody.create(
            MediaType.parse("text/plain"),
            temperature_check_seekbar.progress.toString()
        )

        retrofitService.registerPlace(
            placeID = placeIDField,
            placeTitle = placeTitleField,
            place_image = placeImageField,
            placeDescription = placeDescriptionField,
            placeAddress = placeAddressField,
            placeLongitude = placeLngField,
            placeLatitude = placeLatField,
            personHygiene = personHygieneRatingField,
            temperatureCheck = temperatureCheckRatingField,
            handSanitizer = handSanitizerRatingField
        ).enqueue(object : Callback<LodgingPlace> {
            override fun onResponse(
                call: Call<LodgingPlace>,
                response: Response<LodgingPlace>
            ) {
                if(response.code() == 201 && response.body() != null){
                    Log.d("registerPlace()", "Register Success")
                }else{
                    Log.e("registerPlace()", "Error code : " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<LodgingPlace>, t: Throwable) {
                Log.e("registerPlace()", t.message)
            }
        })
    }
}