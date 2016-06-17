package com.github.ros_java.android_foo.bar;

import android.hardware.Camera;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ros.android.RosActivity;
import org.ros.android.view.camera.RosCameraPreviewView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import org.ros.android.OrientationPublisher;
import android.hardware.SensorManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;


public class Bar extends RosActivity {

    private TextView debugText;
    private Physicaloid mbed;
    private static final Log log = LogFactory.getLog(Bar.class);
    private SerialRelay serialRelay;
    private int cameraId;
    private RosCameraPreviewView rosCameraPreviewView;
    private OrientationPublisher orientPub;
    private SensorManager sensorManager;

        public Bar() {
            super("Demo0", "Demo0");
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.main);
            rosCameraPreviewView = (RosCameraPreviewView) findViewById(R.id.ros_camera_preview_view);
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mbed = new Physicaloid(this);
            log.info("Created...");
        }

        @Override
        protected void init(NodeMainExecutor nodeMainExecutor) {
            cameraId = 0;
            rosCameraPreviewView.setCamera(Camera.open(cameraId));
            serialRelay = new SerialRelay(mbed);
            orientPub = new OrientationPublisher(sensorManager);

            try {
                java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
                java.net.InetAddress local_network_address = socket.getLocalAddress();
                socket.close();
                NodeConfiguration nodeConfiguration =
                        NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());
                nodeMainExecutor.execute(rosCameraPreviewView, nodeConfiguration);
                nodeMainExecutor.execute(serialRelay, nodeConfiguration);
                nodeMainExecutor.execute(orientPub, nodeConfiguration);
            } catch (IOException e) {
                android.util.Log.e("Demo0", "socket error trying to get networking information from the master uri");
            }

        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mbed.close();
    }

}

