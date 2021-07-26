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

import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers._
import play.api.http.Status
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.examplefrontend.common.{SessionKeys, UrlKeys, UserClientProperties}
import uk.gov.hmrc.examplefrontend.controllers.PasswordController
import uk.gov.hmrc.examplefrontend.views.html.PasswordInputPage

import scala.concurrent.Future

class PasswordControllerSpec extends AbstractTest {

  val password: PasswordInputPage = app.injector.instanceOf[PasswordInputPage]

  private val controller = new PasswordController(
    mcc = Helpers.stubMessagesControllerComponents(),
    passwordInputPage = password
  )

  private val fakeRequestGET: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.passwordInputPath
  )
  private val fakeRequestPOST: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "POST",
    path = UrlKeys.passwordInputPath
  )

  private val passwordValue: String = "TestPassword"
  private val htmlContentType: String = "text/html"

  "InputPassword GET" should {
    "return 200" in {
      val result = controller.InputPassword(isUpdate=false).apply(fakeRequestGET)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputPassword(isUpdate=false).apply(fakeRequestGET)
      contentType(result) shouldBe Some(htmlContentType)
    }
  }

  "SubmitInputPassword POST" should {
    "return 303" in {
      val result = controller.SubmitInputPassword(isUpdate=false).apply(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.password -> passwordValue,
        UserClientProperties.passwordCheck -> passwordValue))
      status(result) shouldBe Status.SEE_OTHER
    }
    "redirect with session" in {
      val result = controller.SubmitInputPassword(isUpdate=false).apply(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.password -> passwordValue,
        UserClientProperties.passwordCheck -> passwordValue))
      session(result).get(SessionKeys.password).get shouldBe passwordValue
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputPassword(isUpdate=false).apply(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.password -> "",
        UserClientProperties.passwordCheck -> passwordValue))
      status(result) shouldBe Status.BAD_REQUEST
    }
    "return Bad Request with passwords not matching" in {
      val result: Future[Result] = controller.SubmitInputPassword(isUpdate=false).apply(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.password -> "badPassword",
        UserClientProperties.passwordCheck -> passwordValue))
      status(result) shouldBe Status.BAD_REQUEST
    }

    "returns Bad Request and form with errors when there are no values" in {
      val result: Future[Result] = controller.SubmitInputPassword(isUpdate=false).apply(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.password -> "",
        UserClientProperties.passwordCheck -> ""))
      status(result) shouldBe Status.BAD_REQUEST
    }
  }
}