package com.lofarolabs.udpsender;

import android.graphics.Point;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;

import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.SystemClock;
import android.os.StrictMode;
import android.widget.EditText;
import android.view.MotionEvent;
import android.graphics.PointF;
import android.util.SparseArray;
import android.graphics.drawable.Drawable;
import android.view.SurfaceHolder;
import android.content.Context;
import java.util.TimerTask;
import java.util.Timer;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.widget.ImageView;
import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import android.text.SpannableString;
import android.widget.Toast;

public class MainSend extends ActionBarActivity {

    /*define button constants */
    private final static int touchPad = 273;
    private final static int rightJoyTick =274;
    private final static int leftJoySick = 275;
    private final static int atButton = 276;
    private final static int andButton = 277;
    private final static int percentButton = 278;
    private final static int hashButton = 279;
    private final static int startButton = 280;
    private final static int selectButton = 281;

    private static final String host = null;
    private int port;
    String str=null;
    byte[] send_data = new byte[1024];
    byte[] receiveData = new byte[1024];
    Button bt_open_port,bt_send_port,bt3,bt4;
    TextView txt0,txt1, txt_touch_x, txt_touch_y;
    EditText txt_ip, txt_port;
    DatagramSocket client_socket = null;
    int mPort = 2362;
    private SparseArray<PointF> mActivePointers;
    boolean pressedUp = false;
    private SurfaceHolder holder;
    Timer t = new Timer();
    Paint paint = new Paint();
    ImageView drawingImageView;

    /*Ratios for buttons */
    double xRatioAtButton = 0.865625;
    double yRatioAtButton = 0.182181;
    double xRatioHashButton= 0.774219;
    double yRatioHashButton = 0.332447;
    double xRatioAndButton = 0.870313;
    double yRatioAndButton = 0.461436;
    double xRatioPercentButton = 0.939844;
    double yRatioPercentButton = 0.321809;
    double xRatioLeftJoyStick = 0.139844;
    double yRatioLeftJoyStick = 0.776596;
    double xRatioRightJoyStick= 0.861719;
    double yRatioRightJoyStick = 0.776596;
    double xRatioSelect = 0.071875;
    double yRatioSelect = 0.531915;
    double xRatioStart = 0.196094;
    double yRatioStart = 0.531915;
    double xRatioCenter = 0.497656;
    double yRatioCenter = 0.553191;
    /* button location declarations */
    double touch_center_y;
    double touch_center_x;
    double touch_delta_x = 300;
    double touch_delta_y = 180;
    double joy_left_center_x;
    double joy_left_center_y;
    double joy_right_center_x;
    double joy_right_center_y;
    double joy_right_radius = 100;
    double joy_left_radius = 100;
    double button_and_center_x;
    double button_and_center_y;
    double button_and_radius=55;
    double button_at_center_x;
    double button_at_center_y;
    double button_at_radius=55;
    double button_hash_center_x;
    double button_hash_center_y;
    double button_hash_radius=55;
    double button_percent_center_x;
    double button_percent_center_y;
    double button_percent_radius=55;
    double button_start_center_x;
    double button_start_center_y;
    double button_start_delta_x=95;
    double button_start_delta_y=45;
    double button_select_center_x;
    double button_select_center_y;
    double button_select_delta_x = 95;
    double button_select_delta_y = 45;
    ImageView buttonAnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher_arc);
        setContentView(R.layout.activity_main_send);

        mActivePointers = new SparseArray<PointF>();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Log.e("WIDTH: ", Integer.toString(size.x));
        Log.e("HEIGHT: ",Integer.toString(size.y));

