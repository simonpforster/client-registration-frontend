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

package uk.gov.hmrc.examplefrontend.model

import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import uk.gov.hmrc.examplefrontend.common.UserClientProperties


case class UserName(name: String)

object UserNameForm {
  val submitForm: Form[UserName] =
    Form(
      mapping(
        UserClientProperties.name -> nonEmptyText
      )(UserName.apply)(UserName.unapply)
    )
}

case class UserBusinessName(business: String)

object UserBusinessNameForm {
  val submitForm: Form[UserBusinessName] =
    Form(
      mapping(
        UserClientProperties.businessName -> nonEmptyText
      )(UserBusinessName.apply)(UserBusinessName.unapply)
    )
}

case class UserContactNumber(contact: String)

object UserContactNumberForm {
  val submitForm: Form[UserContactNumber] =
    Form(
      mapping(
        UserClientProperties.contactNumber -> nonEmptyText(minLength = 10)
      )(UserContactNumber.apply)(UserContactNumber.unapply)
    )
}

case class UserProperty(propertyNumber: String, postcode: String) {
  def encode(): String = {
    propertyNumber + "/" + postcode
  }
}

object UserProperty {
  def decode(x: String): UserProperty = {
    val (propertyNumber, postcode): (String, String) = x.split("/").toList match {
      case propertyNumber :: postcode :: _ => (propertyNumber, postcode)
    }
    UserProperty(propertyNumber = propertyNumber, postcode = postcode)
  }
}

object UserPropertyForm {
  val submitForm: Form[UserProperty] =
    Form(
      mapping(
        UserClientProperties.propertyNumber -> nonEmptyText,
        UserClientProperties.postcode -> nonEmptyText
      )(UserProperty.apply)(UserProperty.unapply)
    )
}

//(soleTrader:String, partnership:String, limitedCompany:String,other:String)
case class UserBusinessType(businessType: String)

object UserBusinessTypeForm {
  val submitForm: Form[UserBusinessType] =
    Form(
      mapping(
        UserClientProperties.businessType -> nonEmptyText,
      )(UserBusinessType.apply)(UserBusinessType.unapply)
    )
}


case class UserPassword(password: String, passwordCheck: String)


object UserPasswordForm {
  val submitForm: Form[UserPassword] =
    Form(
      mapping(
        UserClientProperties.password -> nonEmptyText,
        UserClientProperties.passwordCheck -> nonEmptyText
      )(UserPassword.apply)(UserPassword.unapply)
    )

}

case class User(name: String,
                businessName: String,
                contactNumber: String,
                propertyNumber: String,
                postcode: String,
                businessType: String,
                password: String)



