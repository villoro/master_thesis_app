package com.villoro.expensor_beta;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by Arnau on 20/01/2015.
 */
public class FullTestSuite extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(FullTestSuite.class)
                .includeAllPackagesUnderHere().build();
    }

    public FullTestSuite() {
        super();
    }
}