/*      DO WE NEED DENSITY?
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float densityDpi = metrics.density;*/

        /*assign coordinates */
        /*buttons */
        button_and_center_x = xRatioAndButton * size.x;
        button_and_center_y = yRatioAndButton * size.y;
        button_at_center_x  = xRatioAtButton * size.x;
        button_at_center_y  = yRatioAtButton *size.y;
        button_hash_center_x = xRatioHashButton * size.x;
        button_hash_center_y = yRatioHashButton * size.y;
        button_percent_center_x = xRatioPercentButton * size.x;
        button_percent_center_y = yRatioPercentButton * size.y;
        /*joy stick */
        joy_left_center_x = xRatioLeftJoyStick*size.x;
        joy_left_center_y = yRatioLeftJoyStick*size.y;
        joy_right_center_x= xRatioRightJoyStick*size.x;
        joy_right_center_y = yRatioRightJoyStick*size.y;
        /*select and start */
        button_select_center_x = xRatioSelect *size.x;
        button_select_center_y = yRatioSelect *size.y;
        button_start_center_x = xRatioStart * size.x;
        button_start_center_y = yRatioStart * size.y;
        /*center piece */
        touch_center_x = xRatioCenter*size.x;
        touch_center_y = yRatioCenter*size.y;
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        //set focusable
        txt_ip   = (EditText)findViewById(R.id.editText_ip);
        txt_ip.clearFocus();
        txt_ip.setFocusableInTouchMode(true);
        txt_port   = (EditText)findViewById(R.id.editText_port);
        txt_port.setFocusable(false);
        txt_port.setFocusableInTouchMode(true);
        txt_port.setOnEditorActionListener(clearReturnToFullScreen());
        txt_ip.setOnEditorActionListener(clearReturnToFullScreen());

        txt1   = (TextView)findViewById(R.id.textView_top);
        bt_open_port = (Button) findViewById(R.id.button_open_port);
        bt_open_port.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                str="temp";
                try {
                    client_open();

                    //txt1.setText(modifiedSentence);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    txt1.setText(e.toString());
                    e.printStackTrace();
                }
            }

        });

        bt_send_port = (Button) findViewById(R.id.button_send_port);
        bt_send_port.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Perform action on click
                //textIn.setText("test");
                //txt2.setText("text2");
                //task.execute(null);
                str="temp";
                try {
                    client_send();


                    //txt1.setText(modifiedSentence);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    txt1.setText(e.toString());
                    e.printStackTrace();
                }
            }

        });

    }
    @Override
    protected void onResume(){
        super.onResume();
                /* try to hide status bar */
        final View decorView = getWindow().getDecorView();
        final int uiOptions = decorView.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener(){
            @Override
        public void onSystemUiVisibilityChange(int visibility){
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN)==0){
                    //nothing
                }else{
                    decorView.setSystemUiVisibility(uiOptions);
                }
            }
        });
    }
    EditText.OnEditorActionListener clearReturnToFullScreen(){
        EditText.OnEditorActionListener actionListener = new EditText.OnEditorActionListener(){
            @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId== EditorInfo.IME_ACTION_DONE){
                    v.clearFocus();
                    final View decorView = getWindow().getDecorView();
                    final int uiOptions = decorView.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);
                }
                return false;
            }
        };
        return actionListener;
    }
