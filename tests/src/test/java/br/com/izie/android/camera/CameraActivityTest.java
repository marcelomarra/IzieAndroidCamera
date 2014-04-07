package br.com.izie.android.camera;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import br.com.izie.android.camera.library.CameraActivity;
import br.com.izie.android.camera.library.R;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class CameraActivityTest {

    private CameraActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = new CameraActivity();
        activity.setIntent(new Intent());
        activity.onCreate(null);
    }

    // this is just a dummy test to verify that all dependencies
    // are set up correctly
    @Test
    public void testLibraryActivity() throws Exception {
        activity.findViewById(R.id.button_capture).performClick();
        assertThat(activity.isFinishing()).isFalse();
    }
}
