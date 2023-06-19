package org.xapps.services.cartservice.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor
import org.springframework.stereotype.Repository
import org.xapps.services.cartservice.entities.Cart

@Repository
interface CartRepository : QueryByExampleExecutor<Cart>, CrudRepository<Cart, Long>