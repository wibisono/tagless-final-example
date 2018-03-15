package com.experiments.calvin.repositories.interpreters.inmemory

import java.util.UUID

import cats.Id
import cats.syntax.option._
import com.experiments.calvin.repositories.Order
import com.experiments.calvin.repositories.algebras.OrderRepositoryATM

trait InMemoryOrderRepositoryATM extends OrderRepositoryATM {

  type F[A] = Id[A]

  private var database = Map.empty[UUID, Order]

  override def put(order: Order): Order = {
    database = database + (order.id -> order)
    order
  }

  override def get(orderId: UUID): Option[Order] = database.get(orderId)

  override def remove(orderId: UUID): Option[Order] = {
    database.get(orderId).fold[Option[Order]](ifEmpty = None) { existingOrder =>
      database  = database - existingOrder.id
      existingOrder.some
    }
   }
}

object InMemoryOrderRepositoryATM extends  InMemoryOrderRepositoryATM