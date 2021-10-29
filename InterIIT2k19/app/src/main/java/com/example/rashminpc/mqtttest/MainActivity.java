package com.example.rashminpc.mqtttest;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    float hrv; int h,s1,d,f,a,s2,hrv2;
    private LineChart mChart;
    public void act(View v)
    {
        Intent i = new Intent(this,Main3Activity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("Live Heart Beat");
        mChart.setHighlightPerDragEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.WHITE);
        //mChart.
        LineData data = new LineData();
        data.setValueTextColor(Color.RED);
        mChart.setData(data);
        XAxis x1= mChart.getXAxis();
        x1.setTextColor(Color.BLACK);
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);
        YAxis y1= mChart.getAxisLeft();
        y1.setTextColor(Color.BLACK);
        y1.setAxisMaximum(90f);
        y1.setAxisMinimum(60f);
        y1.setDrawGridLines(false);
        x1.setAxisLineWidth(2f);
        YAxis y12= mChart.getAxisRight();
        y12.setEnabled(false);

        connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<200;i++){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    });try{
                        Thread.sleep(600);} catch (InterruptedException e){}

                }
            }
        }).start();
    }

    private void addEntry(){
        LineData data= mChart.getData();
        if(data ==null) {
            data = new LineData();
            mChart.setData(data);}
        ILineDataSet set= data.getDataSetByIndex(0);
        if(set==null){
            set= createSet();
            data.addDataSet(set);
        }
        int randomDataSetIndex = (int) (Math.random() * data.getDataSetCount());
        ILineDataSet randomSet = data.getDataSetByIndex(randomDataSetIndex);
        //float value = (float) (Math.random() * 50) + 50f * (randomDataSetIndex + 1);

        data.addEntry(new Entry(randomSet.getEntryCount(), hrv/10), randomDataSetIndex);
        data.notifyDataChanged();
        mChart.notifyDataSetChanged();

        //mChart.setVisibleXRangeMaximum(6);
        mChart.setVisibleXRangeMinimum(15);
        //chart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
//
//            // this automatically refreshes the chart (calls invalidate())
        mChart.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);}

private LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null,"Heart Beat");
        set.setDrawCircles(true);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.RED);
        set.setCircleColor(Color.RED);
        set.setLineWidth(2f);
        set.setCircleHoleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244,117,117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);
        return set;
}
   public void connect(){

        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://farmer.cloudmqtt.com:10813",
                        clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setCleanSession(false);
        options.setUserName("tjixgrhv");
        options.setPassword("IIkOaqzK-gq1".toCharArray());
        try {
            IMqttToken token = client.connect(options);
            //IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // connected
                    Log.e("file", "onSuccess");
                    publish(client,"payloadd");
                    subscribe(client,"hrv");
                    client.setCallback(new MqttCallback() {
                        TextView happy = (TextView) findViewById(R.id.happy);
                        TextView sad = (TextView) findViewById(R.id.sad);

                        TextView disgust = (TextView) findViewById(R.id.disgust);

                        TextView fear = (TextView) findViewById(R.id.fear);

                        TextView anger = (TextView) findViewById(R.id.anger);

                        TextView surprised = (TextView) findViewById(R.id.suprised);


                        @Override
                        public void connectionLost(Throwable cause) {
                            Log.d("abcd","failed" );

                        }
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            Log.d("file", message.toString());

                            if (topic.equals("hrv")){
                                hrv= Float.parseFloat(message.toString());
                                hrv2=Math.round(hrv);
                             

                               happy.setText(Integer.toString(h));

                                sad.setText(Integer.toString(s1));

                                disgust.setText(Integer.toString(d));

                                fear.setText(Integer.toString(f));

                                anger.setText(Integer.toString(a));

                                surprised.setText(Integer.toString(s2));

                            }

                        }
                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("file", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(MqttAndroidClient client, String payload){
        String topic = "foo/bar";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(MqttAndroidClient client , String topic){
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}


