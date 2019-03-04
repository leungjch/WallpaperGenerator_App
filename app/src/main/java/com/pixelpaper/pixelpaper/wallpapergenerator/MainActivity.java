package com.pixelpaper.justin.wallpapergenerator;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.WallpaperManager;
import android.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.graphics.Shader.TileMode;

import com.pixelpaper.justin.wallpapergenerator.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.lang.String;
import java.util.List;


// https://www.androidcentral.com/sites/androidcentral.com/files/styles/xlarge_wm_brw/public/article_images/2016/03/color-calibrate-android-n-5x.jpg?itok=FMWctRnL

public class MainActivity extends AppCompatActivity {



    Canvas canvas;

    private static final int STORAGE_REQUEST_CODE = 1;
    private int grantResults[];

    Random seedGen = new Random();
    Random rand = new Random();
    long seed = 0;
    final Wallpaper wallpaper = new Wallpaper();

    // preferred tile width and tile height. 0 defaults to random settings.
    int preferWidth = 0;
    int preferHeight = 0;
    int preferPixelSize = 0;
    int preferColors = 0;
    Boolean isRandomCheck = true;

    List<String> nameList = new ArrayList<String>();
    List<String> infoList = new ArrayList<String>();
    List<Bitmap> imageList = new ArrayList<Bitmap>();

    CustomListAdapter adapter;

    Boolean listIsDisplaying = false;
    void fixMediaDir() {
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard != null) {
            File mediaDir = new File(sdcard, "DCIM/Camera");
            if (!mediaDir.exists()) {
                mediaDir.mkdirs();
            }
        }
    }
    public void saveImage(Bitmap finalBitmap, Long seed) {

        Long time = System.nanoTime();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, Long.toString(time));
        values.put(MediaStore.Images.Media.DISPLAY_NAME, Long.toString(time));
        values.put(MediaStore.Images.Media.DESCRIPTION, "");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, time);
        values.put(MediaStore.Images.Media.DATE_TAKEN, time);

        Uri uri = null;

        try {
            uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (finalBitmap != null) {
                fixMediaDir();
                OutputStream imageOut = getContentResolver().openOutputStream(uri);

                try {
                    finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOut);
                } finally {
                    imageOut.close();
                }

                Toast.makeText(getApplicationContext(),
                        "Saved image " + Long.toString(time) + " to gallery",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Failed to save image " + Long.toString(time) + " to gallery",
                        Toast.LENGTH_LONG).show();
                getContentResolver().delete(uri, null, null);
                uri = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Failed to save image " + Long.toString(time) + " to gallery, please check storage permissions",
                    Toast.LENGTH_LONG).show();
            if (uri != null) {
                getContentResolver().delete(uri, null, null);
                uri = null;
            }
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

//    public void alertCustomizedLayout(){
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
//        // get the layout inflater
//        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
//
//        // inflate and set the layout for the dialog
//        // pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.create, null))
//
//                // action buttons
//                .setPositiveButton("Generate", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        // your sign in code here
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // remove the dialog from the screen
//                    }
//                })
//                .show();
//
//    }

    // handle checkbox events
//    public void onCheckboxClicked(View view) {
//        // Is the view now checked?
//        boolean checked = ((CheckBox) view).isChecked();
//
//        // Check which checkbox was clicked
//        switch(view.getId()) {
//            case R.id.checkbox_allow_random:
//                if (checked)
//                {
//                    // set preferences to default (random)
//                    preferHeight = 0;
//                    preferWidth = 0;
//                }
//                else
//                {
//                    break;
//                }
////            else
////                break;
////            case R.id.____:
////                if (checked)
////            else
////                break;
//        }
//    }



    // This is for the handling events in the action bar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getSupportActionBar().hide();
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Get Toolbar
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        myToolbar.setTitle("PixelPaper");
        setSupportActionBar(myToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // initialize adapter
        adapter = new CustomListAdapter(this, nameList, infoList, imageList);

        // Get progress bar
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.indeterminateBar);
        progressBar.setVisibility(View.INVISIBLE);
        // Get screen width and height
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int SCREENWIDTH = dm.widthPixels;
        final int SCREENHEIGHT = dm.heightPixels;

        // A note on DPI:
        // DPI is necessary to make sure that tile sizes are not physically varying among devices
        // The Tileable object uses DPI in the creation of tiles, but Wallpaper does not use it
        // Tile size should be from 0.05in to 0.5in
        final int DPI = getResources().getDisplayMetrics().densityDpi;

        wallpaper.setVars(SCREENWIDTH,SCREENHEIGHT,DPI);

        // set random seed
        seedGen = new Random();
        seed = seedGen.nextLong();
        rand = new Random(seed);


        // get imageview
        final ImageView mImg = (ImageView) findViewById(R.id.myView);
        mImg.setVisibility(View.VISIBLE);

        // generate first wallpaper;
        seed = seedGen.nextLong();
        rand = new Random(seed);
        GenerateWallpaperTask generateTask = new GenerateWallpaperTask(wallpaper);//, rand, seedGen, mImg);
        generateTask.execute();


        // Generate button
        final Button generateButton = (Button) findViewById(R.id.submit_button);
        // img2 is "background" while img1 is actively being redrawn.
