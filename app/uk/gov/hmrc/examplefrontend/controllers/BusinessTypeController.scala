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
import uk.gov.hmrc.examplefrontend.common.Utils.loggedInCheck
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

  def InputBusinessType(isUpdate: Boolean): Action[AnyContent] = Action { implicit request =>
    loggedInCheck{ () =>
      Ok(businessTypeInputPage(UserBusinessTypeForm.submitForm.fill(UserBusinessType("")),isUpdate))
    }
  }

  def SubmitInputBusinessType(isUpdate: Boolean): Action[AnyContent] = Action { implicit request =>
    loggedInCheck{ () =>
      UserBusinessTypeForm.submitForm.bindFromRequest().fold({ _ =>
        val form = UserBusinessTypeForm.submitForm.fill(UserBusinessType(""))
          .withError(UserClientProperties.businessType, ErrorMessages.businessTypeFormError)
        BadRequest(businessTypeInputPage(form,isUpdate))
      },formData => if(isUpdate){
        Redirect(routes.SummaryController.Summary(isUpdate)).withSession(request.session + (SessionKeys.businessType -> formData.businessType))
      }else {
        Redirect(routes.PasswordController.InputPassword(isUpdate))
          .withSession(request.session + (SessionKeys.businessType -> formData.businessType))
      })
    }
    }
}
