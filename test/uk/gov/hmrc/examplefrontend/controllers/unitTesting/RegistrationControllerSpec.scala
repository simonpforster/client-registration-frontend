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

package uk.gov.hmrc.examplefrontend.controllers.unitTesting

import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.examplefrontend.common.{SessionKeys, UrlKeys}
import uk.gov.hmrc.examplefrontend.controllers.RegistrationController
import uk.gov.hmrc.examplefrontend.model.{User, UserProperty}

import scala.concurrent.Future


class RegistrationControllerSpec extends AbstractTest {

  private val fakeRequestName: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.nameInputPath
  )

  private val controller = new RegistrationController(
    mcc = Helpers.stubMessagesControllerComponents()
  )
  private val user: User = User(
    name = "TestFullName",
    businessName = "TestNameOfBusiness",
    contactNumber = "01111111111111",
    propertyNumber = "TestPropertyNumber",
    postcode = "TestAddress",
    businessType = "Private Limited",
    password = "TestPassword")
  private val fakeRequestSummary: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.summaryPath)
    .withSession(
      SessionKeys.name -> user.name,
      SessionKeys.businessName -> user.businessName,
      SessionKeys.contactNumber -> user.contactNumber,
      SessionKeys.property -> UserProperty(user.propertyNumber, user.postcode).encode(),
      SessionKeys.businessType -> user.businessType,
      SessionKeys.password -> user.password
    )

  private val crnTest: String = "CRN00000001"

  "Redirect home page" should {
    "return 303" in {
      val result: Future[Result] = controller.home(fakeRequestSummary)
      status(result) shouldBe Status.SEE_OTHER
    }
  }

  "Redirect Dashboard" should {
    "return 303" in {
      val result: Future[Result] = controller.dashboard(fakeRequestName.withSession(SessionKeys.crn -> crnTest))
      status(result) shouldBe Status.SEE_OTHER
    }
  }
}
