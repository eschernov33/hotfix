package com.travels.searchtravels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.api.services.vision.v1.model.LatLng;
import com.preview.planner.prefs.AppPreferences;
import com.travels.searchtravels.api.OnVisionApiListener;
import com.travels.searchtravels.api.VisionApi;
import com.travels.searchtravels.tests.FPicture;

import org.bouncycastle.crypto.tls.AlertLevel;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


 /*

    FPicture - класс, находящийся в com.travels.searchtravels.tests.
    Содержит 2 поля - ссылка на изображение и тип, например sea, beach
    getBitmapFromURL - создает Bitmap по ссылке
    testType - проверяет корректность определения типа изображения (sea, beach, etc)

 */

@RunWith(AndroidJUnit4.class)
@Config(sdk = Build.VERSION_CODES.P)
public class InstrumentedTestPictureType {
    private List<FPicture> listPicture() {
        List<FPicture> list = new ArrayList<>();
        list.add(new FPicture("https://www.krym4you.com/files/catalog/138/gallery/big//chernoe-more_1435752144.jpg", "sea"));
        list.add(new FPicture("https://laguna-kerch.ru/userfiles/articles/azovskoe-more-shtorm_1545999776.jpg", "sea"));
        list.add(new FPicture("https://naturae.ru/foto/tihiy-okean.jpg", "ocean"));
        list.add(new FPicture("https://blog.tutoronline.ru/media/621769/best-fishing-spots-emt.jpg", "ocean"));
        list.add(new FPicture("https://blacksea7.com/images/stories/sea/peschanye-plyazhi-chernogo-morya/peschanye-plyazhi-chernogo-morya.jpg", "beach"));
        list.add(new FPicture("https://image.freepik.com/free-photo/empty-sea-and-beach-background_74190-1749.jpg", "beach"));
        list.add(new FPicture("https://asiamountains.net/upload/slide/slide-1960x857-07.jpg", "mountain"));
        list.add(new FPicture("https://vesti.ua/wp-content/uploads/2020/06/samye-vysokie-gory-800x530.jpg", "mountain"));
        list.add(new FPicture("https://cdnimg.rg.ru/img/content/134/83/54/sneg1_d_850.jpg", "snow"));
        list.add(new FPicture("https://www.lada.kz/uploads/posts/2017-09/1506677532_x6jqfbh2-580.jpg", "snow"));
        return list;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    //Проверка на соответствие картинки/категори
    @Test
    public void testType()
    {
        List<FPicture> list = listPicture();
        for (FPicture fPicture: list) {
            Bitmap bitmap = getBitmapFromURL(fPicture.getImgPath());
            if (bitmap != null) {
                VisionApi.findLocation(bitmap, AppPreferences.INSTANCE.getToken(ApplicationProvider.getApplicationContext()), new OnVisionApiListener() {
                    @Override
                    public void onSuccess(LatLng latLng) {

                    }

                    @Override
                    public void onErrorPlace(String category) {
                        Assert.assertEquals("Error: wrong category: " + category + " for " + fPicture.getTypePhoto(), category, fPicture.getTypePhoto());
                    }

                    @Override
                    public void onError() {
                        Assert.fail("Error: not detect or other error");
                    }
                });
            }
        }
    }

    //Проверка получения цены
    @Test
    public void testCost(){
        URL obj = null;
        Integer ticketPrice = 0;
        try {
            obj = new URL("https://api.travelpayouts.com/v1/prices/cheap?origin=LED&depart_date=2019-12&return_date=2019-12&token=471ae7d420d82eb92428018ec458623b&destination=MOW");
            HttpURLConnection connection2 = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            connection2.setRequestMethod("GET");
            connection2.setRequestProperty("User-Agent", "Mozilla/5.0" );
            connection2.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            connection2.setRequestProperty("Content-Type", "application/json");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();
            JSONObject responseJSON = new JSONObject(response.toString());
            ticketPrice = Integer.parseInt(responseJSON.getJSONObject("data").getJSONObject("MOW").getJSONObject("1").getString("price"));
            Assert.assertNotEquals("Price not parcing", ticketPrice+"", 0+"");
        }catch (Exception e){
            Assert.fail("Error parsing api.travelpayouts.com");
        }
    }

}
