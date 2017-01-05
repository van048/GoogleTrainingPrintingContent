package cn.ben.googletrainingprintingcontent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class PrintPhotosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_photos);
    }

    private void doPhotoPrint() {
        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.droids);
        photoPrinter.printBitmap("droids.jpg - test print", bitmap);
    }

    public void printImage(@SuppressWarnings("UnusedParameters") View view) {
        doPhotoPrint();
    }

    public void printHtml(@SuppressWarnings("UnusedParameters") View view) {
        startActivity(new Intent(this, PrintHtmlActivity.class));
    }
}
