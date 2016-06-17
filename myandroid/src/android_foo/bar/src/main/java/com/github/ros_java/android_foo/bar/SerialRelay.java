package com.github.ros_java.android_foo.bar;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;


import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import std_msgs.String;

/**
 * Created by michael on 6/11/16.
 */
public class SerialRelay extends AbstractNodeMain {

  //  private Publisher<std_msgs.String> publisher;
  //  private Subscriber<std_msgs.String> subscriber;
    private final Physicaloid mbed;

    public SerialRelay(Physicaloid device){
        this.mbed = device;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android/SerialRelay");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        final Publisher<std_msgs.String> publisher = connectedNode.newPublisher("android/SerialPub", std_msgs.String._TYPE);
        final Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("android/SerialSub", std_msgs.String._TYPE);

        if(mbed.open())
        {
            mbed.setBaudrate(9600);
            mbed.addReadListener(new ReadLisener() {
                @Override
                public void onRead(int size) {
                    byte[] buf = new byte[size];
                    mbed.read(buf,size);
                    std_msgs.String str = publisher.newMessage();
                    try {
                        str.setData(new java.lang.String(buf, "UTF-8"));
                    }catch(Exception e)
                    {
                        str.setData("Exception Caught!");
                    }
                    publisher.publish(str);
                }
            });

            subscriber.addMessageListener(new MessageListener<String>() {
                @Override
                public void onNewMessage(std_msgs.String message) {
                    byte[] wdata = message.getData().getBytes();
                    mbed.write(wdata);
                }
            });
        }
        else
        {
        }
    }
}
