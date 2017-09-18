package com.richyan.android.imagegame;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    private int COL =4;
    private int ROW = 4;
    private Bitmap[] mImgs;
    private ImageView[] mImageViews;
    private int btnHeight;
    private int noei, noi;
    private int gi, gj;
    private int mIVwidth, mIVheight;
    private int[] ivIndex;
    final Handler handler = new Handler();
    private Timer timer;
    private TimerTask task;
    private int mm = 0;
    private int ss = 0;
    private boolean start = false;
    private int lastIVNum;
    private int imgId = R.drawable.buda;
    private Bitmap bitmap;
    private Uri imageUri;
    private boolean showNumber = false;
    private EditText timerEtxt;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_photo:
                Intent photoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(photoIntent, 0);
                return true;
            case R.id.action_recommend:
                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("text/plain");
                send.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.sendMsg));
                startActivity(Intent.createChooser(send, getResources().getString(R.string.share)));
                return true;
            case R.id.action_about:
                PopupFragment aboutFragment = new PopupFragment();
                aboutFragment.setmPopupId(3);
                aboutFragment.show(getSupportFragmentManager(),"about");
                return true;
            case R.id.action_camera:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent,1);
                }
                return true;
            case R.id.action_setting:
                PopupFragment settingFragment = new PopupFragment();
                settingFragment.setmPopupId(1);
                settingFragment.show(getSupportFragmentManager(),"setting");;
                return true;
            case R.id.action_login:
                return true;
            case R.id.action_scores:
                try {
                    InputStream inputStream = getApplicationContext().openFileInput("scores.txt");
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString="";
                    ArrayAdapter<String> records = new ArrayAdapter<String>(this, R.layout.simple_list_green_text);
                    while ( (receiveString=bufferedReader.readLine()) != null ) {
                        String temp = getResources().getString(R.string.player)+ receiveString;
                        temp = temp.replaceFirst("\t","\n"+getResources().getString(R.string.complete));
                        temp = temp.replaceAll("\t", "\n@: ");
                        records.insert(temp,0);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.scores,null);
                    builder.setView(view);
                    ListView lv = (ListView) view.findViewById(R.id.scoresLV);
                    lv.setAdapter(records);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    inputStream.close();
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),R.string.record,Toast.LENGTH_LONG).show();
                    return true;
                } catch (IOException e) {e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0 && resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            //Toast.makeText(getApplicationContext(),targetUri.toString(),Toast.LENGTH_LONG).show();
            Intent intent = new Intent(GameActivity.this,GameActivity.class);
            intent.putExtra("imageUri", targetUri.toString());
            intent.putExtra("COL", COL);
            intent.putExtra("showNumber",showNumber);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Intent intent = new Intent(GameActivity.this, GameActivity.class);
            intent.putExtra("bitmap", imageBitmap);
            intent.putExtra("COL", COL);
            intent.putExtra("showNumber",showNumber);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            if(bundle.get("bitmap")!=null) bitmap = (Bitmap)bundle.get("bitmap");
            else if(bundle.getString("imageUri")!=null) imageUri = Uri.parse(bundle.getString("imageUri"));
            else {
                imgId = bundle.getInt("imgId");
            }
            COL = ROW = bundle.getInt("COL");
            showNumber = bundle.getBoolean("showNumber");
        }
        if(bitmap !=null){
        } else if( imageUri!=null){
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else
            bitmap = BitmapFactory.decodeResource(getResources(), imgId);

        btnHeight = COL == 4? 55:20;
        mIVwidth = (screenWidth)/COL;
        mIVheight = (screenHeight-btnHeight)/(ROW+2);
        int imgWidth = bitmap.getWidth()/COL;
        int imgHeight = bitmap.getHeight()/ROW;

        mImgs = new Bitmap[COL*ROW];
        ivIndex = new int[COL*ROW];
        lastIVNum = COL*(ROW+1)-1;
        int count = 0;
        for (int x=0; x<COL;x++){
            for(int y=0; y<ROW; y++) {
                Bitmap temp = Bitmap.createBitmap(bitmap, imgWidth * y, imgHeight * x, imgWidth, imgHeight);
                mImgs[count] = Bitmap.createScaledBitmap(temp, mIVwidth-2, mIVheight-2, true);
                ivIndex[count]=count;
                mImgs[count]=drawBitmap(mImgs[count++],count+"");
            }
        }

        mImageViews = new ImageView[COL*ROW+ROW];
        final TextView nameTxt = (TextView)findViewById(R.id.nameTxt);
        timerEtxt = (EditText)findViewById(R.id.timeEtxt);
        setNumberOfEmptyIV(lastIVNum);
        for(int i=0; i<COL*ROW; i++){
            final int num = i;
            int id = getResId("image"+(i+1));
            mImageViews[i] = (ImageView)findViewById(id);
            mImageViews[i].setImageBitmap(mImgs[i]);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mImageViews[num].setX(num%COL*mIVwidth);
                            mImageViews[num].setY(((int)num/ROW)*mIVheight);
                        }
                    });
                }
            };
            new Thread(runnable).start();

            mImageViews[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    noi = ivIndex[num];
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_UP:
                            if(!testAvailability(getNumberOfEmptyIV(),noi)) return true;
                            if(!start) return true;
                            v.setX(noei%COL*mIVwidth);
                            v.setY(((int)noei/ROW)*mIVheight);
                            ivIndex[num] = noei;
                            setNumberOfEmptyIV(noi);
                            if(checkResult()){
                                Toast.makeText(getApplicationContext(),R.string.win,Toast.LENGTH_LONG).show();
                                timer.cancel();
                                timer.purge();
                                start = false;
                                mImageViews[lastIVNum].setVisibility(View.VISIBLE);
                                //File file = new File(getBaseContext().getFilesDir(),"scores.txt");
                                String temp= nameTxt.getText()+"\t"+timerEtxt.getText()+"\t";
                                DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                                String date = df.format(Calendar.getInstance().getTime());
                                temp = temp + date+"\n";
                                try {
                                    FileOutputStream stream = openFileOutput("scores.txt", MODE_APPEND);
                                    stream.write(temp.getBytes());
                                    stream.close();
                                } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                } catch (IOException e) {
                                       e.printStackTrace();
                                } finally {
                                }

                                return true;
                            }
                            break;
                    }
                    return true;
                }
            });
        }
        for(int i=COL*ROW; i<COL*(ROW+1);i++){
            int id = getResId("image"+(i+1));
            mImageViews[i]=(ImageView)findViewById(id);
            mImageViews[i].getLayoutParams().width = mIVwidth;
            mImageViews[i].getLayoutParams().height = mIVheight;
            mImageViews[i].setX(i%COL*mIVwidth);
            mImageViews[i].setY(((int)i/ROW)*mIVheight );
            mImageViews[i].setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        Button imageBtn =(Button)findViewById(R.id.imgBtn);
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupFragment imageFragment = new PopupFragment();
                imageFragment.setmPopupId(2);
                imageFragment.setImage(bitmap);
                imageFragment.show(getSupportFragmentManager(),"");
            }
        });
        final Button newBtn = (Button)findViewById(R.id.newBtn);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mm=0;
                ss=0;
                newBtn.setText(R.string.newStr);
                shuffleIVs(getNumberOfEmptyIV());
                if(!start) {
                    setTimerTask();
                    mImageViews[lastIVNum].setVisibility(View.GONE);
                    start = true;
                }
            }
        });

    }
    public boolean checkResult(){
        for(int i = 0; i<(COL* ROW); i++){
            if(ivIndex[i] !=i)
                return false;
        }
        return true;
    }
    private void setTimerTask(){
        timer  = new Timer();
        final Handler timerHandler = new Handler();

        task = new TimerTask() {
            @Override
            public void run() {
                ss++;
                if(ss>=60) {mm++; ss=00;}
                if(mm>=60) {timer.cancel();timer.purge();}
                timerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        timerEtxt.setText(((mm<10)?" 0":" ")+ mm+((ss<10)?" :0":" :")+ss );
                    }
                });
            }
        };
        timer.schedule(task, 1000l, 1000L);
    }
    private int getResId(String idName) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(idName, "id", packageName);
        return resId;
    }
    public final void shuffleIVs(int emptyIV){
        Random rand = new Random();
        for(int i=0; i<ROW*COL-1;i++){
            int j = rand.nextInt(COL* ROW/4);
            if(i!=emptyIV && j!=emptyIV)
                swapImageView(i,j);
        }
    }

    private void swapImageView(int m, int n){
        for(int gc=0; gc<COL*ROW;gc++){
            if(ivIndex[gc] == m) gi=gc;
            if(ivIndex[gc] == n) gj=gc;
        }
        int w = n%COL*mIVwidth;
        int h = (int)n/ROW * mIVheight;
        mImageViews[gi].setX(w);
        mImageViews[gi].setY(h);

        w = m%COL*mIVwidth;
        h = (int)m/ROW * mIVheight;
        mImageViews[gj].setX(w);
        mImageViews[gj].setY(h);

        ivIndex[gi] = n;
        ivIndex[gj] = m;
    }
    private Bitmap drawBitmap(Bitmap bitmap, String mText){
        Bitmap bmpWithBorder = Bitmap.createBitmap(bitmap.getWidth()+2, bitmap.getHeight()+2, bitmap.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 1, 1, null);
        if(showNumber) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.rgb(255, 255, 255));
            // text size in pixels
            float scale = this.getResources().getDisplayMetrics().density;
            paint.setTextSize((int) (12 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            canvas.drawText(mText, 10 * scale, 10 * scale, paint);
        }
        return bmpWithBorder;
    }

    public void setNumberOfEmptyIV(int numberOfEmptyIV) {
        this.noei = numberOfEmptyIV;
    }
    public int getNumberOfEmptyIV(){
        return this.noei;
    }
    private boolean testAvailability(int noei,int noi){
        if(Math.abs(noei-noi)==COL) return true;
        else if(Math.abs(noei-noi)==1) { return true;
            //if(noei%COL==0 ) return (noei-noi==1)? true : false;
            //else if((noei+1)%COL==1 ) return noei-noi==1? true : false;
            //else return true;
        }
        return false;
    }
}
