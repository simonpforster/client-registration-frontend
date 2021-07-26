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
import uk.gov.hmrc.examplefrontend.controllers.BusinessTypeController
import uk.gov.hmrc.examplefrontend.views.html.BusinessTypeInputPage

import scala.concurrent.Future

class BusinessTypeControllerSpec extends AbstractTest {

  val businessType: BusinessTypeInputPage = app.injector.instanceOf[BusinessTypeInputPage]

  private val controller = new BusinessTypeController(
    mcc = Helpers.stubMessagesControllerComponents(),
    businessTypeInputPage = businessType
  )

  private val fakeRequestGET: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.businessTypeInputPath
  )
  private val fakeRequestPOST: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "Post",
    path = UrlKeys.businessTypeInputPath
  )

  private val businessTypeValue: String = "Private Limited"
  private val htmlContentType: String = "text/html"

  "InputBusinessType GET" should {
    "return 200" in {
      val result: Future[Result] = controller.InputBusinessType(isUpdate=false).apply(fakeRequestGET)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputBusinessType(isUpdate=false).apply(fakeRequestGET)
      contentType(result) shouldBe Some(htmlContentType)
    }
  }

  "SubmitInputBusinessType POST" should {
    "return redirect Summary" in {
      val result: Future[Result] = controller.SubmitInputBusinessType(isUpdate=true).apply(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.businessType -> businessTypeValue))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result = controller.SubmitInputBusinessType(isUpdate=false).apply(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.businessType -> businessTypeValue))
      status(result) shouldBe Status.SEE_OTHER
    }
    "redirect with session" in {
      val result = controller.SubmitInputBusinessType(isUpdate=false).apply(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.businessType -> businessTypeValue))
      session(result).get(SessionKeys.businessType).getOrElse("") shouldBe businessTypeValue
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputBusinessType(isUpdate=false).apply(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.businessType -> ""))
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

}