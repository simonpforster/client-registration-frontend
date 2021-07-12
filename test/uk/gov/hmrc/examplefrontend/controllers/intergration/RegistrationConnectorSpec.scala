/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.examplefrontend.controllers.intergration

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, urlMatching}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.examplefrontend.common.UrlKeys
import uk.gov.hmrc.examplefrontend.model.Client

class RegistrationConnectorSpec extends WireMockHelper with BeforeAndAfterEach with Matchers {

  val testClient: Client = Client(
    crn = "testCrn",
    name = "testName",
    businessName = "testBusiness",
    contactNumber = "testContact",
    propertyNumber = "12",
    postcode = "testPostcode",
    businessType = "testBusinessType")

  override def beforeEach: Unit = {
    wireMockServer.start()
    WireMock.configureFor(wiremockHost, wiremockPort)
  }

  override def afterEach: Unit = {
    wireMockServer.stop()
  }

  "The User" should {
    "send proper request" in {
      wireMockServer.stubFor(post(urlMatching(UrlKeys.register)).willReturn(aResponse().withStatus(201)
        .withBody(
          """{
            |"crn": "testCrn",
						|"name": "testName",
						|"businessName": "testBusinessName",
						|"contactNumber": "testContactNumber",
						|"propertyNumber": "testPropertyNumber",
						|"postcode": "testPostcode",
						|"businessType": "testBusinessType"}
            |""".stripMargin)))
      val result = await(connector.create(user))
      result.get.crn should include("testCrn")
    }
  }
}
