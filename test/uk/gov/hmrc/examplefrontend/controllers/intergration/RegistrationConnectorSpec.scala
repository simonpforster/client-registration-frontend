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
import uk.gov.hmrc.examplefrontend.common.{UrlKeys, UserClientProperties}
import uk.gov.hmrc.examplefrontend.model.Client

class RegistrationConnectorSpec extends WireMockHelper with BeforeAndAfterEach with Matchers {

  val testClient: Client = Client(
    crn = "testCrn",
    name = user.name,
    businessName = user.businessName,
    contactNumber = user.contactNumber,
    propertyNumber = user.propertyNumber,
    postcode = user.postcode,
    businessType = user.businessType)

  override def beforeEach: Unit = {
    wireMockServer.start()
    WireMock.configureFor(wiremockHost, wiremockPort)
  }

  override def afterEach: Unit = {
    wireMockServer.stop()
  }

  "The User" should {
    "send proper request" in {
      wireMockServer.stubFor(post(urlMatching(UrlKeys.clients + UrlKeys.register)).willReturn(aResponse().withStatus(201)
        .withBody(
          s"""{
            |"${UserClientProperties.crn}": "${testClient.crn}",
						|"${UserClientProperties.name}": "${testClient.name}",
						|"${UserClientProperties.businessName}": "${testClient.businessName}",
						|"${UserClientProperties.contactNumber}": "${testClient.contactNumber}",
						|"${UserClientProperties.propertyNumber}": "${testClient.propertyNumber}",
						|"${UserClientProperties.postcode}": "${testClient.postcode}",
						|"${UserClientProperties.businessType}": "${testClient.businessType}"}
            |""".stripMargin)))
      val result: Option[Client] = await(connector.create(user))
      result shouldBe Some(testClient)
    }
  }
}
