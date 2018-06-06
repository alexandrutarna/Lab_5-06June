package it.polito.mad.lab5.NewBook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.createChooser;
import static android.provider.MediaStore.EXTRA_OUTPUT;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import it.polito.mad.lab5.MyBooks;
import it.polito.mad.lab5.NewBook.Barcode.BarcodeCaptureActivity;
import it.polito.mad.lab5.NewBook.Barcode.HttpHandler;
import it.polito.mad.lab5.R;
import it.polito.mad.lab5.SearchBook.SearchBook;
import it.polito.mad.lab5.ShowProfile;
import it.polito.mad.lab5.Utility;


public class NewBook extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    static final int GALLERY_REQ = 0;
    static final int CAMERA_REQ = 1;
    static final int CROP_REQ = 2;
    private static final int RC_BARCODE_CAPTURE = 3;

    private EditText title;
    private EditText author;
    private EditText publisher;
    private EditText editionYear;
    private Spinner genre;
    private EditText bookCondition;
    private EditText isbn;
    private ImageButton saveButton;
    private ImageButton takePhotoBook;
    //ImageButton sendIsbn;
    private Button manualBtn;
    private ImageButton read_barcode;
    private TextView scanISBNTxt;
    private Button saveButton2;


    private TextView topText;
    private Resources res;


    private String isbn_string = "";
    private String title_str = "";
    private String publisher_str = "";
    private String editionYear_str = "";
    private String author_str = "";

    Uri photo_uri;

    private String TAG = NewBook.class.getSimpleName();
    private final static String baseUrl = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

    Bitmap imageBitmap;

    private boolean manual = false;

    private int userChoosenTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);


        topText = findViewById(R.id.topText);

        res = getApplicationContext().getResources();
        String textTopString = res.getString(R.string.newBook);
        topText.setText(textTopString);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }


        title = findViewById(R.id.title);
        author = findViewById(R.id.author);
        publisher = findViewById(R.id.publisher);
        editionYear = findViewById(R.id.editionYear);
        genre = findViewById(R.id.genre_spinner);
        bookCondition = findViewById(R.id.bookCondition);
        isbn = findViewById(R.id.isbn);

        genre.setOnItemSelectedListener(this);

        Resources res = getResources();
        String [] gen = res.getStringArray(R.array.genre_array);

        List<String> genre_array = new ArrayList<String>();
        for (int i = 0; i < gen.length; i++) genre_array.add(gen[i]);


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genre_array) {
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genre.setAdapter(dataAdapter);


        saveButton = findViewById(R.id.saveButton);
        saveButton.setBackgroundColor(Color.TRANSPARENT);
        saveButton.setOnClickListener(this);

        takePhotoBook = findViewById(R.id.takePhotoBook);
        takePhotoBook.setBackgroundColor(Color.TRANSPARENT);
        takePhotoBook.setOnClickListener(this);

        manualBtn = findViewById(R.id.manualBtn);
        manualBtn.setBackgroundColor(Color.TRANSPARENT);
        manualBtn.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        manualBtn.setPaintFlags(manualBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        manualBtn.setOnClickListener(this);

        read_barcode = findViewById(R.id.read_barcode);
        read_barcode.setBackgroundColor(Color.TRANSPARENT);
        read_barcode.setOnClickListener(this);

        saveButton2 = findViewById(R.id.saveButton2);
        saveButton2.setBackgroundColor(Color.rgb(3,155,245));//0x039BE5
        saveButton2.setOnClickListener(this);

        scanISBNTxt = findViewById(R.id.scanISBNTxt);

    }
    //end of onCreate

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_barcode:
                // launch barcode activity.
                Intent intent = new Intent(this, BarcodeCaptureActivity.class);
                //intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                //intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());
                //intent.putExtra(BarcodeCaptureActivity.UseFlash, true);
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
                break;

            case R.id.manualBtn:
                manual = true;
                manualBtn.setVisibility(View.INVISIBLE);
                read_barcode.setVisibility(View.INVISIBLE);
                scanISBNTxt.setVisibility(View.INVISIBLE);
                title.setVisibility(View.VISIBLE);
                author.setVisibility(View.VISIBLE);
                publisher.setVisibility(View.VISIBLE);
                genre.setVisibility(View.VISIBLE);
                editionYear.setVisibility(View.VISIBLE);
                bookCondition.setVisibility(View.VISIBLE);
                isbn.setVisibility(View.VISIBLE);
                saveButton2.setVisibility(View.VISIBLE);
                break;

            case R.id.saveButton:
                saveBook();
                break;

            case R.id.takePhotoBook:
                select_image();
                break;

            case R.id.saveButton2:
                saveBook();
                break;
        }
    } //end of onClick

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView tv = (TextView) view;
        if(position == 0){
            // Set the hint text color gray
            tv.setTextColor(Color.GRAY);
        }
        else {
            tv.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    public void onBackPressed() {
        if (manual) {
            manual = false;
            manualBtn.setVisibility(View.VISIBLE);
            read_barcode.setVisibility(View.VISIBLE);
            scanISBNTxt.setVisibility(View.VISIBLE);
            title.setVisibility(View.INVISIBLE);
            author.setVisibility(View.INVISIBLE);
            publisher.setVisibility(View.INVISIBLE);
            editionYear.setVisibility(View.INVISIBLE);
            bookCondition.setVisibility(View.INVISIBLE);
            isbn.setVisibility(View.INVISIBLE);
            saveButton2.setVisibility(View.INVISIBLE);
            genre.setVisibility(View.INVISIBLE);
       } else {
            //Intent intent=new Intent(NewBook.this,SearchBook.class);
            //startActivity(intent);
            finish();
       }

       }


    void saveBook() {

        SharedPreferences sharedPref = this.getSharedPreferences("shared_id",Context.MODE_PRIVATE);
        String uID = sharedPref.getString("uID", null);
        String geo = sharedPref.getString("geo", null);

        String title_to_save = title.getText().toString();
        String author_to_save = author.getText().toString();
        String publisher_to_save = publisher.getText().toString();
        String editionYear_to_save = editionYear.getText().toString();
        String genre_to_save = genre.getSelectedItem().toString();
        String bookCondition_to_save = bookCondition.getText().toString();
        String isbn_to_save = isbn.getText().toString();

        Toast.makeText(this, title_to_save, Toast.LENGTH_LONG).show();

        DatabaseReference booksDBRef = FirebaseDatabase.getInstance().getReference().child("books").child(isbn_to_save);

        String newCopyRef = FirebaseDatabase.getInstance().getReference().child("copies").push().getKey();
        DatabaseReference copyDBRef = FirebaseDatabase.getInstance().getReference().child("copies").child(newCopyRef);

        booksDBRef.child("editionYear").setValue(editionYear_to_save);
        booksDBRef.child("genre").setValue(genre_to_save);
        booksDBRef.child("publisher").setValue(publisher_to_save);
        booksDBRef.child("author").setValue(author_to_save);
        booksDBRef.child("title").setValue(title_to_save);
        booksDBRef.child("isbn").setValue(isbn_to_save);

        copyDBRef.child("bookCondition").setValue(bookCondition_to_save);
        copyDBRef.child("genre").setValue(genre_to_save);
        copyDBRef.child("userID").setValue(uID);
        copyDBRef.child("isbn").setValue(isbn_to_save);
        copyDBRef.child("copyID").setValue(newCopyRef);
        copyDBRef.child("author").setValue(author_to_save);
        copyDBRef.child("author_to_search").setValue(author_to_save.toLowerCase());
        copyDBRef.child("title").setValue(title_to_save);
        copyDBRef.child("title_to_search").setValue(title_to_save.toLowerCase());
        copyDBRef.child("geo").setValue(geo);


        encodeBitmapAndSaveToFirebase(imageBitmap,newCopyRef);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uID);
        userRef.child("copies").child(newCopyRef).setValue(newCopyRef);

        myBooks();
    }


    private void select_image() {
        final CharSequence[] items = {getResources().getString(R.string.gallery),
                getResources().getString(R.string.photo),
                getResources().getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(NewBook.this);
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
                //camera

                if (item == GALLERY_REQ)
                {
                    userChoosenTask = GALLERY_REQ;
                    boolean result = Utility.checkPermission(NewBook.this);
                    if(result)
                        gallery();
                }
                //gallery
                if (item == CAMERA_REQ)
                {
                    userChoosenTask = CAMERA_REQ;
                    boolean result = Utility.checkPermission(NewBook.this);
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
        galleryIntent.setType("image/*");
        galleryIntent.putExtra("crop", "true");
        /*galleryIntent.putExtra("aspectX", 1);
        galleryIntent.putExtra("aspectY", 1);
        galleryIntent.putExtra("outputX", 240);
        galleryIntent.putExtra("outputY", 240);
        galleryIntent.putExtra("return-data", true);*/
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
            matrix.postRotate(90);
            mBitmap = Bitmap.createBitmap(mBitmap , 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        }
        photo_uri = getImageUri(getApplicationContext(), mBitmap);

        cropIntent.setDataAndType(photo_uri, "image/*");
        cropIntent.putExtra("crop", true);
        //cropIntent.putExtra("outputX", 280);
        //cropIntent.putExtra("outputY", 400);
        //cropIntent.putExtra("outputX", Utility.getScreenWidth());
        //cropIntent.putExtra("outputY", Utility.getScreenHeight());
        //cropIntent.putExtra("scale", true);
        //cropIntent.putExtra("scaleUpIfNeeded", true);
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

        System.out.print("onActivivtyResult\n\n\n");


        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK)
        {

        /*    Bundle gallery_bundle = data.getExtras();
            //Bitmap
            imageBitmap = (Bitmap) gallery_bundle.get("data");
            photo_uri = getImageUri(getApplicationContext(), imageBitmap);
            //save_photo();
            final ImageView takePhotoBook = findViewById(R.id.takePhotoBook);
            //            takePhotoBook.setImageBitmap(imageBitmap);*/

            /*Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(index);
            cursor.close();
            imageBitmap = (Bitmap) BitmapFactory.decodeFile(imagePath);
            final ImageView takePhotoBook = findViewById(R.id.takePhotoBook);
            takePhotoBook.setImageBitmap(imageBitmap);*/

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
                        Log.e("Activity", "Pick from Gallery::>>> ");

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
            final ImageView takePhotoBook = findViewById(R.id.takePhotoBook);
            takePhotoBook.setImageBitmap(imageBitmap);

        }

        else if (requestCode == CAMERA_REQ && resultCode == RESULT_OK)
        {
            performCrop();
        }

        else if (requestCode == CROP_REQ && resultCode == RESULT_OK)
        {
            Bundle cropped_image = data.getExtras();
            //Bitmap
            imageBitmap = (Bitmap) (cropped_image != null ? cropped_image.get("data") : null);
            photo_uri = getImageUri(getApplicationContext(), imageBitmap);
            //save_photo();

            final ImageView takePhotoBook = findViewById(R.id.takePhotoBook);
            takePhotoBook.setImageBitmap(imageBitmap);
        }

        else if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    //isbn.setText(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    isbn_string = barcode.displayValue;
                    new GetBook().execute();
                } else {
                    //statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            }
        }
    }


    private class GetBook extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {

            String textTopString = res.getString(R.string.connecting);
            topText.setText(textTopString);

            String url = baseUrl + isbn_string;

            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            //Log.e(TAG, "Response from url: " + jsonStr);

            return jsonStr;
        }//end of doInBackground
        protected void onPostExecute(String jsonStr){
            super.onPostExecute(jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray items = jsonObj.getJSONArray("items");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject volumeInfo = items.getJSONObject(i).getJSONObject("volumeInfo");

                        try { title_str = volumeInfo.getString("title"); } catch (Exception e) {}
                        try { publisher_str = volumeInfo.getString("publisher"); } catch (Exception e) {}
                        try { editionYear_str = volumeInfo.getString("publishedDate"); } catch (Exception e) {}


                        JSONArray authors = volumeInfo.getJSONArray("authors");
                        for (int j = 0; j < authors.length(); j++) {
                            try {
                                author_str += authors.getString(j);
                            } catch (Exception e) {
                            }
                            if (j < authors.length()-1) {
                                author_str += ", ";
                            }
                        }

                        //Show all fields in read-only
                        title.setText(title_str);
                        title.setVisibility(View.VISIBLE);
                        title.setEnabled(false);

                        author.setText(author_str);
                        author.setVisibility(View.VISIBLE);
                        author.setEnabled(false);

                        publisher.setText(publisher_str);
                        publisher.setVisibility(View.VISIBLE);
                        publisher.setEnabled(false);

                        editionYear.setText(editionYear_str);
                        editionYear.setVisibility(View.VISIBLE);
                        editionYear.setEnabled(false);

                        isbn.setText(isbn_string);
                        isbn.setVisibility(View.VISIBLE);
                        isbn.setEnabled(false);

                        genre.setVisibility(View.VISIBLE);

                        //You can edit only book condition
                        bookCondition.setVisibility(View.VISIBLE);

                        //Hide the button of scan ISBN
                        manualBtn.setVisibility(View.INVISIBLE);
                        read_barcode.setVisibility(View.INVISIBLE);
                        scanISBNTxt.setVisibility(View.INVISIBLE);

                        saveButton2.setVisibility(View.VISIBLE);

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            String textTopString = res.getString(R.string.newBook);
            topText.setText(textTopString);
        }

    }//end of it.polito.mad.lab5.GetBook


    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap, String newCopyRef) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            DatabaseReference copyDBRef = FirebaseDatabase.getInstance().getReference().child("copies").child(newCopyRef);
            copyDBRef.child("imageUrl").setValue(imageEncoded);
        } catch (NullPointerException e) {
            e.fillInStackTrace();
        }
    }

    public void myBooks(){
        Intent intent = new Intent(getApplicationContext(), MyBooks.class);
        startActivity(intent);
    }
}