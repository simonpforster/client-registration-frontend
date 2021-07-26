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
import uk.gov.hmrc.examplefrontend.controllers.ContactNumberController
import uk.gov.hmrc.examplefrontend.views.html.ContactNumberInputPage

import scala.concurrent.Future

class ContactNumberControllerSpec extends AbstractTest {

  val contactNumber: ContactNumberInputPage = app.injector.instanceOf[ContactNumberInputPage]

  private val controller = new ContactNumberController(
    mcc = Helpers.stubMessagesControllerComponents(),
    contactNumberInputPage = contactNumber
  )

  private val fakeRequestGET: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.contactInputPath
  )
  private val fakeRequestPOST: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "POST",
    path = UrlKeys.contactInputPath
  )

  private val contactNumberValue: String = "07123456789"
  private val htmlContentType: String = "text/html"

  "InputContactNumber GET" should {
    "return 200" in {
      val result: Future[Result] = controller.InputContactNumber(isUpdate=false).apply(fakeRequestGET)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputContactNumber(isUpdate=false).apply(fakeRequestGET)
      contentType(result) shouldBe Some(htmlContentType)
    }
    "pre populate the form with session" in {
      val result: Future[Result] = controller.InputContactNumber(isUpdate=false).apply(
        fakeRequestGET.withSession(SessionKeys.contactNumber -> contactNumberValue))
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("contact-number").`val` shouldBe contactNumberValue
    }
  }

  "SubmitInputContactNumber POST" should {
    "return redirect Summary" in {
      val result: Future[Result] = controller.SubmitInputContactNumber(isUpdate=true).apply(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.contactNumber -> contactNumberValue))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result: Future[Result] = controller.SubmitInputContactNumber(isUpdate=false).apply(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.contactNumber -> contactNumberValue))
      status(result) shouldBe Status.SEE_OTHER
    }
    "redirect with session" in {
      val result: Future[Result] = controller.SubmitInputContactNumber(isUpdate=false).apply(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.contactNumber -> contactNumberValue))
      session(result).get(SessionKeys.contactNumber).getOrElse("") shouldBe contactNumberValue
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputContactNumber(isUpdate=false).apply(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.contactNumber -> ""))
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

}
