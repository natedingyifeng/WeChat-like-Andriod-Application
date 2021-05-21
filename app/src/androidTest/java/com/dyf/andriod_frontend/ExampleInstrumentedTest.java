package com.dyf.andriod_frontend;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dyf.andriod_frontend.utils.HttpRequest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.dyf.andriod_frontend", appContext.getPackageName());
    }

    /**
     * 测试HTTP GET请求是否正常
     */
    @Test
    public void httpGetTest() throws IOException {
        String getResp = HttpRequest.get("user/search?username=yihao_xu");
    }

}