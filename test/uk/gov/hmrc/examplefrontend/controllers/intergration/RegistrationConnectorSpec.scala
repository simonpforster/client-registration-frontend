package uk.gov.hmrc.examplefrontend.controllers.intergration

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse,post,urlMatching}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import play.api.test.Helpers.{await,defaultAwaitTimeout}

class RegistrationConnectorSpec extends WireMockHelper with BeforeAndAfterEach  with  Matchers{

  override def beforeEach: Unit = {
    wireMockServer.start()
    WireMock.configureFor(wiremockHost,wiremockPort)
  }

  override def afterEach: Unit ={
    wireMockServer.stop()
  }

  "The User" should {
    "send proper request" in {
      wireMockServer.stubFor(post(urlMatching( s"/register")).willReturn(aResponse().withStatus(201)))
      val result = await(connector.create(user))
      result.get.crn should include("CRN")
    }
  }


}
