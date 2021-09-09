/**
* This Source Code Form is subject to the terms of the Mozilla Public License, v.
* 2.0 with a Healthcare Disclaimer.
* A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
* be found under the top level directory, named LICENSE.
* If a copy of the MPL was not distributed with this file, You can obtain one at
* http://mozilla.org/MPL/2.0/.
* If a copy of the Healthcare Disclaimer was not distributed with this file, You
* can obtain one at the project website https://github.com/igia.
*
* Copyright (C) 2021-2022 Persistent Systems, Inc.
*/


package i2b2

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class FactSimulation extends Simulation {

    object Configuration {
        val users = Integer.getInteger("users", 1).toInt
        val rampUp = Integer.getInteger("rampUp", 1).toInt
        val repeat = Integer.getInteger("repeat", 1).toInt

        val prefix = System.getProperty("prefix")
        val username = System.getProperty("username")
        val password = System.getProperty("password")
        val baseUrl = System.getProperty("baseUrl")
    }

    object Add {
        val patientFeeder = csv("patient.csv").random

        val add = repeat(Configuration.repeat, "index") {
            feed(patientFeeder)
                .exec(_.set("prefix", Configuration.prefix))
                .doIfOrElse(session => session("index").as[Int] % 3 != 0) {
                    exec(
                        http("Lab fact")
                            .post("/api/facts")
                            .body(ElFileBody("lab_fact.json")).asJson
                            .check(status.is(201))
                            .check(jsonPath("$.encounterId"))

                    )
                } {
                    exec(
                        http("Medication fact")
                            .post("/api/facts")
                            .body(ElFileBody("medication_fact.json")).asJson
                            .check(status.is(201))
                            .check(jsonPath("$.modifiers[0].modifierCode"))
                    )
                }
        }
    }

    val scn = scenario("Add fact").exec(Add.add)

    val httpProtocol = http
        .baseUrl(Configuration.baseUrl)
        .basicAuth(Configuration.username, Configuration.password)
        .acceptHeader("application/json;charset=UTF-8")
        .userAgentHeader("Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20100101 Firefox/10.0")

    setUp(
        scn.inject(rampUsers(Configuration.users) during (Configuration.rampUp seconds))
            //        scn.inject(constantUsersPerSec(Configuration.users) during (Configuration.rampUp minutes))
            //            .throttle(
            //                reachRps(10) in (10 seconds),
            //                holdFor(2 minute),
            //                jumpToRps(3),
            //                holdFor(3 minute)
            //            )
            .protocols(httpProtocol)
    ).assertions(
        global.responseTime.max.lte(5000),
        global.responseTime.mean.lte(1000),
        global.successfulRequests.percent.gte(99),
    )
}
