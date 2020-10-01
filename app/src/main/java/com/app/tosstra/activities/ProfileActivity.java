package com.app.tosstra.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.tosstra.R;
import com.app.tosstra.fragments.dispacher.SearchActivity;
import com.app.tosstra.models.GenricModel;
import com.app.tosstra.models.Profile;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.PreferenceHandler;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_back;
    private EditText etName, etLast, etEmail, etDot, etPhone;
    private ImageView ivEdit;
    private Button bt_save;
    private TextView tv_userType, tvName;
    public static TextView etAddress;
    private CircleImageView ivProfilePic;
    public static LatLng latit;

    private String filename = "";
    private Bitmap bitmap;
    private File finalFile;
    private MultipartBody.Part image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initUI();
        hitProfileViewAPI();
    }

    private void initUI() {
        ivProfilePic = findViewById(R.id.ivProfilePic);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        etName = findViewById(R.id.etName);
        ivEdit = findViewById(R.id.ivEdit);
        etLast = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etDot = findViewById(R.id.edtName);
        tv_userType = findViewById(R.id.tv_userType);
        tvName = findViewById(R.id.tv_name_activity);
        bt_save = findViewById(R.id.bt_save);
        bt_save.setOnClickListener(this);
        ivEdit.setOnClickListener(this);
        ivProfilePic.setOnClickListener(this);
        ivProfilePic.setClickable(false);
        etAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ivEdit:
                etPhone.setEnabled(true);
                etName.setEnabled(true);
                etLast.setEnabled(true);
                etEmail.setEnabled(true);
                etAddress.setEnabled(true);
                etDot.setEnabled(true);
                ivProfilePic.setClickable(true);
                bt_save.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_save:
                hitEditProfileAPI();
                break;
            case R.id.ivProfilePic:
                checkPermissions();
                break;
            case R.id.etAddress:
                Intent i = new Intent(ProfileActivity.this, SearchActivity.class);
                i.putExtra("pickDrop", "profile");
                startActivity(i);
                break;
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ProfileActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                return;
            } else {
                take_Picture();
            }
        } else
            take_Picture();
    }

    private void take_Picture() {
        final CharSequence[] options =
                {getString(R.string.take_photo),
                        (getString(R.string.choose_from_gallery)),
                        (getString(R.string.cancel))};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setCancelable(false);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getString(R.string.take_photo))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals(getString(R.string.choose_from_gallery))) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals((getString(R.string.cancel)))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void hitProfileViewAPI() {
        final Dialog dialog = AppUtil.showProgress(ProfileActivity.this);
        Interface service = CommonUtils.retroInit();
        Call<Profile> call = service.view_profile(PreferenceHandler.readString(ProfileActivity.this, PreferenceHandler.USER_ID, ""));
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Profile data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    etPhone.setText(data.getData().get(0).getPhone());
                    etName.setText(data.getData().get(0).getFirstName());
                    etLast.setText(data.getData().get(0).getLastName());
                    etEmail.setText(data.getData().get(0).getEmail());
                    etAddress.setText(data.getData().get(0).getAddress());
                    etDot.setText(data.getData().get(0).getDotNumber());
                    tv_userType.setText(data.getData().get(0).getUserType());
                    tvName.setText(data.getData().get(0).getCompanyName());

                        Glide.with(ProfileActivity.this)
                                .load("http://a1professionals.net/tosstra/assets/usersImg/" + data.getData().get(0).getProfileImg())
                                .centerCrop()
                                .placeholder(R.drawable.profile_image_placeholder)
                                .into(ivProfilePic);

                } else {
                    dialog.dismiss();
                    CommonUtils.showSmallToast(ProfileActivity.this, data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(ProfileActivity.this, t.getMessage());
            }
        });
    }

    private void hitEditProfileAPI() {
        final Dialog dialog = AppUtil.showProgress(ProfileActivity.this);
        Interface service = CommonUtils.retroInit();
        if (null == finalFile) {
        } else {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), finalFile);
            image = MultipartBody.Part.createFormData("profileImg", finalFile.getName(), requestFile);
        }
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), PreferenceHandler.readString(ProfileActivity.this, PreferenceHandler.USER_ID, ""));
        RequestBody fname = RequestBody.create(MediaType.parse("text/plain"), etName.getText().toString().trim());
        RequestBody lname = RequestBody.create(MediaType.parse("text/plain"), etLast.getText().toString().trim());
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), etAddress.getText().toString().trim());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), etPhone.getText().toString().trim());
        RequestBody dotNumer = RequestBody.create(MediaType.parse("text/plain"), etDot.getText().toString().trim());
        RequestBody company_name = RequestBody.create(MediaType.parse("text/plain"), etEmail.getText().toString().trim());

        RequestBody latt,lonn;
        if(latit!=null){
             latt = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latit.latitude));
             lonn = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latit.longitude));
        }else {
             latt = RequestBody.create(MediaType.parse("text/plain"), "");
             lonn = RequestBody.create(MediaType.parse("text/plain"),"");
        }

        Call<GenricModel> call = service.edit_profile(id, image, fname,
                lname, company_name, dotNumer,
                address, phone,latt,lonn);
        call.enqueue(new Callback<GenricModel>() {
            @Override
            public void onResponse(Call<GenricModel> call, Response<GenricModel> response) {
                GenricModel data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    CommonUtils.showSmallToast(ProfileActivity.this, data.getMessage());
                    dialog.dismiss();
                    bt_save.setVisibility(View.GONE);
                    etName.setEnabled(false);
                    etPhone.setEnabled(false);
                    etLast.setEnabled(false);
                    etEmail.setEnabled(false);
                    etAddress.setEnabled(false);
                    etDot.setEnabled(false);
                    ivProfilePic.setClickable(false);
                    // CommonUtils.closeKeyBoard(getActivity());
                    hitProfileViewAPI();
                }
            }

            @Override
            public void onFailure(Call<GenricModel> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(ProfileActivity.this, t.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("requestCode", "" + requestCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                try {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    Uri tempUri = getImageUri(ProfileActivity.this, bitmap);
                    compressImage(getRealPathFromURI(tempUri));
                    finalFile = new File(filename);
                    ivProfilePic.setImageBitmap(bitmap);
                    ivProfilePic.setRotation(0);
                } catch (Exception e) {
                    Log.e("from_signup", e.toString());
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String Path = c.getString(columnIndex);
                compressImage(Path);
                finalFile = new File(filename);
                bitmap = (BitmapFactory.decodeFile(filename));
                ivProfilePic.setImageBitmap(bitmap);
                ivProfilePic.setRotation(0);
                c.close();
            }
        } else {
            Log.e("activity Result", "error");
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String pathone = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(pathone);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();

                } else if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                }
            }
        }
    }

    private String compressImage(String absolutePath) {

        String filePath = getRealPathFromURI(absolutePath);
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bitmap = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        assert scaledBitmap != null;
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int actualWidth, int actualHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > actualHeight || width > actualWidth) {
            final int heightRatio = Math.round((float) height / (float) actualHeight);
            final int widthRatio = Math.round((float) width / (float) actualWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = actualWidth * actualHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    private String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
    }

}