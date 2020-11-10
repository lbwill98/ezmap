package com.example.ezmap.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;

import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.example.ezmap.R;
import com.example.ezmap.view.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

public class Itineraire {
    static Context context;
    static String fileName, downloadURL;

    public static View.OnClickListener rechercheItineraire(Context context, Resources resources, ImageView ivQRCode) {
        Itineraire.context = context;
        return new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                createFolder();
                Uri uri = createPdf(resources);
                if (uri != null) {
                    downloadURL = uploadFile(uri);
                }

            }
        };
    }


    private static void createFolder() {
        File file = new File(Environment.getExternalStorageDirectory(), "EzMap".trim());
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static Uri createPdf(Resources resources) {

        PdfDocument myPdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(6000, 10500, 1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();

        //add image
        Bitmap btmPlan = BitmapFactory.decodeResource(resources, R.drawable.premier_moyen);
        //Bitmap btmSmallPlan = Bitmap.createScaledBitmap(btmPlan,2000,3500,false );
        canvas.drawBitmap(btmPlan, 0, 0, null);

        myPdfDocument.finishPage(myPage);
        fileName = System.currentTimeMillis() + "";
        File file = new File(Environment.getExternalStorageDirectory(), "EzMap/" + fileName + ".pdf");
        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        btmPlan.recycle();
        myPdfDocument.close();
        return (Uri.fromFile(file));
    }

    private static String uploadFile(Uri uri) {
        FirebaseStorage storage;
        storage = FirebaseStorage.getInstance();

        //final String fileName = System.currentTimeMillis() + "";
        final StorageReference storageReference = storage.getReference();//return root path
        final String[] downloadURL = new String[1];

        final StorageReference ref = storageReference.child("Uploads").child(fileName);
        Toast.makeText(context, uri.toString(), Toast.LENGTH_SHORT).show();
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                Toast.makeText(context, uri.getResult().toString(), Toast.LENGTH_SHORT).show();
                while (!uri.isComplete());
                downloadURL[0] = uri.getResult().toString();
            }
        });
        return (downloadURL[0]);
    }

    /*private static void generateQRCode(String string){

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(string, BarcodeFormat.QR_CODE,ivQRCode.getWidth(),ivQRCode.getHeight());
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ivQRCode.setImageBitmap(bitmap);
            bitmap.recycle();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }*/
}




/*Bitmap btmPlan;

                btmPlan = BitmapFactory.decodeResource(resources,R.drawable.rdc);

                // create a new document
                PdfDocument document = new PdfDocument();
                // create a page description
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(7000, 4000, 1).create();

                // start a page
                PdfDocument.Page page = document.startPage(pageInfo);

                // draw something on the page
               Canvas canvas = page.getCanvas();
               canvas.drawBitmap(btmPlan,0,0,new Paint());

                // finish the page
                document.finishPage(page);

                // add more pages

                // write the document content
                String targetPdf = "/sdcard/test.pdf";
                File filePath = new File(targetPdf);
                try {
                    document.writeTo(new FileOutputStream(filePath));
                    //Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    //Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
                }

                // close the document
                document.close();*/