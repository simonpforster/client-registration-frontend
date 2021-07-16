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
import uk.gov.hmrc.examplefrontend.model.{UserBusinessType, UserBusinessTypeForm}
import uk.gov.hmrc.examplefrontend.views.html.BusinessTypeInputPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject

class BusinessTypeController @Inject()(
                                        mcc: MessagesControllerComponents,
                                        businessTypeInputPage: BusinessTypeInputPage
                                      )
  extends FrontendController(mcc) {

  def InputBusinessType: Action[AnyContent] = Action { implicit request =>
    if (request.session.get(SessionKeys.crn).isDefined) {
      Redirect(routes.RegistrationController.home())
    } else {
      Ok(businessTypeInputPage(UserBusinessTypeForm.submitForm.fill(UserBusinessType(""))))
    }
  }

  def SubmitInputBusinessType: Action[AnyContent] = Action { implicit request =>
    if (request.session.get(SessionKeys.crn).isDefined) {
      Redirect(routes.RegistrationController.home())
    } else {
      UserBusinessTypeForm.submitForm.bindFromRequest().fold({ _ =>
        val form = UserBusinessTypeForm.submitForm.fill(UserBusinessType(""))
          .withError(UserClientProperties.businessType, ErrorMessages.businessTypeFormError)
        BadRequest(businessTypeInputPage(form))
      }, { formData =>
        Redirect(routes.PasswordController.InputPassword())
          .withSession(request.session + (SessionKeys.businessType -> formData.businessType))
      })
    }
  }

}
