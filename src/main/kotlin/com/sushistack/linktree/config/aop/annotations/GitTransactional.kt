package com.sushistack.linktree.config.aop.annotations

import org.springframework.transaction.annotation.Transactional

@Transactional
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class GitTransactional