//        final ImageView mImg2 = (ImageView) findViewById(R.id.myView2);
//        mImg2.setImageBitmap(wallpaper.grid);
//        mImg2.setVisibility(View.VISIBLE);

        generateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                seed = seedGen.nextLong();
                rand = new Random(seed);
                GenerateWallpaperTask generateTask = new GenerateWallpaperTask(wallpaper);//, rand, seedGen, mImg);
                generateTask.execute();

            }
        });

        // Save button
        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // check if user has granted write permission
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Show explanation
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showMessageOKCancel("Access to external storage is required for wallpaper storage and wallpaper sharing.",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                        if (which == DialogInterface.BUTTON_POSITIVE)
                                        {

                                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);

                                        }

                                    }
                                });
                        return;

                    }
                    else
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);

                    }

                }
                else {

                    saveImage(wallpaper.grid, seed);

                }
            }


        });

        // Set wallpaper button
        Button setWallpaperButton = (Button) findViewById(R.id.set_wallpaper_button);
        setWallpaperButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Change wallpaper");
                builder.setMessage("Are you sure you want to change the current wallpaper?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                        try {
                            myWallpaperManager.setBitmap(wallpaper.grid);
                            Toast.makeText(getApplicationContext(), "Set as wallpaper!", Toast.LENGTH_SHORT).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Set seed
//        final EditText setSeedField = (EditText) findViewById(R.id.set_seed);
//        setSeedField.setOnKeyListener(new OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                // If the event is a key-down event on the "enter" button
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    // Set new seed
//                    try {
//                        setSeedField.setText(setSeedField.getText().toString().replaceAll(" ", ""));
//                        Pattern p = Pattern.compile(".*[a-zA-Z]+.*");
//                        Matcher m = p.matcher(setSeedField.getText());
//                        if (m.find())
//                        {
//                            // encode alphanumeric
//                            seed = Long.parseLong(setSeedField.getText().toString(), 36);
//                        }
//                        else
//                        {
//                            // encode base 10
//                            seed = Long.parseLong(setSeedField.getText().toString());
//                        }
//                        // Generate wallpaper of given seed
//
//                        rand = new Random(seed);
//                        // generate first wallpaper;
//                        GenerateWallpaperTask generateTask = new GenerateWallpaperTask(wallpaper);//, rand, seedGen, mImg);
//                        generateTask.execute();
//
//                        Toast.makeText(getApplicationContext(), "Set seed to " + Long.toString(seed, 36), Toast.LENGTH_SHORT).show();
//                        mImg.setImageBitmap(wallpaper.grid);
//
//                        return true;
//                    }
//                    catch(Exception NumberFormatException)
//                    {
//                        Toast.makeText(getApplicationContext(), "Input is too long or contains invalid characters, please try again.", Toast.LENGTH_LONG).show();
//
//                    }
//                }
//                return false;
//            }});

        // Request permission on startup
        // check if user has granted write permission
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Show explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showMessageOKCancel("Access to external storage is required for wallpaper storage and wallpaper sharing.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (which == DialogInterface.BUTTON_POSITIVE)
                                {

                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);

                                }

                            }
                        });
                return;

            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);

            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(MainActivity.this, "Permission successfully granted.", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    private class GenerateWallpaperTask extends AsyncTask<Void, Void, Wallpaper>{
        Wallpaper wallpaper;
//        Random rand;
//        Random seedGen;
        ImageView mImg;

        ProgressBar mProgressBar;
        GenerateWallpaperTask(Wallpaper tWallpaper)//, Random rand, Random seedGen, ImageView mImg)
        {
            this.wallpaper = tWallpaper;
//            this.rand = rand;
//            this.mImg = mImg;
//            this.seedGen = seedGen;
            this.mImg = (ImageView) findViewById(R.id.myView);

        }
        @Override
        protected void onPreExecute()
        {
//            mImg2.setImageBitmap(wallpaper.grid);
//            Wallpaper copy = wallpaper;
//            try
//            {
//                copy = (Wallpaper)((Wallpaper)wallpaper).clone();
//            }
//            catch(Exception CloneNotSupportedException)
//            {
//                Log.i("Caught clone error","caught clone error");
//            }
//            adapter.imageList.add(copy.grid);
            this.mProgressBar = (ProgressBar) findViewById(R.id.indeterminateBar);
            this.mProgressBar.setVisibility(View.VISIBLE);
            this.mImg.setVisibility(View.INVISIBLE);






        }
        @Override
        protected Wallpaper doInBackground(Void... voids)
        {

            Wallpaper tempPaper = new Wallpaper();
            if (isRandomCheck)
            {
                tempPaper.grid = this.wallpaper.createWallpaper(rand, 0, 0, 0, 0);
                tempPaper.tile = this.wallpaper.tile;

            }
            else
            {
                tempPaper.grid = this.wallpaper.createWallpaper(rand, preferWidth, preferHeight, preferPixelSize, preferColors);
                tempPaper.tile = this.wallpaper.tile;
            }
            return tempPaper;
        }

        @Override
        protected void onProgressUpdate(Void... voids) {


        }
        @Override
        protected void onPostExecute(final Wallpaper myWallpaper)
        {
            if (imageList.size() % 1 == 0)
            {
                Log.i("hi","hi");
            }


            this.mImg.post(new Runnable() {
                @Override
                public void run() {
                    final BitmapDrawable drawable = new BitmapDrawable(getResources(), myWallpaper.grid);
                    drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);

                    mImg.setImageResource(android.R.color.transparent);
                    mImg.setBackground(drawable);

                    Bitmap tiledBitmap = Bitmap.createBitmap(mImg.getWidth(), mImg.getHeight(), Bitmap.Config.ARGB_8888);
                    canvas = new Canvas (tiledBitmap);
                    drawable.setBounds(0,0, mImg.getWidth(), mImg.getHeight());
                    drawable.draw( canvas );


                    wallpaper.setBitmap(tiledBitmap);
                    myWallpaper.setBitmap(tiledBitmap);

                    // if not duplicate wallpaper (this can happen when pulling a wallpaper from history)
                    if (!adapter.nameList.contains(Long.toString(seed,36))) {

                        // update history
                        adapter.nameList.add(Long.toString(seed,36));
                        adapter.infoList.add(Long.toString(seed,36));
//                adapter.imageList.add(new Wallpaper(myWallpaper.grid).grid); // add full wallpaper size
//                adapter.imageList.add(new Wallpaper(myWallpaper.grid).grid);
                        adapter.imageList.add(myWallpaper.grid);


                        // keep only the last 10 wallpapers
                        if (adapter.imageList.size() >= 10) {
                            adapter.nameList.remove(0);
                            adapter.infoList.remove(0);
                            adapter.imageList.remove(0);
                        }
                    }


//            this.mImg.setImageBitmap(myWallpaper.grid);
                    //imageList.add(mImg.getDrawable());
                    Log.i("imageList",Integer.toString(imageList.size()));

//            this.mImg.postInvalidate();

                    mImg.setVisibility(View.VISIBLE);

                    mProgressBar.setVisibility(View.INVISIBLE);


//            if (((BitmapDrawable)(mImg.getDrawable())).getBitmap().sameAs(adapter.imageList.get(0)))
//            {
//                Log.i("Same","same");
//            }

                    adapter.notifyDataSetChanged();
                }
            });

//                Log.i(mImg.,"hi");

//            Bitmap tiledBitmap = Bitmap.createBitmap(mImg.getWidth(), mImg.getHeight(), Bitmap.Config.ARGB_8888);



        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final ListView listView = (ListView) findViewById(R.id.history_list);


        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportActionBar().setTitle("PixelPaper");
                listView.setVisibility(View.INVISIBLE);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                listIsDisplaying = false;
                return true;

            case R.id.action_create:

                final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
                final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

                final View Viewlayout = inflater.inflate(R.layout.activity_dialog,
                        (ViewGroup) findViewById(R.id.layout_dialog));

                final TextView item1 = (TextView)Viewlayout.findViewById(R.id.txtItem1); // txtItem1
                final TextView item2 = (TextView)Viewlayout.findViewById(R.id.txtItem2); // txtItem2
                final TextView item3 = (TextView)Viewlayout.findViewById(R.id.txtItem3); // txtItem3
                final TextView item4 = (TextView)Viewlayout.findViewById(R.id.txtItem4); // txtItem4
                item1.setText("Tile width: " + Integer.toString(preferWidth) + "%");
                item2.setText("Tile height: " + Integer.toString(preferHeight) + "%");
                item3.setText("Pixel size: " + Integer.toString(preferPixelSize) + "%");
                item4.setText("Max colors: " + Integer.toString(preferColors));
                popDialog.setTitle("Set Preferences");
                popDialog.setView(Viewlayout);

                //  seekBar1
                final SeekBar seek1 = (SeekBar) Viewlayout.findViewById(R.id.seekBar1);
                seek1.setMax(99);
                seek1.setProgress(preferWidth);

                if (isRandomCheck) {

                    seek1.setEnabled(false);
                }
                else
                {
                    seek1.setEnabled(true);
                }
                seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                        //Do something here with new value
                        item1.setText("Tile width: " + (int)(progress+1) + "%");
                    }

                    public void onStartTrackingTouch(SeekBar arg0) {
                        // TODO Auto-generated method stub

                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                        preferWidth = seekBar.getProgress() + 1;

                    }
                });

                //  seekBar2
                final SeekBar seek2 = (SeekBar) Viewlayout.findViewById(R.id.seekBar2);
                seek2.setMax(99);

                seek2.setProgress(preferHeight);

                if (isRandomCheck) {

                    seek2.setEnabled(false);
                }
                else
                {
                    seek2.setEnabled(true);

                }

                seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                        //Do something here with new value
                        item2.setText("Tile height: " + (int)(progress +1) + "%");
                    }

                    public void onStartTrackingTouch(SeekBar arg0) {
                        // TODO Auto-generated method stub

                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                        preferHeight = seekBar.getProgress()+1;

                    }
                });

                // preferred pixel length
                final SeekBar pixelSeek = (SeekBar) Viewlayout.findViewById(R.id.pixelSeek);
                pixelSeek.setMax(Math.max(preferHeight,preferWidth));

                pixelSeek.setProgress(preferPixelSize);

                if (isRandomCheck) {

                    pixelSeek.setEnabled(false);
                }
                else
                {
                    pixelSeek.setEnabled(true);
                }
                pixelSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                        //Do something here with new value
                        item3.setText("Pixel size: " + (int)(progress +1) + "%");

                    }

                    public void onStartTrackingTouch(SeekBar arg0) {
                        // TODO Auto-generated method stub
                        pixelSeek.setMax(Math.max(preferHeight,preferWidth));

                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                        preferPixelSize = seekBar.getProgress();


                    }
                });
                // preferred number of colors
                final SeekBar colorSeek = (SeekBar) Viewlayout.findViewById(R.id.colorSeek);
                colorSeek.setMax(8);

                colorSeek.setProgress(preferColors-2);

                if (isRandomCheck) {

                    colorSeek.setEnabled(false);
                }
                else
                {
                    colorSeek.setEnabled(true);
                }
                colorSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                        //Do something here with new value
                        item4.setText("Max colors: " + (int)(progress +2));

                    }

                    public void onStartTrackingTouch(SeekBar arg0) {
                        // TODO Auto-generated method stub
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                        preferColors = seekBar.getProgress()+2;
                    }
                });

                final CheckBox checkBox = (CheckBox) Viewlayout.findViewById(R.id.checkbox_allow_random);
                checkBox.setChecked(isRandomCheck);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
