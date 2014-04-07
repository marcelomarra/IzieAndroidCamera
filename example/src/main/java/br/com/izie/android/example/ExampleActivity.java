package br.com.izie.android.example;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import br.com.izie.android.camera.example.R;
import br.com.izie.android.camera.library.CameraActivity;


/**
 * This is an example activity. It will launch an activity
 * defined in the library project to verify that the manifest
 * merging works correctly.
 */
public class ExampleActivity extends Activity {
    private static final int REQUEST_CODE = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_view);
        findViewById(R.id.bt_take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ExampleActivity.this, CameraActivity.class), REQUEST_CODE);
            }
        });
        Log.d("ExampleActivity", "onCreate()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = decodeSampledBitmap(convertMediaUriToPath(data.getData()), 800, 800);
                if (bitmap == null) {
                    ((ImageView) findViewById(R.id.iv_picture)).setImageResource(android.R.drawable.ic_delete);
                } else {
                    ((ImageView) findViewById(R.id.iv_picture)).setImageBitmap(bitmap);
                }
            } else {
                ((ImageView) findViewById(R.id.iv_picture)).setImageResource(android.R.drawable.ic_dialog_alert);
            }
        }
    }


    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    public Bitmap decodeSampledBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    public String convertMediaUriToPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }
}