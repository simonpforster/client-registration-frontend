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

class RegistrationConnectorSpec extends WireMockHelper with BeforeAndAfterEach with Matchers {

  override def beforeEach: Unit = {
    wireMockServer.start()
    WireMock.configureFor(wiremockHost, wiremockPort)
  }

  override def afterEach: Unit = {
    wireMockServer.stop()
  }

  "The User" should {
    "send proper request" in {
      wireMockServer.stubFor(post(urlMatching(s"/register")).willReturn(aResponse().withStatus(201)))
      val result = await(connector.create(user))
      result.get.crn should include("CRN")
    }
  }


}
