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

    public void qualifyData(StartActivity delegate, String JSON, ChooseActivity.MeasurementMode mode){
        Request request = new Gson().fromJson(JSON, Request.class);
        // The Lambda function invocation results in a network call.
        // Make sure it is not called from the main thread.
        new AsyncTask<Request, Void, Response>() {
            @Override
            protected Response doInBackground(Request... params) {
                // invoke "echo" method. In case it fails, it will throw a
                // LambdaFunctionException.
                Response response = new Response("ERR");
                try {
                    switch(mode){
                        case COMPLEX:
                            response = lambdaInterface.qualifyComplex(params[0]);
                            break;
                        case WEARABLE:
                            response = lambdaInterface.qualifyWearable(params[0]);
                            break;
                        case PHONE:
                            response = lambdaInterface.qualifyPhone(params[0]);
                            break;
                        case ECO:
                            response = lambdaInterface.qualifyEco(params[0]);
                    }
                    return response;
                } catch (LambdaFunctionException lfe) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Response result) {
                if (result == null) {
                    return;
                }

                if (result.getResult().toUpperCase().equals("1")){
                    delegate.notify("1");
                }
            }
        }.execute(request);
    }
}
