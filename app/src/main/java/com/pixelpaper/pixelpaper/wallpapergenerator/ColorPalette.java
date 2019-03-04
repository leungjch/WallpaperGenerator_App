package com.pixelpaper.justin.wallpapergenerator;
import android.graphics.Color;
import android.util.Log;

import java.util.Random;
public class ColorPalette {

    int[] colors;
    int numColors;
    Random rand;
    // Note: num must be greater than or equal to 2
    ColorPalette(int num, Random mRand)
    {
        rand = mRand;
        numColors = rand.nextInt(num-1)+2;
        colors = new int[numColors];
    }
    // Generates numColors of pastel colors. Any pre-existing colors in the colors array are overwritten.
    void generatePastel()
    {
        for (int i = 0; i < numColors; i++)
        {
            int n = rand.nextInt(3)+1;
//            int r1 = rand.nextInt(255);
//            int g1 = rand.nextInt(255);
//            int b1 = rand.nextInt(255);
//            int r1 = 255*n;
//            int g1 = 255*n;
//            int b1= 255*n;

            int r1 = 255*n;
            int g1 = 255*n;
            int b1= 255*n;
            int r2 = rand.nextInt(255);
            int g2 = rand.nextInt(255);
            int b2 = rand.nextInt(255);

            int r = (r1+r2)/(n+1);
            int g = (g1+g2)/(n+1);
            int b = (b1+b2)/(n+1);

            colors[i] = Color.rgb(r,g,b);

        }

    }
    int getRandomColor()
    {
        int n = rand.nextInt(numColors);
        return colors[n];
    }

}
