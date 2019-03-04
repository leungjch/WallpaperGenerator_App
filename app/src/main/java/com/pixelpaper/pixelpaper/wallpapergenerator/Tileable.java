package com.pixelpaper.justin.wallpapergenerator;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.Math;
// A tileable object to tessellate the wallpaper

public class Tileable {

    int tileWidth;
    int tileHeight;

    int pixelLength;

    int dpi;
    // if tile is symmetric on the vertical or horizontal axis
    Boolean is_vSymmetry;
    Boolean is_hSymmetry;

    // axis of symmetry
    int vSymmetryAxis;
    int hSymmetryAxis;

    // 2D array of coloured pixels
    int[][] data;

    // render pixels for storage of colors
    List<Integer> renderData;
    Random rand;
    Tileable(Random mRand, int mDpi){
        this.rand = mRand;
        dpi = mDpi;
    }
    public void generateTile(int gridWidth, int gridHeight, int preferredWidth, int preferredHeight, int preferredPixelSize, int preferredColors)
    {
        // Get tile width and height
//        tileWidth = gridWidth/(rand.nextInt(4)+1);

//        double pixelLength = rand.nextInt(gridWidth)+1;
        // convert screen width in pixels to inches
        int inchWidth = (int)(gridWidth/dpi);
        int inchHeight = (int)(gridHeight/dpi);

        ColorPalette colorPalette = new ColorPalette(10, rand);


        // random settings (default)
        if (preferredWidth == 0 || preferredHeight == 0 || preferredPixelSize == 0 || preferredColors == 0)
        {
            if (rand.nextBoolean())
            {
//                tileHeight = (int)(((rand.nextDouble()*(((0.7-0.2)+1))+0.2)*dpi*0.5)+(dpi*0.15));
                tileHeight = (int)(rand.nextDouble()*(gridHeight/3) + 20);

                if (rand.nextFloat() < 0.50)
                {
                    // this (most likely) makes the tile a rectangle
//                    tileWidth = (int)(((rand.nextDouble()*(((0.7-0.2)+1))+0.2)*dpi*0.5)+(dpi*0.15));
                    tileWidth = (int)(rand.nextDouble()*(gridWidth/3) + 20);

                }
                else
                {
                    // this makes the tile a square
                    tileWidth = tileHeight;

                }
                pixelLength = (int)(dpi*((rand.nextDouble()*0.5))+dpi*0.10) + 16;
                if (pixelLength >= Math.min(tileWidth,tileHeight)/2)
                {
                    pixelLength = (int)(rand.nextFloat()*Math.min(tileWidth,tileHeight)+dpi*0.1);
                }
            }
            else

            {
//                tileWidth = (int)(((rand.nextDouble()*(((0.7-0.2)+1))+0.2)*dpi*0.5)+(dpi*0.15));
                tileWidth = (int)(rand.nextDouble()*(gridWidth/3) + 20);

                if (rand.nextFloat() < 0.5)
                {
//                    tileHeight = (int)(((rand.nextDouble()*(((0.7-0.2)+1))+0.2)*dpi*0.5)+(dpi*0.15));
                    tileHeight = (int)(rand.nextDouble()*(gridHeight/3) + 20);
                }
                else
                {
                    // this makes the tile a square
                    tileHeight = tileWidth;
                }
                pixelLength = (int)(dpi*((rand.nextDouble()*1.0))+dpi*0.10)+16;
                if (pixelLength >= Math.min(tileWidth,tileHeight))
                {
                    pixelLength = (int)((0.5 * rand.nextDouble())*Math.min(tileWidth,tileHeight)+16);
                }
            }

        }
        // preferred settings
        else
        {

            tileWidth = (int)((double)(preferredWidth/100.0)*gridWidth/3);
            tileHeight = (int)((double)(preferredHeight/100.0)*gridHeight/2);
            pixelLength = (int)((double)(preferredPixelSize/100.0)*(gridWidth/6));
            colorPalette = new ColorPalette(preferredColors, rand);

        }


       // pixelLength = (int)(this.rand.nextInt(Math.min(tileHeight,tileWidth))); // One "pixel" is about 0.05in
//        pixelLength = (int)Math.cbrt(Math.round(tileWidth*tileHeight));




        colorPalette.generatePastel();
//       tileWidth =10; // debugging
//        tileHeight = 10;
//        Log.d("pixelLengthV",Double.toString(pixelLength) + " tileheight: " + Integer.toString(tileHeight) + " dpi: " + Integer.toString(dpi));
//        Log.d("pixelLengthH",Double.toString(pixelLength) + " tilewidth: " + Integer.toString(tileWidth));
//        Log.d("numPixH",Double.toString(gridWidth/pixelLength));
//        Log.d("tilesPerWidth",Double.toString(gridWidth/tileWidth));
//////        tileHeight = rand.nextInt(gridWidth);
//        Log.d("resolution", Integer.toString(tileWidth) + " X " + Integer.toString(tileHeight));
//        Log.d("Colors", Integer.toString(colorPalette.numColors));
        data = new int[tileHeight][tileWidth];
        renderData = new ArrayList<>();
        // Determine whether tile is symmetrical on vertical axis
        is_hSymmetry=(Boolean)(rand.nextFloat()<=0.3);
        Log.d("hSymmetry", Boolean.toString(is_hSymmetry));
        is_vSymmetry=(Boolean)(rand.nextFloat()<=0.3);
        Log.d("vSymmetry", Boolean.toString(is_vSymmetry));

        Log.d("Bools", "H + " + Boolean.toString(is_hSymmetry) + " V: " + Boolean.toString(is_vSymmetry));
        Log.d("Pixellength", "H + " + Integer.toString(pixelLength));
        Log.d("Width", "H + " + Integer.toString(tileWidth));
        Log.d("Hite", "H + " + Integer.toString(tileHeight));
//        is_vSymmetry = rand.nextBoolean();
//        if (is_vSymmetry)
//        {
//            vSymmetryAxis = rand.nextInt(tileWidth);
//        }
//
//        // Determine whether tile is symmetrical on vertical axis
//        is_hSymmetry = rand.nextBoolean();
//        if (is_hSymmetry)
//        {
//            hSymmetryAxis = rand.nextInt(tileHeight);
//        }

        // Loop through tile data, generating pixel colours

        // Case 1: No symmetry

        if (!is_vSymmetry && !is_hSymmetry)
        {
            for (int i = 0; i < tileHeight; i+=pixelLength)
            {

                for (int j = 0; j < tileWidth; j+=pixelLength)
                {
//                    if (j > tileWidth-pixelLength || i > tileHeight-pixelLength)
//                    {
//                        colorInt =  -16777216;
//                    }
//                    else
//                    {
//                        colorInt = colorPalette.getRandomColor();
//                    }
                    int colorInt = colorPalette.getRandomColor();

                    for (int k = 0; k < pixelLength && i+k < tileHeight; k++)
                    {

                        for (int l = 0; l < pixelLength && l+j < tileWidth; l++)
                        {

                            data[i][j]=colorInt; // add pixel at index
//                            if ((l+k) < tileWidth)
                            {data[i][j+l]=colorInt;} // add pixel right of index
//                            if ((i+k)< tileHeight)
                            {data[i+k][j]=colorInt;} // add pixel down from index
                            if (((i+k)<tileHeight) && (j+l) < tileWidth)
                            {data[i+k][j+l]=colorInt;} // add pixel diagonally (down-right) from index
                        }

                    }


                }

            }
        }
        // Case 2: Vertical symmetry only
        if (is_vSymmetry && !is_hSymmetry)
        {
            // Get first color
            int colorInt = colorPalette.getRandomColor();
            // Determine vertical axis of symmetry
            if (this.rand.nextFloat() < 0.5)
            {
                vSymmetryAxis = (tileWidth/2)+pixelLength;//rand.nextInt(tileHeight);

            }
            else
            {
                vSymmetryAxis = rand.nextInt(tileWidth)+1;
            }

            for (int i = 0; i < tileHeight; i+=pixelLength)
            {
                for (int j = 0; j < tileWidth; j+=pixelLength)
                {
                    colorInt = colorPalette.getRandomColor();
                    for (int k = 0; k < pixelLength && i+k < tileHeight; k++)
                    {

                        for (int l = 0; l < pixelLength && l+j < tileWidth; l++)
                        {
                            if (!(i >= vSymmetryAxis) && !(i <= vSymmetryAxis+pixelLength))
                            {
                                data[i][(j+vSymmetryAxis)%tileWidth]=colorInt; // add mirrored pixel
//                            if ((l+k) < tileWidth)
                                {data[i][(j+l+vSymmetryAxis)%tileWidth]=colorInt;} // add pixel right of index, mirrored vertically
//                            if ((i+k)< tileHeight)
                                {data[i+k][(j+vSymmetryAxis)%tileWidth]=colorInt;} // add pixel down from index
                                if (((i+k)<tileHeight) && (j+l) < tileWidth)
                                {data[i+k][(j+l+vSymmetryAxis)%tileWidth]=colorInt;} // add pixel diagonally (down-right) from index
                            }
                            data[i][j]=colorInt; // add pixel at index
//                            if ((l+k) < tileWidth)
                            {data[i][j+l]=colorInt;} // add pixel right of index
//                            if ((i+k)< tileHeight)
                            {data[i+k][j]=colorInt;} // add pixel down from index
                            if (((i+k)<tileHeight) && (j+l) < tileWidth)
                            {data[i+k][j+l]=colorInt;} // add pixel diagonally (down-right) from index
                        }

                    }


                }

            }
        }

        // Case 3: Horizontal symmetry only
        if (!is_vSymmetry && is_hSymmetry) {
            // Get first color
            int colorInt = colorPalette.getRandomColor();
            // Determine horizontal axis of symmetry
            if (this.rand.nextFloat() < 0.5)
            {
                hSymmetryAxis = (tileHeight/2);//rand.nextInt(tileHeight);

            }
            else
            {
                hSymmetryAxis = rand.nextInt(tileHeight)+1;
            }
            for (int i = 0; i < tileHeight; i += pixelLength) {
                for (int j = 0; j < tileWidth; j += pixelLength) {
                    colorInt = colorPalette.getRandomColor();
                    for (int k = 0; k < pixelLength && i + k < tileHeight; k++) {

                        for (int l = 0; l < pixelLength && l + j < tileWidth; l++) {
                            if (!(j >= hSymmetryAxis) && !(j <= hSymmetryAxis+pixelLength)) {
                                data[(i + hSymmetryAxis) % tileHeight][j] = colorInt; // add mirrored pixel
//                            if ((l+k) < tileWidth)
                                {
                                    data[(i + hSymmetryAxis) % tileHeight][(j + k) % tileWidth] = colorInt;
                                } // add pixel right of index, mirrored vertically
//                            if ((i+k)< tileHeight)
                                {
                                    data[(i + k + hSymmetryAxis) % tileHeight][j] = colorInt;
                                } // add pixel down from index
//                                if (((i+k)<tileHeight) && (j+l) < tileWidth)
                                {
                                    data[(i + k + hSymmetryAxis) % tileHeight][(j + l) % tileWidth] = colorInt;
                                } // add pixel diagonally (down-right) from index
                            }
                            data[i][j] = colorInt; // add pixel at index
//                            if ((l+k) < tileWidth)
                            {
                                data[i][j + l] = colorInt;
                            } // add pixel right of index
//                            if ((i+k)< tileHeight)
                            {
                                data[i + k][j] = colorInt;
                            } // add pixel down from index
                            if (((i + k) < tileHeight) && (j + l) < tileWidth) {
                                data[i + k][j + l] = colorInt;
                            } // add pixel diagonally (down-right) from index
                        }

                    }


                }

            }
        }

        // Case 4: Vertical and horizontal symmetry
        if (is_vSymmetry && is_hSymmetry)
        {
            // Get first color
            int colorInt = colorPalette.getRandomColor();
            // Determine vertical axis of symmetry
            if (this.rand.nextFloat() < 0.5)
            {
                vSymmetryAxis = (tileWidth/2);//rand.nextInt(tileHeight);

            }
            else
            {
                vSymmetryAxis = rand.nextInt(tileWidth)+1;
            }            if (rand.nextFloat() < 0.5)
            {
                hSymmetryAxis = (tileHeight/2)+pixelLength;//rand.nextInt(tileHeight);

            }
            else
            {
                hSymmetryAxis = rand.nextInt(tileHeight)+1;
            }            for (int i = 0; i < tileHeight; i += pixelLength) {
                for (int j = 0; j < tileWidth; j += pixelLength) {
                    colorInt = colorPalette.getRandomColor();
                    for (int k = 0; k < pixelLength && i + k < tileHeight; k++) {

                        for (int l = 0; l < pixelLength && l + j < tileWidth; l++) {
                            if (!(j >= hSymmetryAxis) && !(j <= hSymmetryAxis+pixelLength) && !(i >= vSymmetryAxis) && !(i <= vSymmetryAxis+pixelLength)) {
                                data[(i + hSymmetryAxis) % tileHeight][(j + vSymmetryAxis) % tileWidth] = colorInt; // add mirrored pixel
//                                if ((l+k) < tileWidth)
                                {
                                    data[(i + hSymmetryAxis) % tileHeight][(j + k + vSymmetryAxis) % tileWidth] = colorInt;
                                } // add pixel right of index, mirrored vertically
//                                if ((i+k)< tileHeight)
                                {
                                    data[(i + k + hSymmetryAxis) % tileHeight][(j + vSymmetryAxis) % tileWidth] = colorInt;
                                } // add pixel down from index
//                                if (((i+k)<tileHeight) && (j+l) < tileWidth)
                                {
                                    data[(i + k + hSymmetryAxis) % tileHeight][(j + l + vSymmetryAxis) % tileWidth] = colorInt;
                                } // add pixel diagonally (down-right) from index
                            }
                            data[i][j] = colorInt; // add pixel at index
//                            if ((l+k) < tileWidth)
                            {
                                data[i][j + l] = colorInt;
                            } // add pixel right of index
//                            if ((i+k)< tileHeight)
                            {
                                data[i + k][j] = colorInt;
                            } // add pixel down from index
//                            if (((i + k) < tileHeight) && (j + l) < tileWidth)
                            {
                                data[i + k][j + l] = colorInt;
                            } // add pixel diagonally (down-right) from index
                        }

                    }


                }

            }
        }


    }
}
