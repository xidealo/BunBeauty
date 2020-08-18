package com.bunbeauty.ideal.myapplication.clean_architecture.business.api

import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithTimeApi

class FiguringServicePointsApi {
     fun figureCreationDatePoints(creationDate: Long, coefficient: Float): Float {
            val creationDatePoints: Float

            val dateBonus = (creationDate - WorkWithTimeApi.getSysdateLong()) / (3600000 * 24) + 7
            if (dateBonus < 0) {
                creationDatePoints = 0f
            } else {
                creationDatePoints = dateBonus * coefficient / 7
            }

            return creationDatePoints
        }

        fun figureCostPoints(cost: Long, maxCost: Long, coefficient: Float): Float {
            return (1 - cost * 1f / maxCost) * coefficient
        }

        fun figureCountOfRatesPoints(countOfRates: Long, maxCountOfRates: Long, coefficient: Float): Float {
            return if (maxCountOfRates != 0L) {
                countOfRates / maxCountOfRates * coefficient
            } else {
                0f
            }
        }

        fun figureRatingPoints(rating: Float, coefficient: Float): Float {
            return rating * coefficient / 5
        }

}