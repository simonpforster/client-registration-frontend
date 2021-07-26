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
import uk.gov.hmrc.examplefrontend.controllers.NameController
import uk.gov.hmrc.examplefrontend.views.html.NameInputPage

import scala.concurrent.Future

class NameControllerSpec extends AbstractTest {

  val name: NameInputPage = app.injector.instanceOf[NameInputPage]

  private val controller = new NameController(
    mcc = Helpers.stubMessagesControllerComponents(),
    nameInputPage = name
  )

  private val fakeRequestGET: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.nameInputPath
  )
  private val fakeRequestPost: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "POST",
    path = UrlKeys.nameInputPath
  )

  private val nameValue: String = "TestFullName"

  "NameInput GET " should {
    "return 200" in {
      val result: Future[Result] = controller.InputName(isUpdate=false).apply(fakeRequestGET)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputName(isUpdate=false).apply(fakeRequestGET)
      contentType(result) shouldBe Some("text/html")
    }
    "pre populate the form with session" in {
      val result: Future[Result] = controller.InputName(isUpdate=false).apply(
        fakeRequestGET.withSession(SessionKeys.name -> nameValue))

      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("name").`val` shouldBe nameValue
    }
  }

  "SubmitNameInput POST " should {
    "return redirect Summary" in {
      val result: Future[Result] = controller.SubmitInputName(isUpdate=true).apply(fakeRequestPost.withFormUrlEncodedBody(
        UserClientProperties.name -> nameValue))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return form with errors if nothings inputted in form for update" in {
      val result: Future[Result] = controller.SubmitInputName(isUpdate=true).apply(fakeRequestPost.withFormUrlEncodedBody(
        UserClientProperties.name -> ""))
      status(result) shouldBe Status.BAD_REQUEST
    }
    "return 303" in {
      val result: Future[Result] = controller.SubmitInputName(isUpdate=false).apply(fakeRequestPost.withFormUrlEncodedBody(
        UserClientProperties.name -> nameValue))
      status(result) shouldBe Status.SEE_OTHER
    }
    "redirect with session" in {
      val result: Future[Result] = controller.SubmitInputName(isUpdate=false).apply(fakeRequestPost.withFormUrlEncodedBody(
        UserClientProperties.name -> nameValue))
      session(result).get(SessionKeys.name).get shouldBe nameValue
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputName(isUpdate=false).apply(fakeRequestPost.withFormUrlEncodedBody(
        UserClientProperties.name -> ""))
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

}