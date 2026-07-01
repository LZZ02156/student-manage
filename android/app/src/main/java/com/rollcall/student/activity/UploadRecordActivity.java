# Android: UploadRecordActivity (simplified)
package com.rollcall.student.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.rollcall.R;
import com.rollcall.network.RetrofitClient;
import com.rollcall.network.RollCallService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadRecordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etStudentId, etStudentName, etClassName, etCourseName;
    private ImageView ivFace;
    private Button btnTakePhoto, btnUpload;
    private String imagePath;
    private String locationStr;
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int REQUEST_TAKE_PHOTO = 101;
    private RollCallService rollCallService;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_record);
        etStudentId = findViewById(R.id.et_student_id);
        etStudentName = findViewById(R.id.et_student_name);
        etClassName = findViewById(R.id.et_class_name);
        etCourseName = findViewById(R.id.et_course_name);
        ivFace = findViewById(R.id.iv_face);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnUpload = findViewById(R.id.btn_upload);
        btnTakePhoto.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        rollCallService = RetrofitClient.getInstance().getRollCallService();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermissions();
        getLocation();
    }

    private void checkPermissions() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
        boolean need = false;
        for (String p : permissions) {
            if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                need = true; break;
            }
        }
        if (need) ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        fusedLocationClient.getLastLocation().addOnSuccessListener(loc -> {
            if (loc != null) locationStr = loc.getLatitude() + "," + loc.getLongitude();
        });
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) == null) return;
        File photoFile = null;
        try { photoFile = createImageFile(); } catch (IOException ex) { Toast.makeText(this, "无法创建文件", Toast.LENGTH_SHORT).show(); }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imagePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            ivFace.setImageURI(Uri.fromFile(new File(imagePath)));
        }
    }

    private void uploadRecord() {
        String studentId = etStudentId.getText().toString().trim();
        String studentName = etStudentName.getText().toString().trim();
        String className = etClassName.getText().toString().trim();
        String courseName = etCourseName.getText().toString().trim();
        if (studentId.isEmpty() || studentName.isEmpty() || className.isEmpty() || courseName.isEmpty()) { Toast.makeText(this, "请完善信息", Toast.LENGTH_SHORT).show(); return; }
        if (imagePath == null) { Toast.makeText(this, "请拍照", Toast.LENGTH_SHORT).show(); return; }
        if (locationStr == null) { Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show(); return; }
        File f = new File(imagePath);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), f);
        MultipartBody.Part body = MultipartBody.Part.createFormData("faceImage", f.getName(), reqFile);
        RequestBody sid = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody sname = RequestBody.create(MediaType.parse("text/plain"), studentName);
        RequestBody cname = RequestBody.create(MediaType.parse("text/plain"), className);
        RequestBody course = RequestBody.create(MediaType.parse("text/plain"), courseName);
        RequestBody loc = RequestBody.create(MediaType.parse("text/plain"), locationStr);
        Call<com.rollcall.util.BaseResponse<String>> call = rollCallService.uploadRollCallRecord(sid, sname, cname, course, loc, body);
        call.enqueue(new Callback<com.rollcall.util.BaseResponse<String>>() {
            @Override
            public void onResponse(Call<com.rollcall.util.BaseResponse<String>> call, Response<com.rollcall.util.BaseResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UploadRecordActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                } else Toast.makeText(UploadRecordActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<com.rollcall.util.BaseResponse<String>> call, Throwable t) {
                Toast.makeText(UploadRecordActivity.this, "上传失败:" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            for (int res : grantResults) if (res != PackageManager.PERMISSION_GRANTED) { Toast.makeText(this, "权限未授予", Toast.LENGTH_SHORT).show(); return; }
            getLocation();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_take_photo) takePhoto();
        else if (v.getId() == R.id.btn_upload) uploadRecord();
    }
}
