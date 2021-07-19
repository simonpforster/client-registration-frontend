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
import uk.gov.hmrc.examplefrontend.controllers.PropertyController
import uk.gov.hmrc.examplefrontend.model.UserProperty
import uk.gov.hmrc.examplefrontend.views.html.PropertyInputPage

import scala.concurrent.Future

class PropertyControllerSpec extends AbstractTest {

  val property: PropertyInputPage = app.injector.instanceOf[PropertyInputPage]

  private val controller = new PropertyController(
    mcc = Helpers.stubMessagesControllerComponents(),
    propertyInputPage = property
  )

  private val fakeRequestGET: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.propertyInputPath
  )
  private val fakeRequestPOST: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = UrlKeys.propertyInputPath
  )

  private val propertyNumberValue: String = "TestPropertyNumber"
  private val postcodeValue: String = "TestAddress"
  private val crnTest: String = "CRN00000001"
  private val htmlContentType: String = "text/html"

  "InputProperty GET" should {
    "return 200" in {
      val result: Future[Result] = controller.InputProperty(fakeRequestGET)
      status(result) shouldBe Status.OK
    }
    "return html" in {
      val result: Future[Result] = controller.InputProperty(fakeRequestGET)
      contentType(result) shouldBe Some(htmlContentType)
    }
    "return redirect home" in {
      val result: Future[Result] = controller.InputProperty(
        fakeRequestGET.withSession(SessionKeys.crn -> crnTest))
      status(result) shouldBe Status.SEE_OTHER
    }
    "pre populate the form with session" in {
      val result: Future[Result] = controller.InputProperty(fakeRequestGET.withSession(
        SessionKeys.property -> UserProperty(propertyNumberValue, postcodeValue).encode()))
      val doc: Document = Jsoup.parse(contentAsString(result))
      doc.getElementById("property-number").`val` shouldBe propertyNumberValue
      doc.getElementById("postcode").`val` shouldBe postcodeValue
    }
  }

  "SubmitInputProperty POST" should {
    "return redirect home" in {
      val result: Future[Result] = controller.SubmitInputProperty(fakeRequestPOST.withSession(
        SessionKeys.crn -> crnTest,
        SessionKeys.property -> UserProperty(propertyNumberValue, postcodeValue).encode()))
      status(result) shouldBe Status.SEE_OTHER
    }
    "return 303" in {
      val result = controller.SubmitInputProperty(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.propertyNumber -> propertyNumberValue,
        UserClientProperties.postcode -> postcodeValue))
      status(result) shouldBe Status.SEE_OTHER
    }
    "redirect with session" in {
      val result = controller.SubmitInputProperty(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.propertyNumber -> propertyNumberValue,
        UserClientProperties.postcode -> postcodeValue))
      session(result).get(SessionKeys.property).get shouldBe UserProperty(propertyNumberValue, postcodeValue).encode()
    }
    "return Bad Request" in {
      val result: Future[Result] = controller.SubmitInputProperty(fakeRequestPOST.withFormUrlEncodedBody(
        UserClientProperties.propertyNumber -> "",
        UserClientProperties.postcode -> ""))
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

}