//    View.OnFocusChangeListener returnToFullScreen(){
//       View.OnFocusChangeListener focusChangeListener= new View.OnFocusChangeListener(){
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus){
//                    final View decorView = getWindow().getDecorView();
//                    final int uiOptions = decorView.SYSTEM_UI_FLAG_FULLSCREEN;
//                    decorView.setSystemUiVisibility(uiOptions);
//                }
//            }
//
//
//        };
//               return focusChangeListener;
//    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        /* try to hide status bar */
        if (!hasFocus) {
            final View decorView = getWindow().getDecorView();
            int uiOptions = decorView.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_send, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Location information */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        txt_touch_x   = (TextView)findViewById(R.id.textView_touch_x);
        txt_touch_y   = (TextView)findViewById(R.id.textView_touch_y);
        txt_touch_x.setText(Integer.toString(x));
        txt_touch_y.setText(Integer.toString(y));


        int pressedButton = firstPress(x, y);
        if (pressedButton!=touchPad && pressedButton!=rightJoyTick && pressedButton!=leftJoySick){
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    txt1.setText("dana");
                    for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                        txt1.setText("danb");
                        PointF point = mActivePointers.get(event.getPointerId(i));
                        txt1.setText("danc");
                        if (1==1) {
                            txt1.setText("dand");
                            float xx = event.getX(i);
                            float yy = event.getY(i);

                            boolean openFlag = false;
                            if (!(client_socket == null)) {
                                openFlag = (client_socket.getLocalPort() == Integer.parseInt(txt_port.getText().toString())) & !client_socket.isClosed();
                            }

                            if(openFlag) {
                                String buff = parseTouch(xx, yy);
                                if(buff != null) {
                                    try {
                                        client_send_buff(buff);
                                        //txt1.setText(modifiedSentence);
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        txt1.setText(e.toString());
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    pressedUp = false;
                    break;
            }
        }else {
        /* else for joysticks and touch pad */
            txt1.setText("dana");
            for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                txt1.setText("danb");
                PointF point = mActivePointers.get(event.getPointerId(i));
                txt1.setText("danc");
                if (1 == 1) {
                    txt1.setText("dand");
                    float xx = event.getX(i);
                    float yy = event.getY(i);

                    boolean openFlag = false;
                    if (!(client_socket == null)) {
                        openFlag = (client_socket.getLocalPort() == Integer.parseInt(txt_port.getText().toString())) & !client_socket.isClosed();
                    }

                    if (openFlag) {
                        String buff = parseTouch(xx, yy);
                        if (buff != null) {
                            try {
                                client_send_buff(buff);
                                //txt1.setText(modifiedSentence);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                txt1.setText(e.toString());
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }
        }
        return false;
    }
/*
    public double[] getCenterSquare(float x, float y) {
        double[] xy = {0.0, 0.0};
        xy[0] =
        return xy;
    }
*/
    //public void client() throws IOException {
    public void client_open() throws IOException {
        //SystemClock.sleep(1000);
        txt_port   = (EditText)findViewById(R.id.editText_port);
        mPort = Integer.parseInt(txt_port.getText().toString());
        client_socket = new DatagramSocket(mPort);
        bt_open_port.setText("Port Open");
    }
    public void client_send() throws IOException {
        //SystemClock.sleep(1000);
        txt_ip   = (EditText)findViewById(R.id.editText_ip);

        InetAddress IPAddress =  InetAddress.getByName(txt_ip.getText().toString());
        str="dan1";
        send_data = str.getBytes();
        DatagramPacket send_packet = new DatagramPacket(send_data,str.length(), IPAddress, mPort);
        client_socket.send(send_packet);
    }

    public void client_send_buff(String buff) throws IOException {
        //SystemClock.sleep(1000);
        txt_ip   = (EditText)findViewById(R.id.editText_ip);
        InetAddress IPAddress =  InetAddress.getByName(txt_ip.getText().toString());
        str=buff;
        send_data = str.getBytes();
        DatagramPacket send_packet = new DatagramPacket(send_data,str.length(), IPAddress, mPort);
        client_socket.send(send_packet);
        try {Thread.sleep(10);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void send_buff(String buff) {
        //SystemClock.sleep(1000);
        try {
            client_send_buff(buff);
            //txt1.setText(modifiedSentence);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            txt1.setText(e.toString());
            e.printStackTrace();
        }

    }

    public static byte[] toByteArray(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
    }

    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }
    public int firstPress(float x, float y){
        if(
                x > (touch_center_x-touch_delta_x) &
                        x < (touch_center_x+touch_delta_x) &
                        y > (touch_center_y-touch_delta_y) &
                        y < (touch_center_y+touch_delta_y))
            return touchPad;
        else if((Math.sqrt((x-joy_left_center_x)*(x-joy_left_center_x) +
                (y-joy_left_center_y)*(y-joy_left_center_y)) < joy_left_radius))
            return leftJoySick;
        else if((Math.sqrt((x-joy_right_center_x)*(x-joy_right_center_x) +
                (y-joy_right_center_y)*(y-joy_right_center_y)) < joy_right_radius))
            return rightJoyTick;
        else if((Math.sqrt((x-button_at_center_x)*(x-button_at_center_x) +
                (y-button_at_center_y)*(y-button_at_center_y)) < button_at_radius))
            return atButton;
        else if((Math.sqrt((x-button_hash_center_x)*(x-button_hash_center_x) +
                (y-button_hash_center_y)*(y-button_hash_center_y)) < button_hash_radius))
            return hashButton;
        else if((Math.sqrt((x-button_percent_center_x)*(x-button_percent_center_x) +
                (y-button_percent_center_y)*(y-button_percent_center_y)) < button_percent_radius))
            return percentButton;
        else if ((Math.sqrt((x-button_and_center_x)*(x-button_and_center_x) +
                (y-button_and_center_y)*(y-button_and_center_y)) < button_and_radius))
            return andButton;
        else if(x > (button_start_center_x-button_start_delta_x) &
                x < (button_start_center_x+button_start_delta_x) &
                y > (button_start_center_y-button_start_delta_y) &
                y < (button_start_center_y+button_start_delta_y))
            return startButton;
        else if(x > (button_select_center_x-button_select_delta_x) &
                x < (button_select_center_x+button_select_delta_x) &
                y > (button_select_center_y-button_select_delta_y) &
                y < (button_select_center_y+button_select_delta_y))
            return selectButton;
        else
            return -1;
    }


    public String parseTouch(float x, float y){
        byte[] button = {0};
        double[] xy = {0.0, 0.0};
        String theOut = null;


        /* main touch */
        if(
                x > (touch_center_x-touch_delta_x) &
                x < (touch_center_x+touch_delta_x) &
                y > (touch_center_y-touch_delta_y) &
                y < (touch_center_y+touch_delta_y))
        {
            button = ("t").getBytes();
            double xx = (x-touch_center_x)/touch_delta_x;
            double yy = -(y-touch_center_y)/touch_delta_y;

            try {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                outputStream.write( ("touch").getBytes() );
                outputStream.write( (" ").getBytes() );
                outputStream.write( (String.format("%.3f", xx)).getBytes());
                outputStream.write( (" ").getBytes() );
                outputStream.write( (String.format("%.3f", yy)).getBytes());
                //outputStream.write( toByteArray(xx) );
                //outputStream.write(toByteArray(yy));
                txt1.setText(outputStream.toString());
                //outputStream.toByteArray();
                theOut = outputStream.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                txt1.setText(e.toString());
                e.printStackTrace();
            }

        }
        /* left joystick */
        else if(
                (Math.sqrt((x-joy_left_center_x)*(x-joy_left_center_x) + (y-joy_left_center_y)*(y-joy_left_center_y)) < joy_left_radius))
        {
            button = ("t").getBytes();
            double xx = (x-joy_left_center_x)/joy_left_radius;
            double yy = -(y-joy_left_center_y)/joy_left_radius;

            try {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                outputStream.write( ("joy left").getBytes() );
                outputStream.write( (" ").getBytes() );
                outputStream.write( (String.format("%.3f", xx)).getBytes());
                outputStream.write( (" ").getBytes() );
                outputStream.write( (String.format("%.3f", yy)).getBytes());
                //outputStream.write( toByteArray(xx) );
                //outputStream.write(toByteArray(yy));
                txt1.setText(outputStream.toString());
                //outputStream.toByteArray();
                theOut = outputStream.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                txt1.setText(e.toString());
                e.printStackTrace();
            }

        }
        /* right joystick */
        else if(
                (Math.sqrt((x-joy_right_center_x)*(x-joy_right_center_x) + (y-joy_right_center_y)*(y-joy_right_center_y)) < joy_right_radius))
        {
            button = ("t").getBytes();
            double xx = (x-joy_right_center_x)/joy_right_radius;
            double yy = -(y-joy_right_center_y)/joy_right_radius;

            try {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                outputStream.write( ("joy right").getBytes() );
                outputStream.write( (" ").getBytes() );
                outputStream.write( (String.format("%.3f", xx)).getBytes());
                outputStream.write( (" ").getBytes() );
                outputStream.write( (String.format("%.3f", yy)).getBytes());
                //outputStream.write( toByteArray(xx) );
                //outputStream.write(toByteArray(yy));
                txt1.setText(outputStream.toString());
                //outputStream.toByteArray();
                theOut = outputStream.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                txt1.setText(e.toString());
                e.printStackTrace();
            }

        }

        /* @button */
        else if(
                (Math.sqrt((x-button_at_center_x)*(x-button_at_center_x) + (y-button_at_center_y)*(y-button_at_center_y)) < button_at_radius))
        {
            button = ("t").getBytes();
            double xx = (x-button_at_center_x)/button_at_radius;
            double yy = -(y-button_at_center_y)/button_at_radius;
            startVibrate();
            Log.e("@BUTTON: ", "I was pressed");


            try {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                outputStream.write( ("button @").getBytes() );
                outputStream.write( (" ").getBytes() );
                outputStream.write( (String.format("%d", 1)).getBytes());
                //outputStream.write( toByteArray(xx) );
                //outputStream.write(toByteArray(yy));
                txt1.setText(outputStream.toString());
                //outputStream.toByteArray();
                theOut = outputStream.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                txt1.setText(e.toString());
                e.printStackTrace();
            }

        }


        /* #button */
        else if(
                (Math.sqrt((x-button_hash_center_x)*(x-button_hash_center_x) + (y-button_hash_center_y)*(y-button_hash_center_y)) < button_hash_radius))
        {
            button = ("t").getBytes();
            double xx = (x-button_hash_center_x)/button_hash_radius;
            double yy = -(y-button_hash_center_y)/button_hash_radius;
            Log.e("#BUTTON: ","I was pressed");
            startVibrate();

            try {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                outputStream.write( ("button #").getBytes() );
                outputStream.write( (" ").getBytes() );
                outputStream.write( (String.format("%d", 1)).getBytes());
                //outputStream.write( toByteArray(xx) );
                //outputStream.write(toByteArray(yy));
                txt1.setText(outputStream.toString());
                //outputStream.toByteArray();
                theOut = outputStream.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                txt1.setText(e.toString());
                e.printStackTrace();
            }

        }


        /* %button */
        else if(
                (Math.sqrt((x-button_percent_center_x)*(x-button_percent_center_x) + (y-button_percent_center_y)*(y-button_percent_center_y)) < button_percent_radius))
        {
            button = ("t").getBytes();
            double xx = (x-button_percent_center_x)/button_percent_radius;
            double yy = -(y-button_percent_center_y)/button_percent_radius;
            Log.e("%BUTTON: ","I was pressed");
            startVibrate();

            try {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                outputStream.write( ("button %").getBytes() );
                outputStream.write( (" ").getBytes() );
                outputStream.write( (String.format("%d", 1)).getBytes());
                //outputStream.write( toByteArray(xx) );
                //outputStream.write(toByteArray(yy));
                txt1.setText(outputStream.toString());
                //outputStream.toByteArray();
                theOut = outputStream.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                txt1.setText(e.toString());
                e.printStackTrace();
            }

        }

        /* &button */
        else if(
                (Math.sqrt((x-button_and_center_x)*(x-button_and_center_x) + (y-button_and_center_y)*(y-button_and_center_y)) < button_and_radius))
        {
            button = ("t").getBytes();
            double xx = (x-button_and_center_x)/button_and_radius;
            double yy = -(y-button_and_center_y)/button_and_radius;
            Log.e("&BUTTON: ", "I was pressed");
            startVibrate();


            try {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                outputStream.write( ("button &").getBytes() );
                outputStream.write( (" ").getBytes() );
                outputStream.write( (String.format("%d", 1)).getBytes());
                //outputStream.write( toByteArray(xx) );
                //outputStream.write(toByteArray(yy));
                txt1.setText(outputStream.toString());
                //outputStream.toByteArray();
                theOut = outputStream.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                txt1.setText(e.toString());
                e.printStackTrace();
            }

        }

        /* start button */
        else if(
                x > (button_start_center_x-button_start_delta_x) &
                x < (button_start_center_x+button_start_delta_x) &
                y > (button_start_center_y-button_start_delta_y) &
                y < (button_start_center_y+button_start_delta_y))
        {
            try {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                outputStream.write( ("button start").getBytes() );
                outputStream.write( (" ").getBytes() );
                outputStream.write( (String.format("%d", 1)).getBytes());
                //outputStream.write( toByteArray(xx) );
                //outputStream.write(toByteArray(yy));
                txt1.setText(outputStream.toString());
                //outputStream.toByteArray();
                theOut = outputStream.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                txt1.setText(e.toString());
                e.printStackTrace();
            }

        }

        /* select button */
        else if(
                x > (button_select_center_x-button_select_delta_x) &
                x < (button_select_center_x+button_select_delta_x) &
                y > (button_select_center_y-button_select_delta_y) &
                y < (button_select_center_y+button_select_delta_y))
        {
            try {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                outputStream.write( ("button select").getBytes() );
                outputStream.write( (" ").getBytes() );
                outputStream.write( (String.format("%d", 1)).getBytes());
                //outputStream.write( toByteArray(xx) );
                //outputStream.write(toByteArray(yy));
                txt1.setText(outputStream.toString());
                //outputStream.toByteArray();
                theOut = outputStream.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                txt1.setText(e.toString());
                e.printStackTrace();
            }

        }



        return theOut;
    }
    Vibrator vibrator;
    public void startVibrate(){
        long pattern[]={0, 100};
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern,-1);
    }
    public void stopVibrate(){
        vibrator.cancel();
    }
}

