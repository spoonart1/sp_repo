package javasign.com.dompetsehat.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;
import java.net.URLConnection;
import javasign.com.dompetsehat.BuildConfig;
import javax.inject.Inject;
import javax.inject.Singleton;

import javasign.com.dompetsehat.injection.ApplicationContext;

/**
 * Created by Xenix on 7/10/2015.
 */
@Singleton public class LoadAndSaveImage {
    private Context _context;
    public final String dirApps = "/data/data/"+ BuildConfig.APPLICATION_ID+"/app_imageDir/";
    public final static String dirAppsPing = "/data/data/"+ BuildConfig.APPLICATION_ID+"/app_ping/";
    public final static String pingName = "ping.vfp";

    @Inject public LoadAndSaveImage(@ApplicationContext Context context) {
        this._context = context;
    }

    public void saveToInternalSorage(Bitmap bitmapImage, String imageName) {
        ContextWrapper cw = new ContextWrapper(_context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, imageName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);       // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap loadImageFromStorage(String path, String imageName) {
        try {
            File f = new File(path, imageName);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap loadBitmap(String url)
    {
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try
        {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if (bis != null)
            {
                try
                {
                    bis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }

    public boolean cekFile(String path, String fileName) {
        File file = new File(path, fileName);
        return file.exists();
    }

    public void executePingFile(){
        if(!cekFile(dirAppsPing, pingName)) {
            ContextWrapper cw = new ContextWrapper(_context);
            File directory = cw.getDir("ping", Context.MODE_PRIVATE);
            File mypath = new File(directory, pingName);
            copyAssets(mypath);
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    private void copyAssets(File pathfiletocopy) {
        AssetManager assetManager = _context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(pingName);
            File outFile = new File(pathfiletocopy.getAbsolutePath());
            out = new FileOutputStream(outFile);
            copyFile(in, out);
        } catch(IOException e) {
            return;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
        }
    }
}