//                if (checkBox.isChecked()) {
//                    checkBox.setChecked(false);
//                }
                        if (!isRandomCheck) {

                            seek1.setEnabled(false);
                            seek2.setEnabled(false);
                            pixelSeek.setEnabled(false);
                            seek1.setProgress(preferWidth);
                            seek2.setProgress(preferHeight);
                            pixelSeek.setProgress(preferPixelSize);
                            colorSeek.setProgress(preferColors);


//                            preferWidth = 0;
//                            preferHeight = 0;
//                            preferPixelSize = 0;
                            isRandomCheck = !isRandomCheck;

                        }
                        else
                        {

                            preferHeight=seek2.getProgress();
                            preferWidth=seek1.getProgress();
                            preferPixelSize=pixelSeek.getProgress();
                            preferColors=colorSeek.getProgress();

                            seek1.setEnabled(true);
                            seek2.setEnabled(true);
                            pixelSeek.setEnabled(true);
                            colorSeek.setEnabled(true);
                            isRandomCheck = !isRandomCheck;



                        }
                    }
                });
                // removed edit text function
//                final EditText setSeedField = (EditText) Viewlayout.findViewById(R.id.set_my_seed);
//                setSeedField.setOnKeyListener(new OnKeyListener() {
//                    public boolean onKey(View v, int keyCode, KeyEvent event) {
//                        // If the event is a key-down event on the "enter" button
////                        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
////                                (keyCode == KeyEvent.KEYCODE_ENTER)) {
////                            // Set new seed
//                            try {
//                                setSeedField.setText(setSeedField.getText().toString().replaceAll(" ", ""));
//                                Pattern p = Pattern.compile(".*[a-zA-Z]+.*");
//                                Matcher m = p.matcher(setSeedField.getText());
//                                if (m.find())
//                                {
//                                    // encode alphanumeric
//                                    seed = Long.parseLong(setSeedField.getText().toString(), 36);
//                                }
//                                else
//                                {
//                                    // encode base 10
//                                    seed = Long.parseLong(setSeedField.getText().toString());
//                                }
//                                // Generate wallpaper of given seed
//
//                                rand = new Random(seed);
//                                // reset preferences to default
//                                preferHeight = 0;
//                                preferPixelSize = 0;
//                                preferWidth = 0;
//                                isRandomCheck = true;
//                                // generate first wallpaper;
////                                GenerateWallpaperTask generateTask = new GenerateWallpaperTask(wallpaper);//, rand, seedGen, mImg);
////                                generateTask.execute();
//                                Toast.makeText(getApplicationContext(), "Set seed to " + Long.toString(seed, 36), Toast.LENGTH_SHORT).show();
//
//                                return true;
//                            }
//                            catch(Exception NumberFormatException)
//                            {
//                                Toast.makeText(getApplicationContext(), "Input is too long or contains invalid characters, please try again.", Toast.LENGTH_LONG).show();
//                            }
////                        }
//                        return false;
//                    }});

                // Button OK
                popDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(getApplicationContext(), "Successfully set preferences", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                        });


                popDialog.create();
                popDialog.show();


                return true;

            case R.id.action_history:
                getSupportActionBar().setTitle("History");
                listView.setScrollbarFadingEnabled(false);
                adapter = new CustomListAdapter(this, nameList, infoList, imageList);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                listView.setVisibility(View.VISIBLE);
                listView.setBackgroundColor(Color.WHITE);
                listView.setAdapter(adapter);
                listIsDisplaying = true;
                // Get List, check if user is currently viewing the list
                // List is currently not displaying, so load the list and set listIsDisplaying to true
