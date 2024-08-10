package com.sushistack.linktree.service

import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.StaticWebpage
import com.sushistack.linktree.repository.publisher.StaticWebpageRepository
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class StaticWebpageService(private val staticWebpageRepository: StaticWebpageRepository) {

    fun findStaticWebpagesByProviderType(providerType: ServiceProviderType, seed: Long, fixedSize: Int): List<StaticWebpage> {
        val staticWebpages = staticWebpageRepository.findAllByProviderType(providerType = providerType)

        val random = Random(seed)
        val step = if(staticWebpages.size < fixedSize) 1 else staticWebpages.size / fixedSize

        return (0 until fixedSize)
            .map { i -> staticWebpages[(random.nextInt(staticWebpages.size) + i * step) % staticWebpages.size] }
    }

    fun findStaticWebpagesByProviderType(providerType: ServiceProviderType): List<StaticWebpage> =
        staticWebpageRepository.findAllByProviderType(providerType = providerType)

}