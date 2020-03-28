package com.java.calumjohnston.utilities;

import matlabcontrol.*;

public class SPAMEvaluator {

    public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException {
        // create proxy
        MatlabProxyFactoryOptions options =
                new MatlabProxyFactoryOptions.Builder()
                        .setUsePreviouslyControlledSession(true)
                        .build();
        MatlabProxyFactory factory = new MatlabProxyFactory(options);
        MatlabProxy proxy = factory.getProxy();

        // call user-defined function (must be on the path)
        proxy.eval("addpath('C:\\Users\\Calum\\Documents\\MATLAB')");
        Object[] temp = proxy.returningEval("SPAM('test_image_encoded.png')", 1);
        Object[] temp2 = proxy.returningEval("SPAM('test_image.png')", 1);
        proxy.eval("rmpath('C:\\Users\\Calum\\Documents\\MATLAB')");

        // close connection
        proxy.disconnect();
    }
}