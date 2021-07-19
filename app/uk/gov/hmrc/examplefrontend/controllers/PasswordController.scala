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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.examplefrontend.common.{ErrorMessages, SessionKeys, UserClientProperties}
import uk.gov.hmrc.examplefrontend.model.{UserPassword, UserPasswordForm}
import uk.gov.hmrc.examplefrontend.views.html.PasswordInputPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject

class PasswordController @Inject()(
                                    mcc: MessagesControllerComponents,
                                    passwordInputPage: PasswordInputPage
                                  )
  extends FrontendController(mcc) {

  def InputPassword: Action[AnyContent] = Action { implicit request =>
    if (request.session.get(SessionKeys.crn).isDefined) {
      Redirect(routes.RegistrationController.home())
    } else {
      Ok(passwordInputPage(UserPasswordForm.submitForm.fill(UserPassword("", ""))))
    }
  }

  def SubmitInputPassword: Action[AnyContent] = Action { implicit request =>
    if (request.session.get(SessionKeys.crn).isDefined) {
      Redirect(routes.RegistrationController.home())
    } else {
      UserPasswordForm.submitForm.bindFromRequest().fold({ formWithErrors =>
        BadRequest(passwordInputPage(formWithErrors))
      }, { formData =>
        formData.password match {
          case formData.passwordCheck => Redirect(routes.SummaryController.Summary()).withSession(request.session + (SessionKeys.password -> formData.password))
          case _ => BadRequest(passwordInputPage(UserPasswordForm.submitForm.fill(UserPassword("", "")).withError(UserClientProperties.passwordCheck, ErrorMessages.passwordMatch)))
        }
      })
    }
  }

}
