package com.example.dam.fotointent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_IMAGE_GET = 1;
    private android.widget.TextView textView;
    private android.widget.ImageView imageView;
    private android.widget.Button button;
    private android.widget.Button button2;
    private android.widget.Button button3;
    private android.widget.TableLayout linearLayout;
    private Uri uri;
    private Bitmap bitmap;
    private Random rd;
    private String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.linearLayout = (TableLayout) findViewById(R.id.linearLayout);
        this.button3 = (Button) findViewById(R.id.button3);
        this.button2 = (Button) findViewById(R.id.button2);
        this.button = (Button) findViewById(R.id.button);
        this.imageView = (ImageView) findViewById(R.id.imageView);
        this.textView = (TextView) findViewById(R.id.textView);

        rd = new Random();
        nombre = "imagen"+rd.nextInt(1000);

        Uri original = getIntent().getData();
        if (original != null){
            imageView.setImageURI(original);
        }
    }

    public void explorar(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && requestCode==REQUEST_IMAGE_GET){
            Bitmap thumbnail = data.getParcelableExtra("data");

            uri = data.getData();
            if (uri != null){
                imageView.setImageURI(uri);
            }
        }
    }

    public void girar(View view) {
        bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        imageView.setImageBitmap(MainActivity.rotarBitmap(bitmap, 90));
    }

    public void byn(View view) {
        bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        imageView.setImageBitmap(MainActivity.blancoYnegro(bitmap));
    }

    public void sepia(View view) {
        bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        imageView.setImageBitmap(MainActivity.sepia(bitmap, 150, .7, 0.3, 0.12));
    }

    public void onClickGuardar(View view) {
        bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        guardar(bitmap);
    }

    //-----------------------------GUARDAR-----------------------------------
    public void guardar(Bitmap bitmap){
        try {
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +"/" + nombre +".png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            Toast.makeText(this, R.string.imagenGuarda, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
        }
    }

    //---------------------------------EFECTOS-----------------------------------

    public static Bitmap rotarBitmap(Bitmap bmpOriginal, float angulo) {
        Matrix matriz = new Matrix();
        matriz.postRotate(angulo);
        return Bitmap.createBitmap(bmpOriginal, 0, 0,
                bmpOriginal.getWidth(), bmpOriginal.getHeight(), matriz, true);
    }

    public static Bitmap blancoYnegro(Bitmap bitmap){
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bitmap, 0, 0, paint);
        return bmpGrayscale;
    }

    public static Bitmap sepia(Bitmap src, int depth, double red, double green, double blue) {
        // tamaño de la imagen
        int width = src.getWidth();
        int height = src.getHeight();
        // crea el output de la imagen
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // constantes de color
        final double GS_RED = 0.3;
        final double GS_GREEN = 0.59;
        final double GS_BLUE = 0.11;
        // informacion de color
        int A, R, G, B;
        int pixel;

        // recorre todos los pixeles
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // toma el color de un pixel
                pixel = src.getPixel(x, y);
                // toma cada color
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // aplica el filtro de escala de grises
                B = G = R = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);

                // aplica el tono sepia
                R += (depth * red);
                if(R > 255) { R = 255; }

                G += (depth * green);
                if(G > 255) { G = 255; }

                B += (depth * blue);
                if(B > 255) { B = 255; }

                // establece el nuevo pixel
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }


}
