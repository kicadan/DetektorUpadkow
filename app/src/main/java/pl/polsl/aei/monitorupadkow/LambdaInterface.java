package pl.polsl.aei.monitorupadkow;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface LambdaInterface {
    /**
     * Invoke the Lambda function "AndroidBackendLambdaFunction".
     * The function name is the method name.
     */
    @LambdaFunction
    Response QualifyData(Request request);

    @LambdaFunction
    Response testFunction(Request request);

    @LambdaFunction
    Response transformAppData(Request request);
}
