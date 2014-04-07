package br.com.izie.android.camera;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class LibraryActivityTest {

    // this is just a dummy test to verify that all dependencies
    // are set up correctly
    @Test public void testLibraryActivity() throws Exception {
        LibraryActivity activity = new LibraryActivity();
        activity.onCreate(null);

        assertThat(activity.isFinishing()).isFalse();
    }
}
