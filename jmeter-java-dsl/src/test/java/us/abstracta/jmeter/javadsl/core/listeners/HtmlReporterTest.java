package us.abstracta.jmeter.javadsl.core.listeners;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static us.abstracta.jmeter.javadsl.JmeterDsl.htmlReporter;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpSampler;
import static us.abstracta.jmeter.javadsl.JmeterDsl.testPlan;
import static us.abstracta.jmeter.javadsl.JmeterDsl.threadGroup;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import us.abstracta.jmeter.javadsl.JmeterDslTest;

public class HtmlReporterTest extends JmeterDslTest {

  @Test
  public void shouldWriteHtmlReportWhenHtmlReporterAtTestPlan(@TempDir Path tempDir)
      throws IOException {
    testPlan(
        threadGroup(1, TEST_ITERATIONS,
            httpSampler(wiremockUri)
        ),
        htmlReporter(tempDir.toString())
    ).run();
    assertThat(tempDir.resolve("index.html").toFile().exists()).isTrue();
  }

  @Test
  public void shouldThrowExceptionWhenCreatingHtmlReporterWithNonEmptyDirectory(@TempDir Path tempDir) {
    assertThrows(DirectoryNotEmptyException.class, () -> {
      tempDir.resolve("test.txt").toFile().createNewFile();
      htmlReporter(tempDir.toString());
    });
  }

  @Test
  public void shouldThrowExceptionWhenCreatingHtmlReporterWithExistingFileAsDirectory(@TempDir Path tempDir) {
    assertThrows(FileAlreadyExistsException.class, () -> {
      Path filePath = tempDir.resolve("test.txt");
      filePath.toFile().createNewFile();
      htmlReporter(filePath.toString());
    });
  }

}
