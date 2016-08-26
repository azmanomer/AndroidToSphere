package com.example.javier.uoconnector.GUI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.javier.uoconnector.Client.ClientThread;
import com.example.javier.uoconnector.Client.Connector;
import com.example.javier.uoconnector.Client.ServerInfo;
import com.example.javier.uoconnector.Client.UserInfo;
import com.example.javier.uoconnector.R;


public class MainActivity extends AppCompatActivity
{
    public static eRunMode _RunMode;
    public static ClientThread thread;
    public static TextView tv;
    private Connector connector;
    public static ServerInfo serverInfo = null;
    public static UserInfo userInfo = null;

    public enum eRunMode
    {
        RM_Running,
        RM_Loading
    }

    public void onButtonClick(View view)
    {
        MainActivity.tv.setText("");
        if (connector != null)
            connector.socketClose();
        connector = new Connector();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        _RunMode = eRunMode.RM_Loading;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverInfo = new ServerInfo();
        userInfo = new UserInfo();
        tv = (TextView) findViewById(R.id.tvMain);

        _RunMode = eRunMode.RM_Running;
        connector = new Connector();
    }

    public void onDestroy()
    {
        super.onDestroy();
        connector.socketClose();
    }


}