//                if (!listIsDisplaying) {
//
//                    listView.setVisibility(View.VISIBLE);
//                    listView.setBackgroundColor(Color.WHITE);
//                    listView.setAdapter(adapter);
//                    listIsDisplaying = true;
//                }
//                // User is currently viewing the list, so turn the view off when the button is pressed
//                else
//                {
//                    listView.setVisibility(View.INVISIBLE);
//                    listIsDisplaying = false;
//                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        seed = Long.parseLong(adapter.nameList.get(position), 36);
                        rand = new Random(seed);
                        GenerateWallpaperTask generateTask = new GenerateWallpaperTask(wallpaper);//, rand, seedGen, mImg);
                        generateTask.execute();

                        // immediately return to view
                        getSupportActionBar().setTitle("PixelPaper");
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                        listView.setVisibility(View.INVISIBLE);
                        listIsDisplaying = false;
                    }
                });

                return true;
            case R.id.action_info:

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Info")
                        .setMessage("If you enjoy the wallpapers, please share this app with friends! \n\nWritten by Justin Leung");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

//                builder.setNeutralButton("Copy Seed", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                        ClipData clip = ClipData.newPlainText("copiedSeed", Long.toString(seed, 36));
//                        clipboard.setPrimaryClip(clip);
//                        dialog.dismiss();
//
//                        Toast.makeText(getApplicationContext(), " Copied seed: " + Long.toString(seed, 36), Toast.LENGTH_SHORT).show();
//                    }
//                });
                builder.setNegativeButton("Share wallpaper", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // check if user has granted write permission
                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            // Permission is not granted
                            // Show explanation
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("Access to external storage is required for wallpaper sharing.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                                if (which == DialogInterface.BUTTON_POSITIVE)
                                                {

                                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);


                                                }

                                            }
                                        });
                                return;

                            }
                            else
                            {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);

                            }

                        }
                        // permission has been granted, proceed with sharing wallpaper
                        else {

                            Bitmap bitmap = wallpaper.grid;
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null);
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/png");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Intent.EXTRA_TEXT, "Check out this wallpaper app. Each wallpaper is unique and aesthetic! \n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                            intent.putExtra(Intent.EXTRA_TITLE, "Wallpaper generator"); //" + "https://play.google.com/store/apps/details?id=" +getPackageName());
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Wallpaper generator"); //" + "https://play.google.com/store/apps/details?id=" +getPackageName());
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                            startActivity(Intent.createChooser(intent, "Share via"));
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}