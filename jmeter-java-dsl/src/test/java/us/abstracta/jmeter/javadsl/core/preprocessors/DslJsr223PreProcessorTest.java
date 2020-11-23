package us.abstracta.jmeter.javadsl.core.preprocessors;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

import org.eclipse.jetty.http.MimeTypes;
import org.junit.jupiter.api.Test;
import us.abstracta.jmeter.javadsl.JmeterDsl;
import us.abstracta.jmeter.javadsl.JmeterDslTest;

public class DslJsr223PreProcessorTest extends JmeterDslTest {

  public static final String REQUEST_BODY = "put this in the body";

  @Test
  public void shouldUseBodyGeneratedByPreProcessorWhenTestPlanWithJsr223PreProcessor()
      throws Exception {
    testPlan(
        threadGroup(1, 1,
            JmeterDsl.
                httpSampler(wiremockUri)
                .post("${REQUEST_BODY}", MimeTypes.Type.TEXT_PLAIN)
                .children(
                    jsr223PreProcessor(
                        "vars.put('REQUEST_BODY', " + getClass().getName() + ".buildRequestBody())")
                )
        )
    ).run();
    wiremockServer.verify(postRequestedFor(anyUrl())
        .withRequestBody(equalTo(REQUEST_BODY)));
  }

  public static String buildRequestBody() {
    return REQUEST_BODY;
  }

  @Test
  public void shouldUseBodyGeneratedByPreProcessorWhenTestPlanWithJsr223PreProcessorWithLambdaScript()
      throws Exception {
    testPlan(
        threadGroup(1, 1,
            JmeterDsl.
                httpSampler(wiremockUri)
                .post("${REQUEST_BODY}", MimeTypes.Type.TEXT_PLAIN)
                .children(
                    jsr223PreProcessor(s -> s.vars.put("REQUEST_BODY", buildRequestBody()))
                )
        )
    ).run();
    wiremockServer.verify(postRequestedFor(anyUrl())
        .withRequestBody(equalTo(REQUEST_BODY)));
  }

  public static int count = 1;

    @Test
    //Testing body and header together to make sure they play nicely.
    public void shouldUseBodyAndHeaderGeneratedFunctionalRequest()
            throws Exception {
        testPlan(
                threadGroup(1, 2,
                        JmeterDsl.
                                httpSampler(wiremockUri)
                                .header("Header1", s -> "Value" + count)
                                .header("Header2", s -> "Value" + count)
                                .post(s -> "Body" + count, MimeTypes.Type.TEXT_PLAIN)
                        .children(jsr223PostProcessor(s -> count++))
                )
        ).run();
        wiremockServer.verify(postRequestedFor(anyUrl())
                .withHeader("Header1", equalTo("Value1"))
                .withHeader("Header2", equalTo("Value1"))
                .withRequestBody(equalTo("Body1")));
        wiremockServer.verify(postRequestedFor(anyUrl())
                .withHeader("Header1", equalTo("Value2"))
                .withHeader("Header2", equalTo("Value2"))
                .withRequestBody(equalTo("Body2")));
    }

}
