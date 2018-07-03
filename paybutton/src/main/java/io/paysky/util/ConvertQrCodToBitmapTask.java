package io.paysky.util;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.widget.ImageView;

import com.example.paybutton.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

/**
 * Created by Paysky-202 on 5/16/2018.
 */

public class ConvertQrCodToBitmapTask extends AsyncTask<String, String, Bitmap> {

    private WeakReference<ImageView> qrImageView;
    private ProgressDialog progressDialog;

    public ConvertQrCodToBitmapTask(ImageView qrImageView) {
        this.qrImageView = new WeakReference<>(qrImageView);
        progressDialog = AppUtils.createProgressDialog(qrImageView.getContext(), R.string.please_wait);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override

    protected Bitmap doInBackground(String... params) {

        String path = params[0];

        Bitmap ja = null;

        final QRCodeWriter writer = new QRCodeWriter();
        ImageView imageView = qrImageView.get();
        if (imageView == null) return null;
        Resources resources = imageView.getContext().getResources();

        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,

                300, resources.getDisplayMetrics());

        BitMatrix bitMatrix = null;

        try {

            Hashtable hints = new Hashtable();

            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            bitMatrix = writer.encode(path,

                    BarcodeFormat.QR_CODE, Math.round(px)

                    , Math.round(px), hints);

            int width = Math.round(px);

            int height = Math.round(px);

            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            bmp.setHasAlpha(true);

            for (int x = 0; x < width; x++) {

                for (int y = 0; y < height; y++) {

                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);

                }

            }
            ja = bmp;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return ja;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        ImageView imageView = qrImageView.get();
        progressDialog.dismiss();
        if (imageView == null) return;
        imageView.setImageBitmap(result);
    }
}
