/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.baole.oned.model

import android.text.TextUtils

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ServerTimestamp

import java.util.Date

/**
 * Model POJO for a rating.
 */
class Rating {

    var userId: String? = null
    var userName: String? = null
    var rating: Double = 0.toDouble()
    var text: String? = null
    @ServerTimestamp
    var timestamp: Date? = null

    constructor(user: FirebaseUser, rating: Double, text: String) {
        this.userId = user.uid
        this.userName = user.displayName
        if (TextUtils.isEmpty(this.userName)) {
            this.userName = user.email
        }

        this.rating = rating
        this.text = text
    }
}
