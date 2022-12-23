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
   
   static final String C0_0 = "src/main/java/de/peass/C0_0.java";
   static final String C0_0_FQN = "public void de.peass.C0_0.method0()";
   static final String C1_0 = "src/main/java/de/peass/C1_0.java";
   static final String C1_0_FQN = "public void de.peass.C1_0.method0()";
   static final String PROJECT = "/project_2_extends/";

   @Test
   public void testStrictMode() throws IOException {
      InstrumentationConfiguration configuration = getConfiguration(true);
      
      String changedSource = executeInstrumentation(configuration, C1_0, C1_0_FQN);
      MatcherAssert.assertThat(changedSource, Matchers.containsString("android.os.StrictMode.setThreadPolicy(policy);"));
   }

   @Test
   public void testStrictModeNoExtends() throws IOException {
      InstrumentationConfiguration configuration = getConfiguration(true);
      
      String changedSource = executeInstrumentation(configuration, C0_0, C0_0_FQN);
      MatcherAssert.assertThat(changedSource, Matchers.not(Matchers.containsString("android.os.StrictMode.setThreadPolicy(policy);")));
   }
   
   @Test
   public void testNoStrictMode() throws IOException {
      InstrumentationConfiguration configuration = getConfiguration(false);
      
      String changedSource = executeInstrumentation(configuration, C0_0, C0_0_FQN);
      MatcherAssert.assertThat(changedSource, Matchers.not(Matchers.containsString("android.os.StrictMode.setThreadPolicy(policy);")));
   }

   private String executeInstrumentation(InstrumentationConfiguration configuration, String name, String fqn) throws IOException {
      TestConstants.CURRENT_FOLDER.mkdirs();

      File testFile = SourceInstrumentationTestUtil.copyResource(name, PROJECT);

      InstrumentKiekerSource instrumenter = new InstrumentKiekerSource(configuration);
      instrumenter.instrument(testFile);

      TestSourceInstrumentation.testFileIsInstrumented(testFile, fqn, "DurationRecord");
      String changedSource = FileUtils.readFileToString(testFile, StandardCharsets.UTF_8);
      return changedSource;
   }

   private InstrumentationConfiguration getConfiguration(boolean strictMode) {
      return new InstrumentationConfiguration(AllowedKiekerRecord.DURATION, true, true, false, null, null, true, 1000, false, strictMode);
   }
}
