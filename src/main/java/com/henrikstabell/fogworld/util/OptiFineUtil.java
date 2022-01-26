package com.henrikstabell.fogworld.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
