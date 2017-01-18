package com.example.user.readworld;

import com.robotium.solo.Solo;
import android.test.ActivityInstrumentationTestCase2;

public class ApplicationTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;
    public ApplicationTest()
    {
        super(MainActivity.class);
    }
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }
    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
