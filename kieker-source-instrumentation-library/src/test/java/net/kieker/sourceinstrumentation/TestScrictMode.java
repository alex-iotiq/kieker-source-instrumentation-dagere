package net.kieker.sourceinstrumentation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import net.kieker.sourceinstrumentation.instrument.InstrumentKiekerSource;
import net.kieker.sourceinstrumentation.util.TestConstants;

public class TestScrictMode {
   

   @Test
   public void testStrictMode() throws IOException {
      InstrumentationConfiguration configuration = getConfiguration(true);
      
      String changedSource = executeInstrumentation(configuration);
      MatcherAssert.assertThat(changedSource, Matchers.containsString("android.os.StrictMode.setThreadPolicy(policy);"));
   }
   
   @Test
   public void testNoStrictMode() throws IOException {
      InstrumentationConfiguration configuration = getConfiguration(false);
      
      String changedSource = executeInstrumentation(configuration);
      MatcherAssert.assertThat(changedSource, Matchers.not(Matchers.containsString("android.os.StrictMode.setThreadPolicy(policy);")));
   }

   private String executeInstrumentation(InstrumentationConfiguration configuration) throws IOException {
      TestConstants.CURRENT_FOLDER.mkdirs();

      File testFile = SourceInstrumentationTestUtil.copyResource("src/main/java/de/peass/C0_0.java", "/project_2/");

      InstrumentKiekerSource instrumenter = new InstrumentKiekerSource(configuration);
      instrumenter.instrument(testFile);

      TestSourceInstrumentation.testFileIsInstrumented(testFile, "public void de.peass.C0_0.method0()", "DurationRecord");
      String changedSource = FileUtils.readFileToString(testFile, StandardCharsets.UTF_8);
      return changedSource;
   }

   private InstrumentationConfiguration getConfiguration(boolean strictMode) {
      return new InstrumentationConfiguration(AllowedKiekerRecord.DURATION, true, true, false, null, null, true, 1000, false, strictMode);
   }
}
