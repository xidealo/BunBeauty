package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.CodeRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import java.util.*

class PremiumElementInteractor(val serviceRepository: ServiceRepository, val codeRepository: CodeRepository) {

    fun checkCode(code: String) {
        //проверка кода
        codeRepository.getByCode(code)
    }

    fun addSevenDayPremium(date: String): String {
        var sysdateLong = WorkWithTimeApi.getMillisecondsStringDateWithSeconds(date)
        //86400000 - day * 7 day
        sysdateLong += (86400000 * 7).toLong()
        return WorkWithTimeApi.getDateInFormatYMDHMS(Date(sysdateLong))
    }
}