/** 
 * Copyright (c) 2013 Cangol
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cangol.mobile.utils;

/**
 * @Description: Bitmap工具类
 * @version $Revision: 1.0 $
 * @author xuewu.wei
 * @date: 2010-12-6
 * @time: 04:47:39
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;

public class BitmapUtils {
	/**
	 * 添加水印
	 * @param src
	 * @param watermark
	 * @param left
	 * @param top
	 * @return
	 */
	public static Bitmap addBitmap(Bitmap src,Bitmap watermark,float left,float top){
		int w = src.getWidth();
		int h = src.getHeight();
		Bitmap dst = Bitmap.createBitmap( w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(dst);
		canvas.drawBitmap(BitmapUtils.setAlpha(src, 50),0,0,null);
		canvas.drawBitmap(BitmapUtils.scale(watermark,src.getWidth(),(int)(src.getHeight()/3.0f)), left, top, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return dst;	
	}
	public static Bitmap addProgress(Bitmap src,Bitmap pro,int progress){
		int w = pro.getWidth();
		int h = pro.getHeight();
		Bitmap dst = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(dst);
		canvas.drawBitmap(src,0,0,null);
		canvas.drawBitmap(pro, 0, pro.getHeight()*(100-progress)/100.0f, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return dst;	
	}
	public static Bitmap setAlpha(Bitmap sourceImg, int number) {
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg
				.getWidth(), sourceImg.getHeight());
		number = number * 255 / 100;
		for (int i = 0; i < argb.length; i++) {
			argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
		}
		sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg
				.getHeight(), Config.ARGB_8888);

		return sourceImg;
	}
	
	public static Bitmap addWatermark(Bitmap src,float left,float top,String content,int alpha){
		int w = src.getWidth();
		int h = src.getHeight();
		Bitmap dst = Bitmap.createBitmap( w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(dst);
		Paint p = new Paint();
		String fontName = "sans";
		Typeface font = Typeface.create(fontName,Typeface.BOLD);
		p.setColor(Color.RED);
		p.setTypeface(font);
		p.setTextSize(22);
		p.setAlpha(alpha* 255 / 100);
		canvas.drawText(content,left,top,p);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return dst;
	}
	public static Bitmap addWatermark(Bitmap src,float left,float top,Bitmap img,int alpha){
		int w = src.getWidth();
		int h = src.getHeight();
		Bitmap dst = Bitmap.createBitmap( w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(dst);
		Paint p = new Paint();
		canvas.drawBitmap(src,0,0,p);
		p.setAlpha(alpha* 255 / 100);
		canvas.drawBitmap(img,left,top,p);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return dst;
	}
	public static Bitmap scale(Bitmap bitmap,float scaleWidth, float scaleHeight){
		int width = bitmap.getWidth();  
        int height = bitmap.getHeight();  
        Matrix matrix = new Matrix();  
        matrix.postScale(scaleWidth, scaleHeight); 
      	return Bitmap.createBitmap(bitmap, 0, 0, width,height, matrix, true);  
	}
	public static Bitmap scale(Bitmap bitmap,int newWidth, int newHeight){
		int width = bitmap.getWidth();  
        int height = bitmap.getHeight();  
        float scaleWidth = ((float) newWidth) / width;  
        float scaleHeight = ((float) newHeight) / height;  
        Matrix matrix = new Matrix();  
        matrix.postScale(scaleWidth, scaleHeight); 
      	return Bitmap.createBitmap(bitmap, 0, 0, width,height, matrix, true);  
	}

	public static Bitmap roundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
				.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap,
						deafalutPaint);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()+ reflectionGap, paint);
		return bitmapWithReflection;
	}
	 public static Bitmap createReflectedImage(Bitmap originalImage) {    
	        final int reflectionGap = 4;  //倒影和原图片间的距离  
	        int width = originalImage.getWidth();     
	        int height = originalImage.getHeight();  
	          
	        Matrix matrix = new Matrix();     
	        matrix.preScale(1, -1);    
	          
	        //倒影部分  
	        Bitmap reflectionImage = Bitmap.createBitmap(originalImage,   
	                0, height / 2, width, height / 2, matrix, false);    
	        //要返回的倒影图片  
	        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,     
	                (height + height / 2), Config.ARGB_8888);    
	          
	        Canvas canvas = new Canvas(bitmapWithReflection);    
	        //画原来的图片  
	        canvas.drawBitmap(originalImage, 0, 0, null);    
	          
	        Paint defaultPaint = new Paint();     
	        //倒影和原图片间的距离  
	        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);    
	        //画倒影部分  
	        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);    
	          
	        Paint paint = new Paint();     
	        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(),   
	                0, bitmapWithReflection.getHeight() + reflectionGap,   
	                0x70ffffff, 0x00ffffff,     
	                TileMode.MIRROR);    
	        paint.setShader(shader);    
	        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));    
	        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);    
	        return bitmapWithReflection;     
	    }  
	public static Bitmap Bytes2Bimap(byte[] b){  
        if(b.length!=0){  
            return BitmapFactory.decodeByteArray(b, 0, b.length);  
        }  
        else {  
            return null;  
        }  
	} 
	public static byte[] Bitmap2Bytes(Bitmap bm){  
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();    
	    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);    
	    return baos.toByteArray();  
	 } 
	public static String byte2hex(byte[] b) { 
		StringBuffer sb = new StringBuffer(b.length * 2);
		String tmp = "";
		for (int n = 0; n < b.length; n++) {
			tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (tmp.length() == 1) {
				sb.append("0");
			}
			sb.append(tmp);
		}
		return sb.toString().toUpperCase(); 
	}
	public static Bitmap scaleFile(String filepath,int maxWidth, int maxHeight){
		BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeFile(filepath, options); //此时返回bm为空  
        options.inJustDecodeBounds = false;  
        options.inSampleSize = computeSampleSize(options,-1,maxWidth*maxHeight);  
        //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap=BitmapFactory.decodeFile(filepath,options);  
        return bitmap;
	}
	public static Bitmap resizeBitmap(String filepath,int maxWidth, int maxHeight){
		BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeFile(filepath, options); 
        options.inJustDecodeBounds = false;  
        int originWidth  = options.outWidth;
        int originHeight =options.outHeight;
        // no need to resize
        if (originWidth < maxWidth && originHeight < maxHeight) {
            return bitmap;
        }
        float wb=originWidth/maxWidth;
        float hb=originHeight/maxHeight;
        // 若图片过宽, 则保持长宽比缩放图片
        if (originWidth > maxWidth||originHeight>maxHeight) {
        	if(wb>=hb){
	            int i =(int) Math.floor(originWidth * 1.0 / maxWidth);
	            options.inSampleSize = i;  
	            
        	}else{
        		int i = (int) Math.floor(originHeight * 1.0 / maxHeight);
	            options.inSampleSize = i;  
        	}
        }
        System.out.println("inSampleSize "+ options.inSampleSize);
        bitmap=BitmapFactory.decodeFile(filepath,options); 
        return bitmap;
	}
	public static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
	        int originWidth  = bitmap.getWidth();
	        int originHeight = bitmap.getHeight();
	        // no need to resize
	        if (originWidth < maxWidth && originHeight < maxHeight) {
	            return bitmap;
	        }
	        int width  = originWidth;
	        int height = originHeight;
	        float wb=originWidth/maxWidth;
	        float hb=originHeight/maxHeight;
	        // 若图片过宽, 则保持长宽比缩放图片
	        if (originWidth > maxWidth||originHeight>maxHeight) {
	        	if(wb>hb){
	        		width = maxWidth;
		            double i = originWidth * 1.0 / maxWidth;
		            height = (int) Math.floor(originHeight / i);
		            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
	        	}else{
	        		height = maxHeight;
		            double i = originHeight * 1.0 / maxHeight;
		            width = (int) Math.floor(originWidth / i);
		            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
	        	}
	        }
	        return bitmap;
	 }
	public static void bitmapToFile(Bitmap bm,String path){
		File file=new File(path);
        try {
        	file.createNewFile();
            FileOutputStream out=new FileOutputStream(file);
            if(bm.compress(Bitmap.CompressFormat.JPEG, 100, out)){
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	public static int computeSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);

	    int roundedSize;
	    if (initialSize <= 8 ) {
	        roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<= 1;
	        }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }

	    return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;
	    double h = options.outHeight;

	    int lowerBound = (maxNumOfPixels == -1) ? 1 :
	            (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 :
	            (int) Math.min(Math.floor(w / minSideLength),
	            Math.floor(h / minSideLength));

	    if (upperBound < lowerBound) {
	        // return the larger one when there is no overlapping zone.
	        return lowerBound;
	    }

	    if ((maxNumOfPixels == -1) &&
	            (minSideLength == -1)) {
	        return 1;
	    } else if (minSideLength == -1) {
	        return lowerBound;
	    } else {
	        return upperBound;
	    }
	}
}
