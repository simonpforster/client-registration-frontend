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

package uk.gov.hmrc.examplefrontend.controllers

import play.api.data.Form
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.examplefrontend.common.SessionKeys
import uk.gov.hmrc.examplefrontend.model.{UserContactNumber, UserContactNumberForm}
import uk.gov.hmrc.examplefrontend.views.html.ContactNumberInputPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject

class ContactNumberController @Inject()(
                                         mcc: MessagesControllerComponents,
                                         contactNumberInputPage: ContactNumberInputPage
                                       )
  extends FrontendController(mcc) {

  def InputContactNumber: Action[AnyContent] = Action { implicit request =>
    if (request.session.get(SessionKeys.crn).isDefined) {
      Redirect(routes.RegistrationController.home())
    } else {
      val form: Form[UserContactNumber] = request.session.get(SessionKeys.contactNumber).fold(UserContactNumberForm.submitForm.fill(UserContactNumber(""))) {
        contactNumber => UserContactNumberForm.submitForm.fill(UserContactNumber(contactNumber))
      }
      Ok(contactNumberInputPage(form))
    }
  }

  def SubmitInputContactNumber: Action[AnyContent] = Action { implicit request =>
    if (request.session.get(SessionKeys.crn).isDefined) {
      Redirect(routes.RegistrationController.home())
    } else {
      UserContactNumberForm.submitForm.bindFromRequest().fold({ formWithErrors =>
        BadRequest(contactNumberInputPage(formWithErrors))
      }, { formData =>
        Redirect(routes.PropertyController.InputProperty()).withSession(request.session + (SessionKeys.contactNumber -> formData.contact))
      })
    }
  }

}