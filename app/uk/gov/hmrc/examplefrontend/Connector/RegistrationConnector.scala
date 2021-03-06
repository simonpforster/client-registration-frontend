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

package uk.gov.hmrc.examplefrontend.Connector

import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.examplefrontend.common.{UrlKeys, UserClientProperties}
import uk.gov.hmrc.examplefrontend.model.{Client, User}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class RegistrationConnector @Inject()(ws: WSClient, val controllerComponents: ControllerComponents, val ec: ExecutionContext) {

  def wspost(url: String, jsObject: JsObject): Future[WSResponse] = {
    ws.url(UrlKeys.backend + url).post(jsObject)
  }

  def create(user: User): Future[Option[Client]] = {
    val newUser: JsObject = Json.obj(
      UserClientProperties.name -> user.name,
      UserClientProperties.businessName -> user.businessName,
      UserClientProperties.contactNumber -> user.contactNumber,
      UserClientProperties.propertyNumber -> user.propertyNumber,
      UserClientProperties.postcode -> user.postcode,
      UserClientProperties.businessType -> user.businessType,
      UserClientProperties.password -> user.password
    )
    wspost(url = UrlKeys.register, jsObject = newUser).map { x => Json.fromJson[Client](x.json).asOpt }
  }
}
