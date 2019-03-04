package com.pixelpaper.justin.wallpapergenerator;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Random;

public class Wallpaper implements Cloneable{
    // class variables
    Boolean verticalSymmetry;
    Boolean horizontalSymmetry;
    Bitmap grid;
    int gridWidth;
    int gridHeight;
    int dpi;
    Random rand;

    // a tileable object makes up the tesselation of the grid
    Tileable tile;

    Wallpaper(){
        gridWidth = 0;
        gridHeight = 0;
        dpi = 0;
    }
    Wallpaper(Bitmap bitmap){
        grid = bitmap;
    }
    Wallpaper(int screenWidth, int screenHeight, int mDpi){
        grid = Bitmap.createBitmap(screenWidth,screenHeight,Bitmap.Config.ARGB_8888);
        gridWidth = screenWidth;
        gridHeight = screenHeight;
        dpi = mDpi;
    }

    @Override
    public Wallpaper clone() {
        final Wallpaper clone;
        try {
            clone = (Wallpaper) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("superclass messed up", ex);
        }
//        clone.field = this.field.clone();
        return clone;
    }
    public void setVars(int screenWidth, int screenHeight, int mDpi){
        grid = Bitmap.createBitmap(screenWidth,screenHeight,Bitmap.Config.ARGB_8888);
        gridWidth = screenWidth;
        gridHeight = screenHeight;
        dpi = mDpi;
    }
    public void setBitmap(Bitmap mBit){
        this.grid = mBit;
    }
    public Bitmap bitmapToArray(int[][] data, int width, int height)
    {
        int[] newPixels = new int[width*height];
        int k = 0;
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                newPixels[k] = data[i][j];
                k++;
            }
        }
        return Bitmap.createBitmap(newPixels, width, height, Bitmap.Config.ARGB_8888);
    }
    public Bitmap tessellate(Tileable tile)
    {
        // There are different ways to tessellate the grid with a tile (staggered, reflect horiz/vertically, etc.)
        // For now, I will implement the simplest version
        String type = "simple";
        Bitmap ret = Bitmap.createBitmap(gridWidth, gridHeight, Bitmap.Config.ARGB_8888);
        // Simple tessellation.
        if (type == "simple")
        {
//             integers k and l are used for keeping track of the tile pixel,
//             and i and j loop through the screen's display
            for (int i = 0, k = 0; i < gridHeight; i++, k++)
            {
                if (k == tile.tileHeight-1)
                {
                    k = 0;
                }



                for (int j = 0, l = 0; j < gridWidth; j++, l++)
                {
                    if (l == tile.tileWidth-1)
                    {
                        l = 0;
                    }
                    if (gridWidth % tile.pixelLength == 0 )
                    {
                        if (j <= tile.pixelLength/2)
                        {
                            ret.setPixel(j,i, tile.data[i%tile.tileHeight][(tile.tileWidth-(j%tile.tileWidth)-1)]);

                        }
                        else
                        {
                            ret.setPixel(j,i, tile.data[i%tile.tileHeight][(j+tile.pixelLength/2)%tile.tileWidth]);
                        }

                    }
                    else
                    {
                        ret.setPixel(j,i, tile.data[i%tile.tileHeight][(j%tile.tileWidth)]);

                    }
                    ret.setPixel(j,i, tile.data[i%tile.tileHeight][(j%tile.tileWidth)]);

                }


            }
//            for (int i = 0, k = 0; i < tile.tileHeight; i++, k++)
//            {
//                if (k == tile.tileHeight-1)
//                {
//                    k = 0;
//                }
//                for (int j = 0, l = 0; j < tile.tileWidth; j++, l++)
//                {
//                    if (l == tile.tileWidth-1)
//                    {
//                        l = 0;
//                    }
//                    if (gridWidth % tile.pixelLength == 0 )
//                    {
//                        grid.setPixel((j+gridWidth/2)%gridWidth,(i+gridHeight/2)%gridHeight, tile.data[(i+tile.tileWidth/2)%tile.tileHeight][(j+tile.tileWidth/2)%tile.tileWidth]);
//
//                    }
//                    else
//                    {
//                        grid.setPixel((j+gridWidth/2)%gridWidth,(i+gridHeight/2)%gridHeight, tile.data[i%tile.tileHeight][(j%tile.tileWidth)]);
//                    }
//
//                }
//
//
//            }
        }
        return ret;
    }
    // shift entire array to the right by a random number of pixels

    int[][] horizontalShift(Tileable tile, Boolean is_incremental)
    {
        int shiftUnits = 0;
        int increment = 0;
        int incrementStep = 0;
        int incrementTimeStep = 1;
        // debugging only
//        is_incremental=false;
        if (is_incremental)
        {

                incrementStep = (int) (tile.tileWidth/2 * (rand.nextDouble() + 2));
                incrementTimeStep = (int) (rand.nextInt((tile.tileHeight / 4) + 1) + 1);
                Log.d("increment", " horizontal");
                Log.d("incrementSTep", Integer.toString(incrementStep));
                Log.d("incrementTime", Integer.toString(incrementTimeStep));

        }
        if (rand.nextFloat()<0.25)
        {
            shiftUnits = tile.tileWidth/2;//rand.nextInt(tile.tileWidth)+1;

        }
        else
        {
            shiftUnits = rand.nextInt(tile.tileWidth/2);//rand.nextInt(tile.tileWidth)+1;

        }
        Log.d("horiz shiftunits",Integer.toString(shiftUnits));

        int[][] ret = new int[tile.tileHeight][tile.tileWidth];
//        int delay = (rand.nextInt(tile.tileHeight/1))+2;
        int delay = rand.nextInt((tile.tileHeight/tile.pixelLength/2)+1)*tile.pixelLength;
//        int length = (rand.nextInt(tile.tileHeight)/4)+tile.pixelLength;
        int length = rand.nextInt((tile.tileHeight/tile.pixelLength/2)+1)*tile.pixelLength;
        Log.d("horiz shifeeetunits",Integer.toString(delay));

        Log.d("horiz ee",Integer.toString(length));

        Boolean is_shifting = false;
        int count = 0;
        for (int i = 0; i < (tile.tileHeight); i++)
        {
            // number of rows to shift horizontally
            int delaycount = 0;

            for (int j = 0; j < (tile.tileWidth); j++) {
                if (delaycount < delay) {
                    is_shifting = true;
                }
                else
                {
                    delaycount+=1;
                }

//                if (i%tile.tileWidth>(randDist)){
//                ret[i][j] = tile.data[i][(shiftUnits+j+increment)%(tile.tileWidth)];
//                }
//                else
//                {
//                    ret[i][j] = tile.data[i][j];
//                }
                if (is_incremental)
                {
                    ret[i][j] = tile.data[i][(shiftUnits+j+increment)%(tile.tileWidth)];
                }
                else if (i>tile.tileHeight/2)
//                else if (is_shifting && count < length && j + shiftUnits < tile.tileWidth-tile.pixelLength)
                {
                    ret[i][j] = tile.data[i][(shiftUnits+j)%(tile.tileWidth)];
                }
                else
                {
                    count = 0;
                    ret[i][j] = tile.data[i][(j)%(tile.tileWidth)];
                    is_shifting = false;
                }
            }
            if (i%incrementTimeStep==0 && is_incremental)
            {
                increment+=incrementStep;
            }
            if (is_shifting)
            {
                count += 1;
            }
        }
        return ret;
    }
    int[][] verticalShift(Tileable tile, Boolean is_incremental)
    {
        int shiftUnits = 0;
        int increment = 0;
        int incrementStep = 0;
        int incrementTimeStep = 1;
        // debugging only
//        is_incremental=true;
        if (is_incremental)
        {

                incrementStep = (int)(tile.tileHeight/2*(rand.nextDouble()+2));
                incrementTimeStep = (int)(rand.nextInt((tile.tileWidth/4)+1)+1);

                Log.d("increment","vertical");
                Log.d("incrementSTep",Integer.toString(incrementStep));
                Log.d("incrementTime",Integer.toString(incrementTimeStep));




        }
        if (this.rand.nextFloat()<0.25)
        {
            shiftUnits = tile.tileHeight/2;//rand.nextInt(tile.tileWidth)+1;

        }
        else
        {
            shiftUnits = this.rand.nextInt(gridHeight/2)+tile.pixelLength;//rand.nextInt(tile.tileWidth)+1;
        }
        Log.d("vertical shiftunits",Integer.toString(shiftUnits));

        int randDist = (rand.nextInt(tile.tileWidth)+tile.pixelLength)%tile.tileWidth+1;
        Log.d("randDist",Integer.toString(randDist));

        int[][] ret = new int[tile.tileHeight][tile.tileWidth];
//        int delay = rand.nextInt((tile.tileWidth/tile.pixelLength/2)+1)+tile.pixelLength;
        int delay = tile.tileWidth*(99/100);
//        int length = (rand.nextInt(tile.tileHeight)/4)+tile.pixelLength;
//        int length = rand.nextInt((tile.tileWidth/tile.pixelLength/2)+1)+tile.pixelLength;
        int length = tile.tileWidth;
        Log.d("horiz shifeeetunits",Integer.toString(delay));

        Log.d("horiz ee",Integer.toString(length));

        Boolean is_shifting = false;
        int count = 0;
        int delaycount = 0;

        for (int j = 0; j < (tile.tileWidth); j++)
        {
            // number of rows to shift horizontally

            for (int i = 0; i < (tile.tileHeight); i++) {
                if (delaycount > delay) {
                    // TODO make tghis a toggle
                    is_shifting = !is_shifting;
                    delaycount = 0;
                }
                else if (!is_shifting)
                {
                    delaycount+=1;
                }
                if (is_incremental)
                {
                    ret[i][j] = tile.data[(shiftUnits+i+increment)%tile.tileHeight][j];

                }
                else if (j>tile.tileWidth/2){
//                else if (is_shifting){
                ret[i][j] = tile.data[(shiftUnits+i)%(tile.tileHeight)][j];
                    count += 1;
                    delaycount+=1;
                }
                else
                {
                    ret[i][j] = tile.data[i][j];
                }


//                else if (is_shifting) // count < length && i + shiftUnits < tile.tileHeight-tile.pixelLength)
//                {
//                    ret[i][j] = tile.data[(shiftUnits+i)%tile.tileHeight][(j)];
//                }
//                else
//                {
//                    count = 0;
//                    ret[i][j] = tile.data[i%(tile.tileHeight)][j];
//                    is_shifting = false;
//                }
            }
            if (j%incrementTimeStep==0 && is_incremental)
            {
                increment+=incrementStep;
            }
//            if (is_shifting)
//            {
//                count += 1;
//            }
        }
        return ret;
    }
