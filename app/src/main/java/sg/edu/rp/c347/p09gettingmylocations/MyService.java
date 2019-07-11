package sg.edu.rp.c347.p09gettingmylocations;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {

    boolean started;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(this,"Service is running",Toast.LENGTH_SHORT).show();
        Log.d("Service", "Service created");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (started == false){
            started = true;
            Toast.makeText(this,"Service started",Toast.LENGTH_SHORT).show();
            Log.d("Service", "Service started");
        } else {
            Toast.makeText(this,"Service is still running",Toast.LENGTH_SHORT).show();
            Log.d("Service", "Service is still running");
        }
        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,"Service exited",Toast.LENGTH_SHORT).show();
        Log.d("Service" , "Service exited");
        super.onDestroy();
    }}
