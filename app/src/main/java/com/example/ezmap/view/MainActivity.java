package com.example.ezmap.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ezmap.R;
import com.example.ezmap.controller.Itineraire;
import com.example.ezmap.controller.MyAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.storage.FirebaseStorage;
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
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Déclaration des attriuts graphiques
    EditText etRecherche;
    Switch swtcMobiliteReduite;
    Button btnRecherche;
    ImageView ivQRCode;
    com.google.android.material.tabs.TabLayout tableLayout;
    ViewPager2 vpPlan;
    int[] imagesPlan = {R.drawable.rdj,R.drawable.rdc,R.drawable.premier, R.drawable.deuxieme,R.drawable.troisieme};
    MyAdapter myAdapter;

    static String fileName, downloadURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //on enlève la barre de l'application
        Objects.requireNonNull(getSupportActionBar()).hide();

        //on vérifie les permissions
        checkPermissions();

        //Assiciations des attributs avec éléments graphques xml
        etRecherche = (EditText)findViewById(R.id.etRecherche);
        swtcMobiliteReduite = (Switch) findViewById(R.id.swtcMobiliteReduite);
        btnRecherche = (Button) findViewById(R.id.btnRecherche);
        ivQRCode =(ImageView) findViewById(R.id.ivQRCode);
        tableLayout = (com.google.android.material.tabs.TabLayout)findViewById(R.id.tableLayout);
        vpPlan = (ViewPager2)findViewById(R.id.vpPlan);

        //On met les images des plans des étages sur le viewPager avec la class myAdapter
        myAdapter = new MyAdapter(imagesPlan);
        vpPlan.setAdapter(myAdapter);

        //On se met par default sur l'étage 0
        vpPlan.setCurrentItem(1);

        //On affiche les numéros des étages avec le tabLayout
        new TabLayoutMediator(tableLayout, vpPlan,  (tab, position) -> tab.setText("  "+(position - 1)+"  ")).attach();

        //On attend une action sur le boutton
        btnRecherche.setOnClickListener(rechercheItineraire(getApplicationContext(),getResources(),ivQRCode));
    }

    /**
     * Vérification des permissions, l'application ferme si les permissions sont refusées
     */
    protected void checkPermissions(){
        boolean permissionAcceptee = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
                //&& ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                //&& ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)== PackageManager.PERMISSION_GRANTED;
        if(!permissionAcceptee) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},10);
            //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},10);
            //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET},10);

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean demandeRefusee = requestCode!=10 || (grantResults.length == 0) || (grantResults[0]==PackageManager.PERMISSION_DENIED);
        if(demandeRefusee){
            System.exit(0);
        }
    }




    public View.OnClickListener rechercheItineraire(Context context, Resources resources, ImageView ivQRCode) {
        return new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                createFolder();
                Uri uri = createPdf(resources);
                if (uri != null) {
                    downloadURL = uploadFile(uri);
                    //generateQRCode(downloadURL);
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

    private String uploadFile(Uri uri) {
        FirebaseStorage storage;
        storage = FirebaseStorage.getInstance();

        //final String fileName = System.currentTimeMillis() + "";
        final StorageReference storageReference = storage.getReference();//return root path
        final String downloadURL;

        final StorageReference ref = storageReference.child("Uploads").child(fileName);
        //Toast.makeText(context, uri.toString(), Toast.LENGTH_SHORT).show();
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                //Toast.makeText(context, uri.getResult().toString(), Toast.LENGTH_SHORT).show();
                /*uri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                    }
                });*/
                while (!uri.isComplete());
                generateQRCode(uri.getResult().toString());

            }
        });
        return ("");
    }

    private void generateQRCode(String string){

        etRecherche.setText(string);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(string, BarcodeFormat.QR_CODE,ivQRCode.getWidth(),ivQRCode.getHeight());

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ivQRCode.setImageBitmap(bitmap);
            /*bitmap.recycle();*/
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }




}


/*
GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (ivPlan.isReady()) {
                    //PointF sCoord = ivPlan.viewToSourceCoord(e.getX(), e.getY());
                    // ...

                }
                etRecherche.setText(""+ivPlan.getScale());
                return true;
            }
        });
        ivPlan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);

            }
        });
 */

 /*ivPlan = (SubsamplingScaleImageView)findViewById(R.id.ivPlan);

        ivPlan.setImage(ImageSource.resource(R.drawable.rdc_huit));

        ivPlan.setScaleAndCenter(0.5F, new PointF(4000,6000));*/


 /*
        int width= Resources.getSystem().getDisplayMetrics().widthPixels;
        int height= Resources.getSystem().getDisplayMetrics().heightPixels;
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(0,50));
        tableLayout.setTranslationX(-350);
        tableLayout.setTranslationY(300);*/

  /* public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);

    }*/