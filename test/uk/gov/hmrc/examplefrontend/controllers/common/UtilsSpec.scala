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

package uk.gov.hmrc.examplefrontend.controllers.common

import play.api.http.Status.{OK, SEE_OTHER}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import uk.gov.hmrc.examplefrontend.common.SessionKeys
import uk.gov.hmrc.examplefrontend.controllers.{BusinessNameController, SummaryController}
import uk.gov.hmrc.examplefrontend.controllers.unitTesting.AbstractTest

class UtilsSpec extends AbstractTest{

  val businessName: BusinessNameController = app.injector.instanceOf[BusinessNameController]
  val summary: SummaryController = app.injector.instanceOf[SummaryController]




  private val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
    method = "GET",
    path = "/"
  )

  "loggedInCheck()" should{
    "return SeeOther" when {
      "there is a crn in session(user logged in)" in {
        val result = businessName.InputBusinessName(false).apply(fakeRequest.withSession(SessionKeys.crn -> "testCrn"))
        status(result) shouldBe SEE_OTHER
      }
    }
    "return OK" when {
      "there is no crn in session(user not logged in)" in {
        val result = businessName.InputBusinessName(false).apply(fakeRequest)
        status(result) shouldBe OK
      }
    }
  }
  "loggedInCheckAsync()" should{
    "return SeeOther" when {
      "there is a crn in session(user logged in)" in {
        val result = summary.SummarySubmit.apply(fakeRequest.withSession(SessionKeys.crn -> "testCrn"))
        status(result) shouldBe SEE_OTHER
      }
    }
  }
}
