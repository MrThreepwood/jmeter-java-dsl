package us.abstracta.jmeter.javadsl.core.listeners;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.SimpleDataWriter;
import us.abstracta.jmeter.javadsl.core.BaseTestElement;
import us.abstracta.jmeter.javadsl.core.MultiScopedTestElement;

/**
 * Allows to generate a result log file (JTL) with data for each sample for a test plan, thread
 * group or sampler, depending at what level of test plan is added.
 *
 * If jtlWriter is added at testPlan level it will log information about all samples in the test
 * plan, if added at thread group level it will only log samples for samplers contained within it,
 * if added as a sampler child, then only that sampler samples will be logged.
 *
 * By default this writer will use JMeter default JTL format (csv with label, elapsed time, status
 * code, etc). In the future additional methods may be added to configure different fields to
 * include, or use different format.
 *
 * See <a href="http://jmeter.apache.org/usermanual/listeners.html">JMeter listeners doc</a> for
 * more details on JTL format and settings.
 */
public class JtlWriter extends BaseTestElement implements MultiScopedTestElement {

  private final String jtlFile;

  public JtlWriter(String jtlFile) throws FileAlreadyExistsException {
    super("Simple Data Writer", SimpleDataWriter.class);
    this.jtlFile = jtlFile;
    if (new File(jtlFile).exists()) {
      throw new FileAlreadyExistsException(jtlFile);
    }
  }

  @Override
  public TestElement buildTestElement() {
    ResultCollector logger = new ResultCollector();
    logger.setFilename(jtlFile);
    return logger;
  }

}
