package com.example.chatease.data.local.datasource

import com.example.chatease.R
import com.example.chatease.domain.model.MembershipBenefitItem
import com.example.chatease.domain.model.enums.Membership

object MembershipBenefitsDataSource {

    fun getBenefits(membership: Membership): List<MembershipBenefitItem> {
        return when (membership) {
            Membership.FREE -> {
                listOf(
                    MembershipBenefitItem(text = R.string.free_benefit_1),
                    MembershipBenefitItem(text = R.string.free_benefit_2),
                    MembershipBenefitItem(text = R.string.free_benefit_3)
                )
            }

            Membership.PREMIUM -> {
                listOf(
                    MembershipBenefitItem(text = R.string.premium_benefit_1),
                    MembershipBenefitItem(text = R.string.premium_benefit_2),
                    MembershipBenefitItem(text = R.string.premium_benefit_3),
                    MembershipBenefitItem(text = R.string.premium_benefit_4)
                )
            }

            Membership.ULTRA -> {
                listOf(
                    MembershipBenefitItem(text = R.string.ultra_benefit_1),
                    MembershipBenefitItem(text = R.string.ultra_benefit_2),
                    MembershipBenefitItem(text = R.string.ultra_benefit_3),
                    MembershipBenefitItem(text = R.string.ultra_benefit_4),
                    MembershipBenefitItem(text = R.string.ultra_benefit_5),
                )
            }
        }
    }
}
