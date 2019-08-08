
package com.example.ashwiniabhishek.guesscelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.PatternMatcher;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celeb = new ArrayList<>();
    ArrayList<String> celebName = new ArrayList<>();
    ImageView imageView;
    ImageTask imageTask;
    Bitmap bitmap;
    String correctArray[];
    int chosenCeleb;
    int correctAnswer=0;

    Button button0;
    Button button1;
    Button button2;
    Button button3;
    public void heart(View view){
        if(view.getTag().toString().equals(Integer.toString(correctAnswer))){
            Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Wrong,correct answer is "+celebName.get(chosenCeleb),Toast.LENGTH_SHORT).show();

        }
        chageQuestion();
    }
    void chageQuestion(){
        ImageTask imageTask = new ImageTask();
        Random random = new Random();
        chosenCeleb=random.nextInt(celeb.size());
        try {
            bitmap = imageTask.execute(celeb.get(chosenCeleb)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
        correctAnswer = random.nextInt(4);
        correctArray = new String[4];
        for(int i=0;i<4;i++){
            if(i==correctAnswer){
                correctArray[correctAnswer]=celebName.get(chosenCeleb);
            }
            else{
                int incorrect = random.nextInt(celeb.size());
                while(incorrect==chosenCeleb)
                    incorrect = random.nextInt(celeb.size());
                correctArray[i]=celebName.get(incorrect);
            }
        }
        button0.setText(correctArray[0]);
        button1.setText(correctArray[1]);
        button2.setText(correctArray[2]);
        button3.setText(correctArray[3]);

    }

    class ImageTask extends  AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
               // BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                int data = reader.read();
                while(data!=-1){
                    char current = (char)data;
                    result= result+current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button0=findViewById(R.id.button);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        DownloadTask task = new DownloadTask();
        String result = null;
        imageView = findViewById(R.id.imageView);
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
            String SplitResult[] = result.split("<div class=\"sidebarContainer\">");
            Pattern pattern = Pattern.compile("img src=\"(.*?)\"");
            Matcher matcher = pattern.matcher(SplitResult[0]);
            while(matcher.find()){
                //System.out.println(matcher.group(1));
                celeb.add(matcher.group(1));
            }
            pattern = Pattern.compile("alt=\"(.*?)\"");
            matcher = pattern.matcher(SplitResult[0]);
            while(matcher.find()){
                //System.out.println(matcher.group(1));
                celebName.add(matcher.group(1));
            }

                chageQuestion();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
