package com.yanzi.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	private static final  String TAG = "FileUtil";
	private static final File parentPath = Environment.getExternalStorageDirectory();
	private static   String storagePath = "";
	private static final String DST_FOLDER_NAME = "InpaintCamera";

	/**��ʼ������·��
	 * @return
	 */
	public static String initPath(){
		//if(storagePath.equals("")){
			storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
			File f = new File(storagePath);
			if(!f.exists()){
				f.mkdir();
			}
			f = new File(storagePath+"/tmp");
			if(!f.exists()){
				f.mkdir();
			}
		//}
		return storagePath;
	}

	/**����Bitmap��sdcard
	 * @param b
	 */
	public static void saveBitmap(Bitmap b){

		String path = initPath();
		long dataTake = System.currentTimeMillis();
		String jpegName = path + "/" + dataTake +".jpg";
		Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i(TAG, "saveBitmap�ɹ�");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "saveBitmap:ʧ��");
			e.printStackTrace();
		}

	}
	
	public static void saveMask(Bitmap b){

		String path = initPath();
		long dataTake = System.currentTimeMillis();
		String jpegName = path + "/tmp/mask.jpg";
		Log.i(TAG, "saveMask:jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i(TAG, "saveMask�ɹ�");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "saveMask:ʧ��");
			e.printStackTrace();
		}

	}

	public static void saveYUVFrame(int count,byte[] b){

		String path = initPath();
		String fileName = path + "/tmp/" + count +".yuv";
		try {
			FileOutputStream fout = new FileOutputStream(fileName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			bos.write(b);
			bos.flush();
			bos.close();
			fout.close();
			Log.i(TAG, "saveYUVFrame�ɹ�");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "saveYUVFrame:ʧ��");
			e.printStackTrace();
		}

	}
	
	public static void transformPhotos(int count, int width, int height){
		for(int i=1;i<=count;i++)
		{
			String path = initPath();
			String fileName = path + "/tmp/" + i +".yuv";
			String savefileName = path + "/tmp/" + i +".jpg";
			File f = new File(fileName);
			int len = (int) f.length();
			byte[] data = new byte[len];
			try {
				FileInputStream fin = new FileInputStream(fileName);
				fin.read(data);
				fin.close();
				YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
				if (image != null) {
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					image.compressToJpeg(new Rect(0, 0, width, height), 100, stream);

					FileOutputStream fout = new FileOutputStream(savefileName);
					BufferedOutputStream bos = new BufferedOutputStream(fout);
					bos.write(stream.toByteArray());
					bos.flush();
					bos.close();
					fout.close();
					stream.close();
					f.delete();
				}
				Log.i(TAG, "transformPhotos�ɹ�");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.i(TAG, "transformPhotos:ʧ��:"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public static String getPath(Context context, Uri uri) {
		 
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;
 
            try {
                cursor = context.getContentResolver().query(uri, projection,null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
 
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
 
        return null;
    }

}
