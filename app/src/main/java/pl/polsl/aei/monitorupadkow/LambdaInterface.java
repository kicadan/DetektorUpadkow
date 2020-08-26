package pl.polsl.aei.monitorupadkow;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface LambdaInterface {
    /**
     * Invoke the Lambda function "AndroidBackendLambdaFunction".
     * The function name is the method name.
     */
    @LambdaFunction
    Response qualifyComplex(Request request);

    @LambdaFunction
    Response qualifyWearable(Request request);

    @LambdaFunction
    Response qualifyPhone(Request request);

    @LambdaFunction
    Response qualifyEco(Request request);
}
