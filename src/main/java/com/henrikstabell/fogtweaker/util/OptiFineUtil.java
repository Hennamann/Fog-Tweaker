package com.henrikstabell.fogtweaker.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Checks for OptiFine specific classes to check if OptiFine is installed.
 * Used to throw warning about incompatibility with OptiFine.
 **/
@OnlyIn(Dist.CLIENT)
public class OptiFineUtil
{
    private static Boolean optiFineLoaded = null;

    public static boolean isOptiFineLoaded()
    {
        if(optiFineLoaded == null)
        {
            try
            {
                Class.forName("optifine.Installer");
                optiFineLoaded = true;
            }
            catch(ClassNotFoundException e)
            {
                optiFineLoaded = false;
            }
        }
        return optiFineLoaded;
    }
}
