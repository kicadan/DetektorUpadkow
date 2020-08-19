package pl.polsl.aei.monitorupadkow;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;
import com.google.gson.Gson;

public class AWSService {

    Context context;

    LambdaInvokerFactory factory;
    final LambdaInterface lambdaInterface;
    CognitoCachingCredentialsProvider cognitoProvider;

    public AWSService(Context context){
        this.context = context;

        //AWS settings
        // Create an instance of CognitoCachingCredentialsProvider
        cognitoProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:69b13ab2-6d8d-4866-970a-a673b7311666", // Identity pool ID
                Regions.US_EAST_1 // Region
        );
        // Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
        factory = new LambdaInvokerFactory(context,
                Regions.US_EAST_1, cognitoProvider);
        // Create the Lambda proxy object with a default Json data binder.
        // You can provide your own data binder by implementing
        // LambdaDataBinder.

        lambdaInterface = factory.build(LambdaInterface.class);
    }

    /*public void test(){
        Request request = new Request("John", "Doe");
        // The Lambda function invocation results in a network call.
        // Make sure it is not called from the main thread.
        new AsyncTask<Request, Void, Response>() {
            @Override
            protected Response doInBackground(Request... params) {
                // invoke "echo" method. In case it fails, it will throw a
                // LambdaFunctionException.
                try {
                    return lambdaInterface.testFunction(params[0]);
                } catch (LambdaFunctionException lfe) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Response result) {
                if (result == null) {
                    return;
                }

                // Do a toast
                System.out.println(result.getGreetings());
            }
        }.execute(request);
    }*/

    public void qualifyData(String JSON){
        Request request = new Request(new Gson().fromJson(JSON, Body.class));
        // The Lambda function invocation results in a network call.
        // Make sure it is not called from the main thread.
        new AsyncTask<Request, Void, Response>() {
            @Override
            protected Response doInBackground(Request... params) {
                // invoke "echo" method. In case it fails, it will throw a
                // LambdaFunctionException.
                try {
                    return lambdaInterface.transformAppData(params[0]);
                } catch (LambdaFunctionException lfe) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Response result) {
                if (result == null) {
                    return;
                }

                // Do a toast
                System.out.println(result.getBody().getMeasurements().getWearable()[0].getZ());
            }
        }.execute(request);
    }
}
