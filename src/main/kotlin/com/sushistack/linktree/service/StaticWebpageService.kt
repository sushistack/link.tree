package com.sushistack.linktree.service

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.StaticWebpage
import com.sushistack.linktree.repository.publisher.StaticWebpageRepository
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class StaticWebpageService(private val staticWebpageRepository: StaticWebpageRepository) {

    fun findStaticWebpagesByProviderType(providerType: ServiceProviderType, seed: Long, fixedSize: Int): List<StaticWebpage> {
        val staticWebpages = staticWebpageRepository.findAllByProviderType(providerType = providerType)
        val extendedList = mutableListOf<StaticWebpage>()
        while (extendedList.size < fixedSize) {
            extendedList.addAll(staticWebpages)
        }

        val finalList = extendedList.take(fixedSize)

        val random = Random(seed)
        val shuffledList = finalList.toMutableList()
        for (i in shuffledList.indices) {
            val swapIndex = i + random.nextInt(shuffledList.size - i)
            val temp = shuffledList[i]
            shuffledList[i] = shuffledList[swapIndex]
            shuffledList[swapIndex] = temp
        }

        return shuffledList
    }

    fun findStaticWebpagesByOrderAndProviderType(order: Order, providerType: ServiceProviderType): List<StaticWebpage> =
        staticWebpageRepository.findAllByOrderAndProviderType(order, providerType)

    fun findStaticWebpagesByProviderType(providerType: ServiceProviderType): List<StaticWebpage> =
        staticWebpageRepository.findAllByProviderType(providerType = providerType)

}