//    public Bitmap shiftAll(Bitmap bitmap, Tileable tile)
//    {
//        Bitmap ret = bitmap;
//        for (int i = 0; i < bitmap.getHeight(); i++)
//        {
//            for (int j = 0; j < bitmap.getWidth(); j++)
//            {
//                ret.setPixel(j, i, tile.data[i][(j+tile.tileWidth/2)%bitmap.getWidth()-1]);
//
//            }
//        }
//        return ret;
//    }
    public Bitmap createWallpaper(Random mRand, int preferredWidth, int preferredHeight, int preferredPixelSize, int preferredColors){
        rand = mRand;
        tile = new Tileable(rand, dpi);
        tile.generateTile(gridWidth, gridHeight, preferredWidth, preferredHeight, preferredPixelSize, preferredColors);
//        if (gridWidth%tile.pixelLength==0)
//        {
//            grid = shiftAll(grid,tile);
//        }
        if (rand.nextFloat() > 0.3)
        {
            tile.data = horizontalShift(tile, rand.nextFloat()<0.6);
            Log.d("type","horizontal");
        }
        if (rand.nextFloat() > 0.30)
        {
            tile.data = verticalShift(tile, rand.nextFloat()<0.6);
            Log.d("type","vertical");
        }
//        Bitmap ret = tessellate(tile);
        Bitmap ret = bitmapToArray(tile.data, tile.tileWidth, tile.tileHeight);
        return ret;
    }


}
