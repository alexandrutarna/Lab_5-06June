package it.polito.mad.lab5;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.IOException;

import android.net.Uri;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.lab5.NewBook.NewBook;

import static android.content.Intent.createChooser;
import static android.provider.MediaStore.EXTRA_OUTPUT;

public class EditProfile extends AppCompatActivity {

    static final int GALLERY_REQ = 0;
    static final int CAMERA_REQ = 1;
    static final int CROP_REQ = 2;

    static final int PLACE_REQ = 3;

    Bitmap imageBitmap;
    String imageUrl;
    Uri photo_uri;

    private TextView topText;
    private Resources res;

    Button geoButton;

    private int userChoosenTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        setContentView(R.layout.activity_edit_profile);

        topText = findViewById(R.id.topText);

        res = getApplicationContext().getResources();
        String textTopString = res.getString(R.string.editProfile);
        topText.setText(textTopString);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setBackgroundColor(Color.TRANSPARENT);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        final ImageButton saveButton = findViewById(R.id.saveButton);
        saveButton.setBackgroundColor(Color.TRANSPARENT);
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                saveData();
            }

        });
        final Button saveButton2 = findViewById(R.id.saveButton2);
        saveButton2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                saveData();
            }

        });

        final ImageButton photoButton = findViewById(R.id.photoSel);
        photoButton.setBackgroundColor(Color.TRANSPARENT);
        photoButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                selectImage();
            }

        });

        geoButton = findViewById(R.id.geo);
        geoButton.setBackgroundColor(Color.TRANSPARENT);
        geoButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                findPlace();
            }

        });


        String photo_saved = null;


        SharedPreferences sharedPref = this.getSharedPreferences("shared_id", Context.MODE_PRIVATE);
        String uID = sharedPref.getString("uID", null);


        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(uID);

        String mail = sharedPref.getString("mail",null);
        dbRef.child("mail").setValue(mail);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String textTopString = res.getString(R.string.connecting);
                topText.setText(textTopString);

                final String name = dataSnapshot.child("name").getValue(String.class);//getValue(String.class);
                        final String mail = dataSnapshot.child("mail").getValue(String.class);
                        final String bio = dataSnapshot.child("bio").getValue(String.class);
                        final String geo = dataSnapshot.child("geo").getValue(String.class);
                        imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);


                        //System.out.println(name + mail + bio + imageUrl);



                        EditText show_name = findViewById(R.id.name);
                        //EditText show_email = findViewById(R.id.mail);
                        TextView show_email = findViewById(R.id.mail);
                        EditText show_bio = findViewById(R.id.bio);

                        show_name.setText(name);
                        show_email.setText(mail);
                        show_bio.setText(bio);
                        geoButton.setText(geo);

                if (imageUrl != null) {
                            try {
                                imageBitmap = decodeFromFirebaseBase64(imageUrl);
                                final ImageView img = findViewById(R.id.img);
                                img.setImageBitmap(imageBitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                textTopString = res.getString(R.string.editProfile);
                topText.setText(textTopString);
                }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }


    public void saveData() {
        SharedPreferences sharedPref = this.getSharedPreferences("shared_id",Context.MODE_PRIVATE); //to save and load small data
        SharedPreferences.Editor editor = sharedPref.edit();  //to modify shared preferences

        String uID = sharedPref.getString("uID", null);


        EditText edit_name = findViewById(R.id.name);   //edit text object instances
        //EditText edit_mail = findViewById(R.id.mail);
        TextView edit_mail = findViewById(R.id.mail);
        EditText edit_bio = findViewById(R.id.bio);
        Button geoButton = findViewById(R.id.geo);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(uID);

        dbRef.child("name").setValue(edit_name.getText().toString());
        dbRef.child("mail").setValue(edit_mail.getText().toString());
        dbRef.child("bio").setValue(edit_bio.getText().toString());
        dbRef.child("geo").setValue(geoButton.getText().toString());

        editor.putString("geo",geoButton.getText().toString()); editor.apply();

        encodeBitmapAndSaveToFirebase(imageBitmap,dbRef);

        goToShowProfile();
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        EditText edit_name = findViewById(R.id.name);
        //EditText edit_mail = findViewById(R.id.mail);
        TextView edit_mail = findViewById(R.id.mail);
        EditText edit_bio = findViewById(R.id.bio);

        outState.putString("name", edit_name.getText().toString());
        //outState.putString("mail", edit_mail.getText().toString());
        outState.putString("bio", edit_bio.getText().toString());

        outState.putParcelable("BitmapImage", imageBitmap);

    }


    private void save_photo() {
        /*
        SharedPreferences sharedPref = this.getSharedPreferences("shared_id",Context.MODE_PRIVATE); //to save and load small data
        SharedPreferences.Editor editor = sharedPref.edit();  //to modify shared preferences

        editor.putString("photo", photo_uri.toString());
        editor.apply();
        */
    }


    private void selectImage() {
        final CharSequence[] items = {getResources().getString(R.string.gallery),
                getResources().getString(R.string.photo),
                getResources().getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
        //builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                /*
                //camera
                if (item == GALLERY_REQ) gallery();
                //gallery
                if (item == CAMERA_REQ) camera();
                */
                if (item == GALLERY_REQ)
                {
                    userChoosenTask = GALLERY_REQ;
                    boolean result = Utility.checkPermission(EditProfile.this);
                    if(result)
                        gallery();
                }
                //gallery
                if (item == CAMERA_REQ)
                {
                    userChoosenTask = CAMERA_REQ;
                    boolean result = Utility.checkPermission(EditProfile.this);
                    if(result)
                        camera();
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask == CAMERA_REQ)
                        camera();
                    else if(userChoosenTask == GALLERY_REQ)
                        gallery();
                } else {
                    //code for deny
                }
                break;
        }
    }


    private void gallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //Uri gallery_uri = Uri.fromFile(getMediaFile()); // Gallery uri path
        //galleryIntent.putExtra(EXTRA_OUTPUT, gallery_uri);
        galleryIntent.putExtra("crop", "true");
        //galleryIntent.putExtra("aspectX", 1);
        //galleryIntent.putExtra("aspectY", 1);
        //galleryIntent.putExtra("outputX", 240);
        //galleryIntent.putExtra("outputY", 240);
        //galleryIntent.putExtra("return-data", true);
        galleryIntent.setType("image/*");
        startActivityForResult(createChooser(galleryIntent, "Select File"),GALLERY_REQ);
    }


    private void camera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photo_uri = Uri.fromFile(getMediaFile()); // Camera uri path
        takePictureIntent.putExtra(EXTRA_OUTPUT, photo_uri);
        takePictureIntent.putExtra("return-data", true);
        startActivityForResult(takePictureIntent, CAMERA_REQ);
    }


    private void performCrop () {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        Bitmap mBitmap = BitmapFactory.decodeFile(photo_uri.getPath());
        if (mBitmap.getWidth() > mBitmap.getHeight()) {
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            mBitmap = Bitmap.createBitmap(mBitmap , 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        }
        photo_uri = getImageUri(getApplicationContext(), mBitmap);

        cropIntent.setDataAndType(photo_uri, "image/*");
        cropIntent.putExtra("crop", true);
        //cropIntent.putExtra("outputX", 240);
        //cropIntent.putExtra("outputY", 240);
        //cropIntent.putExtra("aspectX", 1);
        //cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("return-data", true);

        startActivityForResult(cropIntent, CROP_REQ);

    }


    private static File getMediaFile() {


        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG1"+ ".png");
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //System.out.print("onActivivtyResult\n\n\n");


        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK)
        {
/*            Bundle gallery_bundle = data.getExtras();
            imageBitmap = (Bitmap) gallery_bundle.get("data");
            photo_uri = getImageUri(getApplicationContext(), imageBitmap);
            save_photo();

            final ImageView img = findViewById(R.id.img);
            img.setImageBitmap(imageBitmap);*/


            Bitmap photo = null;
            Uri photoUri = data.getData();
            if (photoUri != null) {
                photo = BitmapFactory.decodeFile(photoUri.getPath());
                if(photo == null)
                {
                    try {
                        photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                    } catch (Exception e) {

                    }
                }
            }
            if (photo == null) {
                Bundle extra = data.getExtras();
                if (extra != null) {
                    photo = (Bitmap) extra.get("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                }
            }

            imageBitmap = photo;
            final ImageView img = findViewById(R.id.img);
            img.setImageBitmap(imageBitmap);

        }

        else if (requestCode == CAMERA_REQ && resultCode == RESULT_OK)
        {
            performCrop();
        }

        else if (requestCode == CROP_REQ && resultCode == RESULT_OK)
        {
            Bundle cropped_image = data.getExtras();
            imageBitmap = (Bitmap) cropped_image.get("data");
            photo_uri = getImageUri(getApplicationContext(), imageBitmap);
            //save_photo();

            final ImageView img = findViewById(R.id.img);
            img.setImageBitmap(imageBitmap);
        }

        else if (requestCode == PLACE_REQ  && resultCode == RESULT_OK)
        {
            // A place has been received; use requestCode to track the request.
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);

                    Log.e("Tag", "Place: " + place.getAddress() + "\n");

                    Log.e("place", "tostring: " + place.toString()+ "\n");

                    Log.e("place", "id: " + place.getId()+ "\n");
                    Log.e("place", "name: " + place.getName()+ "\n");
                    Log.e("place", "address: " + place.getAddress()+ "\n");
                    Log.e("place", "atributions: " + place.getAttributions()+ "\n");
                    Log.e("place", "latlng: " + place.getLatLng() + "\n");

                    String coordinates = place.getLatLng().toString();
                    System.out.println(coordinates +"\n");

                    int startPos = coordinates.indexOf("(");
                    int commaPos = coordinates.indexOf(",");

                    String lat = coordinates.substring(startPos+1,commaPos);
                    String lng = coordinates.substring(commaPos+1,coordinates.length() -1 );
                    System.out.println("lat: " + lat + "\n");
                    System.out.println("lng: " + lng + "\n");

                    String address = place.getAddress().toString();
                    System.out.println("address: " + address);

                    String[] values = address.split(",");
                    String country = values[values.length -1];
                    country = country.replaceAll("\\s+","");

                    String city = values[0];
                    System.out.println("country :" + country);
                    System.out.println("city :" + city);


                    geoButton.setText(city);
                    //((TextView) findViewById(R.id.cityTextView)).setText(city);
                    //((TextView) findViewById(R.id.countryTextView)).setText(country);

                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.e("Tag", status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }




    }


    private void goToShowProfile () {
        Intent intent = new Intent(getApplicationContext(), it.polito.mad.lab5.ShowProfile.class);
        startActivity(intent);
    }


    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap, DatabaseReference dbRef) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            dbRef.child("imageUrl").setValue(imageEncoded);

        } catch (NullPointerException e) {
            e.fillInStackTrace();
        }
    }


    public static Bitmap decodeFromFirebaseBase64(String imageUrl) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(imageUrl, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }


    public void findPlace(/*View view*/) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_REQ);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }




}
