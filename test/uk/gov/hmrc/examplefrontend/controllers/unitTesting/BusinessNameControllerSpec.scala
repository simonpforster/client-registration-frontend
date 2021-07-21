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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers._
import play.api.http.Status
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.examplefrontend.common.{SessionKeys, UrlKeys, UserClientProperties}
import uk.gov.hmrc.examplefrontend.controllers.BusinessNameController
import uk.gov.hmrc.examplefrontend.views.html.BusinessNameInputPage

import scala.concurrent.Future

class BusinessNameControllerSpec extends AbstractTest {

  val businessName: BusinessNameInputPage = app.injector.instanceOf[BusinessNameInputPage]

  private val controller = new BusinessNameController(
    mcc = Helpers.stubMessagesControllerComponents(),
    businessNameInputPage = businessName
  )

  private val fakeRequestGET: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.businessInputPath
  )
  private val fakeRequestPost: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "POST",
    path = UrlKeys.businessInputPath
  )

  private val businessNameValue: String = "TestNameOfBusiness"
  private val crnTest: String = "CRN00000001"
  private val htmlContentType: String = "text/html"

  "BusinessNameInput GET " should {
    "return 200" in {
      val result: Future[Result] = controller.InputBusinessName(isUpdate=false).apply(fakeRequestGET)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputBusinessName(isUpdate=false).apply(fakeRequestGET)
      contentType(result) shouldBe Some(htmlContentType)
    }
    "return redirect home" in {
      val result: Future[Result] = controller.InputBusinessName(isUpdate=false).apply(
        fakeRequestGET.withSession(SessionKeys.crn -> crnTest))
      status(result) shouldBe Status.SEE_OTHER
    }
    "pre populate the form with session" in {
      val result: Future[Result] = controller.InputBusinessName(isUpdate=false).apply(
        fakeRequestGET.withSession(SessionKeys.businessName -> businessNameValue))
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("business-name").`val` shouldBe businessNameValue
    }
  }

  "SubmitBusinessNameInput POST " should {
    "return redirect home" in {
      val result: Future[Result] = controller.SubmitInputBusinessName(isUpdate=false).apply(fakeRequestPost.withSession(
        SessionKeys.crn -> crnTest,
        SessionKeys.businessName -> businessNameValue))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return redirect Summary" in {
      val result: Future[Result] = controller.SubmitInputBusinessName(isUpdate=true).apply(fakeRequestPost.withFormUrlEncodedBody(
        UserClientProperties.businessName -> businessNameValue))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return Form with errors when updating with no info" in {
      val result: Future[Result] = controller.SubmitInputBusinessName(isUpdate=true).apply(fakeRequestPost.withFormUrlEncodedBody(
        UserClientProperties.businessName -> ""))
      status(result) shouldBe Status.BAD_REQUEST
    }
    "return 303" in {
      val result: Future[Result] = controller.SubmitInputBusinessName(isUpdate=false).apply(fakeRequestPost.withFormUrlEncodedBody(
        UserClientProperties.businessName -> businessNameValue))
      status(result) shouldBe Status.SEE_OTHER
    }
    "redirect with session" in {
      val result: Future[Result] = controller.SubmitInputBusinessName(isUpdate=false).apply(fakeRequestPost.withFormUrlEncodedBody(
        UserClientProperties.businessName -> businessNameValue))
      session(result).get(SessionKeys.businessName).get shouldBe businessNameValue
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputBusinessName(isUpdate=false).apply(fakeRequestPost.withFormUrlEncodedBody(
        UserClientProperties.businessName -> ""))
      status(result) shouldBe Status.BAD_REQUEST
    }
  }
}
